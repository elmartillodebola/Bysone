package com.bysone.backend.service;

import com.bysone.backend.domain.FormulaExposicion;
import com.bysone.backend.domain.PerfilInversion;
import com.bysone.backend.domain.PerfilPortafolio;
import com.bysone.backend.domain.PortafolioInversion;
import com.bysone.backend.dto.response.FormulaExposicionResponse;
import com.bysone.backend.dto.response.OpcionInversionResponse;
import com.bysone.backend.dto.response.PerfilInversionResponse;
import com.bysone.backend.dto.response.PortafolioResponse;
import com.bysone.backend.repository.FormulaExposicionRepository;
import com.bysone.backend.repository.PerfilInversionRepository;
import com.bysone.backend.repository.PerfilPortafolioRepository;
import com.bysone.backend.repository.PortafolioInversionRepository;
import com.bysone.backend.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminPerfilService {

    private final PerfilInversionRepository perfilRepo;
    private final PerfilPortafolioRepository perfilPortafolioRepo;
    private final FormulaExposicionRepository formulaRepo;
    private final PortafolioInversionRepository portafolioRepo;
    private final UsuarioRepository usuarioRepo;

    // ── Consulta ──────────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public List<PerfilInversionResponse> listar() {
        return perfilRepo.findAllWithPortafolios().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public PerfilInversionResponse obtener(Long id) {
        return toResponse(findOrThrow(id));
    }

    // ── CRUD básico ───────────────────────────────────────────────────────────

    @Transactional
    public PerfilInversionResponse crear(String nombre) {
        validarNombreUnico(nombre, null);
        PerfilInversion perfil = new PerfilInversion();
        perfil.setNombrePerfil(nombre.trim());
        return toResponse(perfilRepo.save(perfil));
    }

    @Transactional
    public PerfilInversionResponse renombrar(Long id, String nombre) {
        PerfilInversion perfil = findOrThrow(id);
        validarNombreUnico(nombre, perfil.getNombrePerfil());
        perfil.setNombrePerfil(nombre.trim());
        perfilRepo.save(perfil);
        return toResponse(perfilRepo.findWithAllById(id).orElseThrow());
    }

    @Transactional
    public void eliminar(Long id) {
        if (!perfilRepo.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Perfil no encontrado");
        }
        if (usuarioRepo.existsByPerfilInversionId(id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "No se puede eliminar: hay usuarios con este perfil asignado");
        }
        formulaRepo.deleteByPerfilId(id);
        perfilPortafolioRepo.deleteByPerfilId(id);
        perfilRepo.deleteById(id);
    }

    // ── Composición (portafolios + porcentaje) ────────────────────────────────

    @Transactional
    public PerfilInversionResponse actualizarComposicion(Long id, List<ComposicionItemRequest> items) {
        PerfilInversion perfil = findOrThrow(id);

        BigDecimal suma = items.stream()
                .map(ComposicionItemRequest::porcentaje)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        if (suma.compareTo(new BigDecimal("100.00")) != 0) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY,
                    "Los porcentajes deben sumar exactamente 100%. Suma actual: " + suma + "%");
        }

        // Soft-block: si existe fórmula de exposición para el par perfil-portafolio,
        // validar que el porcentaje asignado esté dentro del rango permitido.
        for (ComposicionItemRequest item : items) {
            formulaRepo.findByPerfilIdAndPortafolioId(id, item.idPortafolio()).ifPresent(formula -> {
                BigDecimal pct = item.porcentaje();
                if (pct.compareTo(formula.getUmbralPorcentajeMin()) < 0
                        || pct.compareTo(formula.getUmbralPorcentajeMax()) > 0) {
                    throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY,
                            String.format(
                                    "El porcentaje %.2f%% para el portafolio %d viola la fórmula de exposición (rango permitido: %.2f%% – %.2f%%)",
                                    pct, item.idPortafolio(),
                                    formula.getUmbralPorcentajeMin(), formula.getUmbralPorcentajeMax()));
                }
            });
        }

        perfilPortafolioRepo.deleteByPerfilId(id);

        for (ComposicionItemRequest item : items) {
            PortafolioInversion portafolio = portafolioRepo.findById(item.idPortafolio())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY,
                            "Portafolio no encontrado: " + item.idPortafolio()));
            PerfilPortafolio pp = new PerfilPortafolio();
            pp.setPerfilInversion(perfil);
            pp.setPortafolioInversion(portafolio);
            pp.setPorcentaje(item.porcentaje());
            perfilPortafolioRepo.save(pp);
        }

        return toResponse(perfilRepo.findWithAllById(id).orElseThrow());
    }

    // ── Fórmulas de exposición ────────────────────────────────────────────────

    @Transactional
    public PerfilInversionResponse actualizarFormulas(Long id, List<FormulaItemRequest> items) {
        PerfilInversion perfil = findOrThrow(id);

        for (FormulaItemRequest item : items) {
            if (item.umbralMin().compareTo(item.umbralMax()) > 0) {
                throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY,
                        "El umbral mínimo no puede ser mayor que el máximo para portafolio " + item.idPortafolio());
            }
        }

        formulaRepo.deleteByPerfilId(id);

        for (FormulaItemRequest item : items) {
            PortafolioInversion portafolio = portafolioRepo.findById(item.idPortafolio())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY,
                            "Portafolio no encontrado: " + item.idPortafolio()));
            FormulaExposicion f = new FormulaExposicion();
            f.setPerfilInversion(perfil);
            f.setPortafolioInversion(portafolio);
            f.setUmbralPorcentajeMin(item.umbralMin());
            f.setUmbralPorcentajeMax(item.umbralMax());
            formulaRepo.save(f);
        }

        return toResponse(perfilRepo.findWithAllById(id).orElseThrow());
    }

    // ── DTOs de entrada (records inline) ─────────────────────────────────────

    public record ComposicionItemRequest(Long idPortafolio, BigDecimal porcentaje) {}

    public record FormulaItemRequest(Long idPortafolio, BigDecimal umbralMin, BigDecimal umbralMax) {}

    // ── Helpers ───────────────────────────────────────────────────────────────

    private PerfilInversion findOrThrow(Long id) {
        return perfilRepo.findWithAllById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Perfil no encontrado"));
    }

    private void validarNombreUnico(String nombre, String nombreActual) {
        if (nombre == null || nombre.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El nombre del perfil es obligatorio");
        }
        boolean esMismoPerfil = nombreActual != null && nombreActual.equalsIgnoreCase(nombre.trim());
        if (!esMismoPerfil && perfilRepo.existsByNombrePerfilIgnoreCase(nombre.trim())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe un perfil con ese nombre");
        }
    }

    PerfilInversionResponse toResponse(PerfilInversion perfil) {
        List<PerfilPortafolio> asignaciones = perfil.getPortafolios();

        BigDecimal rentMin = calcularPonderada(asignaciones, false);
        BigDecimal rentMax = calcularPonderada(asignaciones, true);
        BigDecimal rentMedia = asignaciones.isEmpty()
                ? BigDecimal.ZERO
                : rentMin.add(rentMax).divide(BigDecimal.valueOf(2), 2, RoundingMode.HALF_UP);

        List<PortafolioResponse> portafolios = asignaciones.stream()
                .map(a -> toPortafolioResponse(a.getPortafolioInversion(), a.getPorcentaje()))
                .collect(Collectors.toList());

        List<FormulaExposicionResponse> formulas = perfil.getFormulasExposicion().stream()
                .map(f -> new FormulaExposicionResponse(
                        f.getPortafolioInversion().getId(),
                        f.getUmbralPorcentajeMin(), f.getUmbralPorcentajeMax()))
                .collect(Collectors.toList());

        return new PerfilInversionResponse(
                perfil.getId(), perfil.getNombrePerfil(),
                rentMin, rentMedia, rentMax, portafolios, formulas);
    }

    private BigDecimal calcularPonderada(List<PerfilPortafolio> asignaciones, boolean usarMaxima) {
        if (asignaciones.isEmpty()) return BigDecimal.ZERO;
        return asignaciones.stream()
                .map(a -> {
                    BigDecimal tasa = usarMaxima
                            ? a.getPortafolioInversion().getRentabilidadMaxima()
                            : a.getPortafolioInversion().getRentabilidadMinima();
                    return tasa.multiply(a.getPorcentaje())
                            .divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP);
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);
    }

    private PortafolioResponse toPortafolioResponse(PortafolioInversion p, BigDecimal porcentaje) {
        List<OpcionInversionResponse> opciones = p.getOpciones().stream()
                .map(o -> new OpcionInversionResponse(o.getId(), o.getNombreOpcion(), o.getDescripcionOpcion()))
                .collect(Collectors.toList());
        return new PortafolioResponse(
                p.getId(), p.getNombrePortafolio(), p.getDescripcion(),
                p.getRentabilidadMinima(), p.getRentabilidadMaxima(), porcentaje, opciones);
    }
}
