package br.com.muita_conta.controller;


import br.com.muita_conta.service.email.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/ping")
public class Ping {

    private final EmailService emailService;

    @GetMapping
    public String ping() {
        return "PING";
    }
}
