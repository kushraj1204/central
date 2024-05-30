package com.kush.shaihulud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class ShaiHulud {

    public static void main(String[] args) {

        SpringApplication.run(ShaiHulud.class, args);
    }

}
