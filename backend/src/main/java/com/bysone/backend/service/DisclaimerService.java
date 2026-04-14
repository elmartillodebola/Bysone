package com.bysone.backend.service;

import com.bysone.backend.domain.Disclaimer;
import com.bysone.backend.dto.response.DisclaimerResponse;
import com.bysone.backend.repository.DisclaimerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class DisclaimerService {

    private final DisclaimerRepository disclaimerRepo;

    @Transactional(readOnly = true)
    public DisclaimerResponse obtenerVigente() {
        Disclaimer d = disclaimerRepo.findFirstByActivoTrueOrderByFechaVigenciaDesdeDesc()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No hay disclaimer vigente"));

        return new DisclaimerResponse(
                d.getId(), d.getTitulo(), d.getContenido(),
                d.getFechaVigenciaDesde() != null
                        ? d.getFechaVigenciaDesde().format(DateTimeFormatter.ISO_DATE_TIME) : null,
                d.getFechaVigenciaHasta() != null
                        ? d.getFechaVigenciaHasta().format(DateTimeFormatter.ISO_DATE_TIME) : null);
    }
}
