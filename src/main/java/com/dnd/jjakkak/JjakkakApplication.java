package com.dnd.jjakkak;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling // 스케줄링 설정
public class JjakkakApplication {

    public static void main(String[] args) {
        SpringApplication.run(JjakkakApplication.class, args);
    }

}
