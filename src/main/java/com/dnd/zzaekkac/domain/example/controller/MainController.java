package com.dnd.zzaekkac.domain.example.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 테스트용 컨트롤러입니다.
 *
 * @author 정승조
 * @version 2024. 07. 20.
 */

@RestController
public class MainController {

    @GetMapping("/")
    public String main() {
        return "Hello, World";
    }
}
