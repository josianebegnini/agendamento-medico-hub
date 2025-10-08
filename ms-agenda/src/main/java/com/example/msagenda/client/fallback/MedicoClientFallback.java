package com.example.msagenda.client.fallback;

import com.example.msagenda.client.MedicoClient;
import com.example.msagenda.dtos.MedicoResponseDTO;
import org.springframework.stereotype.Component;

import javax.naming.ServiceUnavailableException;

@Component
public class MedicoClientFallback implements MedicoClient {

    @Override
    public MedicoResponseDTO buscarPorId(Long id) {
        // Retorna null, um objeto vazio ou lança exceção personalizada
        throw new ServiceUnavailableException("Serviço de médicos indisponível. Tente novamente mais tarde.");
    }
}