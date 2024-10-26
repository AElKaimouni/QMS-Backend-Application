package com.example.qms.utils;

import com.example.qms.user.User;
import io.github.cdimascio.dotenv.Dotenv;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.HashMap;
import java.util.Map;

@Service
public class EmailService {
    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private TemplateEngine templateEngine;

    @Value("${app.address}")
    private String appAddress;

    @Value("${app.port}")
    private String appPort;

    @Value("${spring.mail.from}")
    private String emailFrom;


    public void sendEmail(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        message.setFrom(this.emailFrom);
        mailSender.send(message);
    }

    public void sendEmailWithTempate(String to, String subject, String templateName, Map<String, Object> variables) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            Context context = new Context();
            context.setVariables(variables);

            String html = templateEngine.process(templateName, context);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(html, true);
            helper.setFrom(this.emailFrom);

            mailSender.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    @Async
    public void sendReservationArrivedEmail(String to, String name) {
        Map<String, Object> config = new HashMap<>();

        config.put("name", name);

        this.sendEmailWithTempate(to, "Reservation Turn Arrived!", "reservation-turn-arrived", config);
    }

    public void sendVerificationEmail(String to, String token) {
        String subject = "Account Verification";
        String confirmationUrl =  "http://" + appAddress + ":" + appPort + "/verify?token=" + token; // Update with your actual URL
        String message = "Please verify your account by clicking the link: " + confirmationUrl;
        sendEmail(to, subject, message);
    }

    public void sendPasswordResetEmail(User user, String resetToken) {
        String resetUrl = "http://"+appAddress + ":" + appPort +"/reset-password?token=" + resetToken;
        String subject = "Password Reset Request";
        String body = "To reset your password, click the following link: " + resetUrl;
        sendEmail(user.getEmail(), subject, body);
    }

}
