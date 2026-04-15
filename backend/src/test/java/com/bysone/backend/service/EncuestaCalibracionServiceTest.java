package com.bysone.backend.service;

import com.bysone.backend.domain.OpcionRespuestaCalibracion;
import com.bysone.backend.domain.PreguntaCalibracion;
import com.bysone.backend.dto.OpcionRespuestaRequest;
import com.bysone.backend.dto.PreguntaCalibracionRequest;
import com.bysone.backend.dto.PreguntaCalibracionResponse;
import com.bysone.backend.repository.OpcionRespuestaCalibracionRepository;
import com.bysone.backend.repository.PreguntaCalibracionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EncuestaCalibracionServiceTest {

    @Mock
    private PreguntaCalibracionRepository preguntaRepo;

    @Mock
    private OpcionRespuestaCalibracionRepository opcionRepo;

    @InjectMocks
    private EncuestaCalibracionService encuestaService;

    private PreguntaCalibracion pregunta;
    private List<OpcionRespuestaCalibracion> opciones;

    @BeforeEach
    void setUp() {
        pregunta = new PreguntaCalibracion();
        pregunta.setId(1L);
        pregunta.setTextoPregunta("¿Cuál es tu perfil de riesgo?");
        pregunta.setOrden(1);
        pregunta.setActiva(true);

        opciones = new ArrayList<>();
        
        OpcionRespuestaCalibracion opcion1 = new OpcionRespuestaCalibracion();
        opcion1.setId(1L);
        opcion1.setTextoOpcion("Conservador");
        opcion1.setPuntaje(1);
        opcion1.setOrden(1);
        opcion1.setPreguntaCalibracion(pregunta);
        opciones.add(opcion1);

        OpcionRespuestaCalibracion opcion2 = new OpcionRespuestaCalibracion();
        opcion2.setId(2L);
        opcion2.setTextoOpcion("Agresivo");
        opcion2.setPuntaje(3);
        opcion2.setOrden(2);
        opcion2.setPreguntaCalibracion(pregunta);
        opciones.add(opcion2);

        pregunta.setOpciones(opciones);
    }

    /**
     * Test 1: Crear una pregunta con opciones exitosamente (CA-PRG-01, CA-ORC-05)
     */
    @Test
    void testCrearPregunta_Success() {
        OpcionRespuestaRequest opcionReq1 = new OpcionRespuestaRequest("Conservador", 1, 1);
        OpcionRespuestaRequest opcionReq2 = new OpcionRespuestaRequest("Agresivo", 3, 2);
        PreguntaCalibracionRequest req = new PreguntaCalibracionRequest(
                "¿Cuál es tu perfil de riesgo?", 1, true, List.of(opcionReq1, opcionReq2)
        );

        when(preguntaRepo.findAll()).thenReturn(new ArrayList<>());
        when(preguntaRepo.save(any(PreguntaCalibracion.class))).thenReturn(pregunta);
        when(opcionRepo.saveAll(anyList())).thenReturn(opciones);

        PreguntaCalibracionResponse resultado = encuestaService.crearPregunta(req);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals("¿Cuál es tu perfil de riesgo?", resultado.getTextoPregunta());
        assertEquals(2, resultado.getOpciones().size());
        verify(preguntaRepo, times(1)).save(any(PreguntaCalibracion.class));
        verify(opcionRepo, times(1)).saveAll(anyList());
    }

    /**
     * Test 2: Validar que no se puede crear pregunta con orden duplicado (CA-PRG-03)
     */
    @Test
    void testCrearPregunta_OrdenDuplicado() {
        OpcionRespuestaRequest opcionReq1 = new OpcionRespuestaRequest("Conservador", 1, 1);
        OpcionRespuestaRequest opcionReq2 = new OpcionRespuestaRequest("Agresivo", 3, 2);
        PreguntaCalibracionRequest req = new PreguntaCalibracionRequest(
                "¿Cuál es tu perfil de riesgo?", 1, true, List.of(opcionReq1, opcionReq2)
        );

        when(preguntaRepo.findAll()).thenReturn(List.of(pregunta)); // Pregunta con orden 1 ya existe

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> encuestaService.crearPregunta(req));

        assertEquals("Ya existe una pregunta activa con ese orden (CA-PRG-03)", exception.getReason());
        verify(preguntaRepo, never()).save(any(PreguntaCalibracion.class));
    }

    /**
     * Test 3: Actualizar una pregunta exitosamente
     */
    @Test
    void testActualizarPregunta_Success() {
        OpcionRespuestaRequest opcionReq1 = new OpcionRespuestaRequest("Moderado", 2, 1);
        OpcionRespuestaRequest opcionReq2 = new OpcionRespuestaRequest("Conservador", 1, 2);
        PreguntaCalibracionRequest req = new PreguntaCalibracionRequest(
                "¿Cuál es tu perfil de inversión?", 1, true, List.of(opcionReq1, opcionReq2)
        );

        // req tiene orden=1, la pregunta también tiene orden=1 → el servicio no llama findAll()
        when(preguntaRepo.findById(1L)).thenReturn(Optional.of(pregunta));
        when(preguntaRepo.save(any(PreguntaCalibracion.class))).thenReturn(pregunta);
        when(opcionRepo.saveAll(anyList())).thenReturn(opciones);

        PreguntaCalibracionResponse resultado = encuestaService.actualizarPregunta(1L, req);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        verify(preguntaRepo, times(1)).findById(1L);
        verify(opcionRepo, times(1)).deleteAll(pregunta.getOpciones());
        verify(opcionRepo, times(1)).saveAll(anyList());
    }

    /**
     * Test 4: Desactivar una pregunta exitosamente (CA-PRG-04 soft delete)
     */
    @Test
    void testDesactivarPregunta_Success() {
        PreguntaCalibracion preguntaDesactivada = new PreguntaCalibracion();
        preguntaDesactivada.setId(1L);
        preguntaDesactivada.setTextoPregunta("¿Cuál es tu perfil de riesgo?");
        preguntaDesactivada.setOrden(1);
        preguntaDesactivada.setActiva(false);
        preguntaDesactivada.setOpciones(opciones);

        when(preguntaRepo.findById(1L)).thenReturn(Optional.of(pregunta));
        when(preguntaRepo.save(any(PreguntaCalibracion.class))).thenReturn(preguntaDesactivada);

        PreguntaCalibracionResponse resultado = encuestaService.desactivarPregunta(1L);

        assertNotNull(resultado);
        assertFalse(resultado.getActiva());
        verify(preguntaRepo, times(1)).save(any(PreguntaCalibracion.class));
    }

    /**
     * Test 5: Obtener preguntas activas ordenadas por orden (CA-PRG-02)
     */
    @Test
    void testObtenerPreguntasActivas_Success() {
        when(preguntaRepo.findByActivaTrueOrderByOrdenAsc()).thenReturn(List.of(pregunta));

        List<PreguntaCalibracionResponse> resultado = encuestaService.obtenerPreguntasActivas();

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals(1, resultado.get(0).getOrden());
        verify(preguntaRepo, times(1)).findByActivaTrueOrderByOrdenAsc();
    }
}
