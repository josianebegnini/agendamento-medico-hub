package com.example.msagenda.client;
import com.example.msagenda.dtos.PacienteResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "ms-paciente", url = "http://localhost:8082/pacientes")
public interface PacienteClient {
    @GetMapping("/{id}")
    PacienteResponseDTO getPacienteById(@PathVariable Long id);
}