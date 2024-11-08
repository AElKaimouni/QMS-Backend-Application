package com.example.qms;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Objects;

@SpringBootApplication
@EnableAsync
public class QMSApplication {

    public static void main(String[] args) {
        Dotenv dotenv= Dotenv.load();

        // Set the environment variables as system properties
        System.setProperty("spring.app.url", Objects.requireNonNull(dotenv.get("APP_URL")));

        System.setProperty("spring.datasource.url", Objects.requireNonNull(dotenv.get("DB_URL")));
        System.setProperty("spring.datasource.username", Objects.requireNonNull(dotenv.get("DB_USERNAME")));
        System.setProperty("spring.datasource.password", Objects.requireNonNull(dotenv.get("DB_PASSWORD")));

        String jwt_secret = dotenv.get("JWT_SECRET");
        String jwt_exp = dotenv.get("JWT_EXPIRATION");

        if(jwt_secret != null) System.setProperty("jwt.secret",jwt_secret);
        if(jwt_exp != null) System.setProperty("jwt.expiration", jwt_exp);

        System.setProperty("spring.mail.host", Objects.requireNonNull(dotenv.get("SMTP_HOST")));
        System.setProperty("spring.mail.port", Objects.requireNonNull(dotenv.get("SMTP_PORT")));
        System.setProperty("spring.mail.username", Objects.requireNonNull(dotenv.get("SMTP_USERNAME")));
        System.setProperty("spring.mail.password", Objects.requireNonNull(dotenv.get("SMTP_PASSWORD")));
        System.setProperty("spring.mail.from", Objects.requireNonNull(dotenv.get("SMTP_USERNAME")));

        String smtp_auth = dotenv.get("SMTP_AUTH");
        String smtp_starttls_enabled = dotenv.get("STARTTLS_ENABLE");

        if(smtp_auth != null) System.setProperty("spring.mail.properties.mail.smtp.auth", smtp_auth);
        if(smtp_starttls_enabled != null) System.setProperty("spring.mail.properties.mail.smtp.starttls.enable", smtp_starttls_enabled);

        //System.setProperty("spring.security.oauth2.client.registration.google.client-id", Objects.requireNonNull(dotenv.get("GOOGLE_CLIENT_ID")));
//        System.setProperty("spring.security.oauth2.client.registration.google.client-secret", Objects.requireNonNull(dotenv.get("GOOGLE_CLIENT_SECRET")));
//        System.setProperty("spring.security.oauth2.client.registration.google.redirect-uri",Objects.requireNonNull(dotenv.get("GOOGLE_REDIRECT_URI")));
        //System.setProperty("spring.security.oauth2.client.registration.google.redirect-scope",Objects.requireNonNull(dotenv.get("GOOGLE_SCOPE")));
        System.setProperty("logging.level.org.springframework.security","DEBUG");
        SpringApplication.run(QMSApplication.class, args);

    }


}