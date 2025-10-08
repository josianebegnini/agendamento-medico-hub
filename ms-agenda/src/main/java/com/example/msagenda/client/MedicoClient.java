package com.example.msagenda.client;

import com.example.msagenda.dtos.MedicoResponseDTO;
import com.example.msagenda.client.fallback.MedicoClientFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
        name = "ms-medico",
        url = "http://localhost:8081/medicos",
        fallback = MedicoClientFallback.class
)
public interface MedicoClient {

    @GetMapping("/{id}")
    MedicoResponseDTO buscarPorId(@PathVariable("id") Long id);
}