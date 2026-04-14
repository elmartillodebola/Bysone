package com.bysone.backend.messaging;

import com.bysone.backend.config.RabbitMqConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificacionProducer {

    private final RabbitTemplate rabbitTemplate;

    @Async
    public void publicarCalibracionCompletada(String correo, String nombreUsuario, String nombrePerfil) {
        try {
            Map<String, String> evento = Map.of(
                    "correo", correo,
                    "nombreUsuario", nombreUsuario,
                    "nombrePerfil", nombrePerfil
            );
            rabbitTemplate.convertAndSend(
                    RabbitMqConfig.EXCHANGE_NOTIFICACIONES,
                    RabbitMqConfig.ROUTING_KEY_EMAIL,
                    evento);
            log.info("Evento calibración completada publicado para: {}", correo);
        } catch (Exception e) {
            log.error("Error publicando evento de calibración: {}", e.getMessage());
            // No propagar — la notificación no debe bloquear la transacción (CA-ENC-09)
        }
    }
}
