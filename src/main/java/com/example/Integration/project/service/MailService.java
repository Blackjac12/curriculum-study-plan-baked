package com.example.Integration.project.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class MailService {
    private final JavaMailSender mailSender;
    private final String from;

    public MailService(JavaMailSender mailSender, @Value("${spring.mail.username}") String from) {
        this.mailSender = mailSender;
        this.from = from;
    }

    public void sendVerificationEmail(String to, String code) {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(to);
        msg.setFrom(from);
        msg.setSubject("Tu código de verificación");
        msg.setText("Tu código de verificación es: " + code + "\nCaduca en 5 minutos.");
        mailSender.send(msg);
    }

    public void sendProvisionalPassword(String to, String provisionalPassword) {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(to);
        msg.setFrom(from);
        msg.setSubject("Tu contraseña provisional");
        msg.setText("Tu cuenta ha sido verificada. Tu contraseña provisional es: " + provisionalPassword
                + "\nPor favor cámbiala en tu perfil lo antes posible.");
        mailSender.send(msg);
    }
    //Envía la una contraseña provisional al ser cambiada por un administrador, incluyendo la nueva contraseña en el correo.
    public void sendAdminChangedPassword(String to, String newPassword) {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(to);
        msg.setFrom(from);
        msg.setSubject("Tu contraseña ha sido cambiada por un administrador");
        msg.setText("Tu contraseña ha sido cambiada por un administrador. Tu nueva contraseña es: " + newPassword
                + "\nPor favor cámbiala en tu perfil lo antes posible.");
        mailSender.send(msg);
    }
}

