package com.bysone.backend.service;

import com.bysone.backend.domain.*;
import com.bysone.backend.dto.request.SimulacionRequest;
import com.bysone.backend.dto.response.*;
import com.bysone.backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SimulacionService {

    private final PerfilInversionRepository perfilRepo;
    private final TipoPlazoRepository tipoPlazoRepo;
    private final DisclaimerRepository disclaimerRepo;
    private final SimulacionRepository simulacionRepo;

    /** Calcula proyección sin persistir — motor de simulación */
    @Transactional(readOnly = true)
    public SimulacionCalculadaResponse calcular(SimulacionRequest req) {
        PerfilInversion perfil = perfilRepo.findById(req.idPerfil())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Perfil no encontrado"));
        TipoPlazo tipoPlazo = tipoPlazoRepo.findById(req.idTipoPlazo())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tipo de plazo no encontrado"));

        BigDecimal rentMin = calcularRentabilidadPonderada(perfil, false);
        BigDecimal rentMax = calcularRentabilidadPonderada(perfil, true);

        BigDecimal aportePeriodico = req.aporteMensual() != null ? req.aporteMensual() : BigDecimal.ZERO;

        List<PeriodoProyeccionResponse> proyeccion = calcularProyeccion(
                req.valorInversionInicial(), aportePeriodico,
                req.plazo(), tipoPlazo.getFactorConversionDias(),
                rentMin, rentMax);

        ResumenSimulacionResponse resumen = calcularResumen(proyeccion, req.valorInversionInicial());

        return new SimulacionCalculadaResponse(
                perfil.getId(), perfil.getNombrePerfil(),
                req.valorInversionInicial(), aportePeriodico,
                req.plazo(), tipoPlazo.getNombrePlazo(),
                proyeccion, resumen);
    }

    /** Guarda la simulación recalculando server-side */
    @Transactional
    public SimulacionGuardadaResponse guardar(SimulacionRequest req, Usuario usuario) {
        SimulacionCalculadaResponse calculada = calcular(req);

        PerfilInversion perfil = perfilRepo.findById(req.idPerfil()).orElseThrow();
        TipoPlazo tipoPlazo = tipoPlazoRepo.findById(req.idTipoPlazo()).orElseThrow();
        BigDecimal aportePeriodico = req.aporteMensual() != null ? req.aporteMensual() : BigDecimal.ZERO;

        Simulacion sim = new Simulacion();
        sim.setUsuario(usuario);
        sim.setPerfilInversion(perfil);
        sim.setTipoPlazo(tipoPlazo);
        sim.setNombrePerfilSimulado(perfil.getNombrePerfil());
        sim.setValorInversionInicial(req.valorInversionInicial());
        sim.setValorInversionPeriodica(aportePeriodico);
        sim.setPlazoInversion(req.plazo());

        if (req.idDisclaimer() != null) {
            disclaimerRepo.findById(req.idDisclaimer()).ifPresent(sim::setDisclaimer);
        }

        List<DetalleProyeccionSimulacion> detalles = calculada.proyeccion().stream().map(p -> {
            DetalleProyeccionSimulacion d = new DetalleProyeccionSimulacion();
            d.setSimulacion(sim);
            d.setPeriodo(p.periodo());
            d.setValorProyectadoMinimo(p.valorProyectadoMinimo());
            d.setValorProyectadoMaximo(p.valorProyectadoMaximo());
            d.setValorProyectadoEsperado(p.valorProyectadoEsperado());
            d.setRentabilidadMinimaAplicada(p.rentabilidadMinimaAplicada());
            d.setRentabilidadMaximaAplicada(p.rentabilidadMaximaAplicada());
            return d;
        }).collect(Collectors.toList());

        sim.setDetalles(detalles);
        Simulacion saved = simulacionRepo.save(sim);

        return new SimulacionGuardadaResponse(
                saved.getId(), perfil.getId(), perfil.getNombrePerfil(),
                req.valorInversionInicial(), aportePeriodico, req.plazo(),
                tipoPlazo.getNombrePlazo(),
                saved.getFechaSimulacion().format(DateTimeFormatter.ISO_DATE_TIME),
                calculada.proyeccion(), calculada.resumen());
    }

    @Transactional(readOnly = true)
    public Page<SimulacionResumenResponse> listar(Usuario usuario, Pageable pageable) {
        return simulacionRepo.findByUsuarioId(usuario.getId(), pageable)
                .map(this::toResumenRow);
    }

    @Transactional(readOnly = true)
    public SimulacionGuardadaResponse detalle(Long id, Usuario usuario) {
        Simulacion sim = simulacionRepo.findByIdAndUsuarioId(id, usuario.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Simulación no encontrada"));
        return toGuardadaResponse(sim);
    }

    // ── Motor de cálculo ──────────────────────────────────────────────────────

    private List<PeriodoProyeccionResponse> calcularProyeccion(
            BigDecimal inicial, BigDecimal aportePeriodico, int plazo, int factorDias,
            BigDecimal rentMin, BigDecimal rentMax) {

        BigDecimal factorAnual = BigDecimal.valueOf(factorDias)
                .divide(BigDecimal.valueOf(365), 10, RoundingMode.HALF_UP);
        BigDecimal tasaMin = rentMin.divide(BigDecimal.valueOf(100), 10, RoundingMode.HALF_UP).multiply(factorAnual);
        BigDecimal tasaMax = rentMax.divide(BigDecimal.valueOf(100), 10, RoundingMode.HALF_UP).multiply(factorAnual);
        BigDecimal tasaMedia = tasaMin.add(tasaMax).divide(BigDecimal.valueOf(2), 10, RoundingMode.HALF_UP);

        List<PeriodoProyeccionResponse> resultado = new ArrayList<>();
        BigDecimal saldoMin = inicial;
        BigDecimal saldoMax = inicial;
        BigDecimal saldoEsp = inicial;

        for (int p = 1; p <= plazo; p++) {
            saldoMin = saldoMin.multiply(BigDecimal.ONE.add(tasaMin)).add(aportePeriodico).setScale(2, RoundingMode.HALF_UP);
            saldoMax = saldoMax.multiply(BigDecimal.ONE.add(tasaMax)).add(aportePeriodico).setScale(2, RoundingMode.HALF_UP);
            saldoEsp = saldoEsp.multiply(BigDecimal.ONE.add(tasaMedia)).add(aportePeriodico).setScale(2, RoundingMode.HALF_UP);
            resultado.add(new PeriodoProyeccionResponse(
                    p, saldoMin, saldoEsp, saldoMax,
                    rentMin.setScale(2, RoundingMode.HALF_UP),
                    rentMax.setScale(2, RoundingMode.HALF_UP)));
        }
        return resultado;
    }

    private BigDecimal calcularRentabilidadPonderada(PerfilInversion perfil, boolean usarMaxima) {
        return perfil.getPortafolios().stream()
                .map(a -> {
                    BigDecimal tasa = usarMaxima
                            ? a.getPortafolioInversion().getRentabilidadMaxima()
                            : a.getPortafolioInversion().getRentabilidadMinima();
                    return tasa.multiply(a.getPorcentaje())
                            .divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP);
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private ResumenSimulacionResponse calcularResumen(
            List<PeriodoProyeccionResponse> proyeccion, BigDecimal inicial) {
        if (proyeccion.isEmpty()) return new ResumenSimulacionResponse(BigDecimal.ZERO, BigDecimal.ZERO);
        BigDecimal valorFinal = proyeccion.get(proyeccion.size() - 1).valorProyectadoEsperado();
        BigDecimal ganancia = valorFinal.subtract(inicial).setScale(2, RoundingMode.HALF_UP);
        BigDecimal rendimiento = inicial.compareTo(BigDecimal.ZERO) > 0
                ? ganancia.divide(inicial, 4, RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(100)).setScale(2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;
        return new ResumenSimulacionResponse(ganancia, rendimiento);
    }

    private ResumenSimulacionResponse calcularResumenDesdeDetalles(Simulacion sim) {
        List<DetalleProyeccionSimulacion> detalles = sim.getDetalles();
        if (detalles.isEmpty()) return new ResumenSimulacionResponse(BigDecimal.ZERO, BigDecimal.ZERO);
        BigDecimal valorFinal = detalles.get(detalles.size() - 1).getValorProyectadoEsperado();
        BigDecimal ganancia = valorFinal.subtract(sim.getValorInversionInicial()).setScale(2, RoundingMode.HALF_UP);
        BigDecimal rendimiento = sim.getValorInversionInicial().compareTo(BigDecimal.ZERO) > 0
                ? ganancia.divide(sim.getValorInversionInicial(), 4, RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(100)).setScale(2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;
        return new ResumenSimulacionResponse(ganancia, rendimiento);
    }

    private SimulacionResumenResponse toResumenRow(Simulacion sim) {
        ResumenSimulacionResponse r = calcularResumenDesdeDetalles(sim);
        return new SimulacionResumenResponse(
                sim.getId(), sim.getNombrePerfilSimulado(),
                sim.getValorInversionInicial(), sim.getValorInversionPeriodica(), sim.getPlazoInversion(),
                sim.getTipoPlazo().getNombrePlazo(),
                r.gananciaEsperada(), r.rendimientoPorcentualTotal(),
                sim.getFechaSimulacion().format(DateTimeFormatter.ISO_DATE_TIME));
    }

    private List<PeriodoProyeccionResponse> toDetalleResponse(List<DetalleProyeccionSimulacion> detalles) {
        return detalles.stream().map(d -> new PeriodoProyeccionResponse(
                d.getPeriodo(),
                d.getValorProyectadoMinimo(),
                d.getValorProyectadoEsperado(),
                d.getValorProyectadoMaximo(),
                d.getRentabilidadMinimaAplicada(),
                d.getRentabilidadMaximaAplicada()
        )).collect(Collectors.toList());
    }

    private SimulacionGuardadaResponse toGuardadaResponse(Simulacion sim) {
        return new SimulacionGuardadaResponse(
                sim.getId(), sim.getPerfilInversion().getId(), sim.getNombrePerfilSimulado(),
                sim.getValorInversionInicial(), sim.getValorInversionPeriodica(), sim.getPlazoInversion(),
                sim.getTipoPlazo().getNombrePlazo(),
                sim.getFechaSimulacion().format(DateTimeFormatter.ISO_DATE_TIME),
                toDetalleResponse(sim.getDetalles()),
                calcularResumenDesdeDetalles(sim));
    }
}
