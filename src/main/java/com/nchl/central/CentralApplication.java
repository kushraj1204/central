package com.nchl.central;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class CentralApplication {

    public static void main(String[] args) {

        SpringApplication.run(CentralApplication.class, args);
    }

}
