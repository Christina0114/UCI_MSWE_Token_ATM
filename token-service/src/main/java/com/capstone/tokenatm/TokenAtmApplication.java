package com.capstone.tokenatm;

import com.capstone.tokenatm.service.EarnService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.io.IOException;

@Configuration
@EnableAsync
@EnableScheduling
@SpringBootApplication
public class TokenAtmApplication {

    public static void main(String[] args) throws JSONException, IOException {
        ConfigurableApplicationContext context = SpringApplication.run(TokenAtmApplication.class, args);
        context.getBean(EarnService.class).syncTokensOnDeadline();
    }
}