package com.sgn.srv;

import com.sgn.ent.Notificacion;
import com.sgn.repo.NotificacionRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class MailSrv {

    private static final Logger log = LoggerFactory.getLogger(MailSrv.class);

    private final JavaMailSender sender;
    private final NotificacionRepo notifRepo;

    @Value("${sgn.mail.enabled:false}")
    private boolean enabled;

    @Value("${sgn.mail.from:notificaciones@sgn.local}")
    private String from;

    public MailSrv(JavaMailSender sender, NotificacionRepo notifRepo) {
        this.sender = sender;
        this.notifRepo = notifRepo;
    }

    public void enviar(String dest, String asunto, String cuerpo) {
        if (dest == null || dest.isBlank()) return;
        var n = new Notificacion();
        n.setDest(dest);
        n.setAsunto(asunto);
        n.setCuerpo(cuerpo);
        n.setFec(LocalDateTime.now());
        if (!enabled) {
            log.info("[MAIL DEV] Para: {} | {} | {}", dest, asunto, cuerpo);
            n.setEnviado(false);
            n.setError("SGN_MAIL_ENABLED=false — simulado en log");
            notifRepo.save(n);
            return;
        }
        try {
            var msg = new SimpleMailMessage();
            msg.setFrom(from);
            msg.setTo(dest);
            msg.setSubject(asunto);
            msg.setText(cuerpo);
            sender.send(msg);
            n.setEnviado(true);
        } catch (Exception e) {
            log.warn("No se pudo enviar correo a {}: {}", dest, e.getMessage());
            n.setEnviado(false);
            n.setError(e.getMessage());
        }
        notifRepo.save(n);
    }

    public void notaAlumno(String mailAl, String mailTut, String mat, String det) {
        var txt = "SGN - Nota registrada/modificada\nMateria: " + mat + "\n" + det
            + "\n\nEscuela Ing. E. Latzina (ET35)";
        enviar(mailAl, "SGN - Nota " + mat, txt);
        if (mailTut != null && !mailTut.isBlank()) {
            enviar(mailTut, "SGN - Nota de su hijo/a " + mat, txt);
        }
    }

    public void solOficina(String mailOf, String prof, String mat, String motivo, String actual, String nuevo) {
        var txt = "Solicitud de cambio de nota\nProfesor: " + prof + "\nMateria: " + mat
            + "\nMotivo: " + motivo + "\nActual: " + actual + " -> Propuesta: " + nuevo;
        enviar(mailOf, "SGN - Solicitud cambio nota", txt);
    }

    public void passwordTemporal(String destino, String nombre, String password) {

    String txt =
        "Hola " + nombre + ",\n\n" +
        "Se creó su usuario en el Sistema de Gestión de Notas (SGN).\n\n" +
        "Contraseña temporal: " + password + "\n\n" +
        "Al ingresar por primera vez deberá cambiar esta contraseña.\n\n" +
        "Escuela Ing. E. Latzina (ET35)";

    enviar(
        destino,
        "SGN - Datos de acceso",
        txt
    );
}
}
