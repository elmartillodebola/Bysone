package com.bysone.backend.messaging;

import com.bysone.backend.config.RabbitMqConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificacionConsumer {

    private final JavaMailSender mailSender;

    @RabbitListener(queues = RabbitMqConfig.QUEUE_EMAIL)
    public void procesarNotificacion(Map<String, String> evento) {
        try {
            String correo = evento.get("correo");
            String nombre = evento.get("nombreUsuario");
            String perfil = evento.get("nombrePerfil");

            SimpleMailMessage mensaje = new SimpleMailMessage();
            mensaje.setTo(correo);
            mensaje.setSubject("¡Tu perfil de inversión ha sido asignado!");
            mensaje.setText(String.format(
                    "Hola %s,\n\n" +
                    "Has completado tu calibración y se te ha asignado el perfil: %s.\n\n" +
                    "Ingresa a la plataforma para ver tu simulación personalizada.\n\n" +
                    "Mi Portafolio Inteligente — Hackaton 2026 Protección",
                    nombre, perfil));

            mailSender.send(mensaje);
            log.info("Email enviado a: {}", correo);
        } catch (Exception e) {
            log.error("Error enviando email de notificación: {}", e.getMessage());
        }
    }
}
