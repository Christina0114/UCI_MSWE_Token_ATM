package com.capstone.tokenatm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class TokenAtmApplication {

    public static void main(String[] args) {
        SpringApplication.run(TokenAtmApplication.class, args);
    }
}
