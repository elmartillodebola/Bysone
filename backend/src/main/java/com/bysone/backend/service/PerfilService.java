package com.bysone.backend.service;

import com.bysone.backend.domain.FormulaExposicion;
import com.bysone.backend.domain.PerfilInversion;
import com.bysone.backend.domain.PerfilPortafolio;
import com.bysone.backend.domain.PortafolioInversion;
import com.bysone.backend.dto.response.*;
import com.bysone.backend.repository.PerfilInversionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PerfilService {

    private final PerfilInversionRepository perfilRepo;

    @Transactional(readOnly = true)
    public List<PerfilInversionResponse> listarPerfiles() {
        return perfilRepo.findAllWithPortafolios().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    private PerfilInversionResponse toResponse(PerfilInversion perfil) {
        List<PerfilPortafolio> asignaciones = perfil.getPortafolios();

        BigDecimal rentMin = calcularRentabilidadPonderada(asignaciones, false);
        BigDecimal rentMax = calcularRentabilidadPonderada(asignaciones, true);
        BigDecimal rentMedia = rentMin.add(rentMax).divide(BigDecimal.valueOf(2), 2, RoundingMode.HALF_UP);

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

    private BigDecimal calcularRentabilidadPonderada(List<PerfilPortafolio> asignaciones, boolean usarMaxima) {
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
