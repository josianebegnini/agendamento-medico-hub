package com.example.msagenda.client.fallback;

import com.example.msagenda.client.MedicoClient;
import com.example.msagenda.dtos.MedicoResponseDTO;
import com.example.msagenda.exceptions.ServiceUnavailableException;
import org.springframework.stereotype.Component;


@Component
public class MedicoClientFallback implements MedicoClient {

    @Override
    public MedicoResponseDTO buscarPorId(Long id) {
        // Retorna null, um objeto vazio ou lança exceção personalizada
        throw new ServiceUnavailableException("Serviço de médicos indisponível. Tente novamente mais tarde.");
    }
}