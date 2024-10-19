package com.example.qms.utils;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamSource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private TemplateEngine templateEngine;

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

    @Async
    public void sendReservationEmail(
            String to,
            String token,
            String queueName,
            String clientName,
            int yourPosition,
            int currentPosition,
            Date estimatedDate
    ) throws Exception {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setTo(to);
        helper.setSubject("Reservation ticket");

        // Generate the QR Code image
        InputStreamSource qrCodeSource = QRCodeGenerator.generateReservationQRImage(token);

        // Set up Thymeleaf context
        Context context = new Context();

        context.setVariable("clientName", clientName);
        context.setVariable("queueName", queueName);
        context.setVariable("yourPosition", yourPosition);
        context.setVariable("currentPosition", currentPosition);
        context.setVariable("estimatedDate", estimatedDate);

        String htmlContent = templateEngine.process("reservation-email", context);

        helper.setText(htmlContent, true);
        // Add QR Code as an embedded image
        helper.addInline("qrCodeImage", qrCodeSource, "image/png");

        // Send the email
        mailSender.send(message);
    }
}
