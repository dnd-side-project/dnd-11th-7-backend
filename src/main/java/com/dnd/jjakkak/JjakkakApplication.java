package com.dnd.jjakkak;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class JjakkakApplication {

    public static void main(String[] args) {
        SpringApplication.run(JjakkakApplication.class, args);
    }

}
