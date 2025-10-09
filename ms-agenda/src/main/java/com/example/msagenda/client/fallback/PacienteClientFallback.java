package com.example.msagenda.client.fallback;

import com.example.msagenda.client.PacienteClient;
import com.example.msagenda.dtos.PacienteResponseDTO;
import com.example.msagenda.exceptions.ServiceUnavailableException;
import org.springframework.stereotype.Component;

@Component
public class PacienteClientFallback implements PacienteClient {

    @Override
    public PacienteResponseDTO getPacienteById(Long id) {
        // Lança exceção customizada informando que o serviço está indisponível
        throw new ServiceUnavailableException("Serviço de pacientes indisponível. Tente novamente mais tarde.");
    }
}