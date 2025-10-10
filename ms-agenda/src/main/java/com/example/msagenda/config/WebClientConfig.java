package com.example.msagenda.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient pacienteWebClient() {
        return WebClient.builder()
                .baseUrl("http://ms-paciente:8082") // URL base do serviço paciente
                .build();
    }

    // Se quiser outro WebClient para médicos
    @Bean
    public WebClient medicoWebClient() {
        return WebClient.builder()
                .baseUrl("http://ms-medico:8081") // URL base do serviço médico
                .build();
    }
}