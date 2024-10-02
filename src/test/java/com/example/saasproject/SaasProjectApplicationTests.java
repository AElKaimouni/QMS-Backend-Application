package com.example.saasproject;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;

@SpringBootTest
class SaasProjectApplicationTests {
    @Autowired
    private Environment env;

    @Test
    void contextLoads() {
        System.out.println(env.getProperty("spring.datasource.url"));
    }

}
