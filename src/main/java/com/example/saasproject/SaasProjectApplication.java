package com.example.saasproject;

import io.github.cdimascio.dotenv.Dotenv;
import lombok.extern.java.Log;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SaasProjectApplication {

    public static void main(String[] args) {
        Dotenv dotenv= Dotenv.load();
        // Set the environment variables as system properties
        System.setProperty("spring.datasource.url", dotenv.get("DB_URL"));
        System.setProperty("spring.datasource.username", dotenv.get("DB_USERNAME"));
        System.setProperty("spring.datasource.password", dotenv.get("DB_PASSWORD"));


        SpringApplication.run(SaasProjectApplication.class, args);

    }

}
