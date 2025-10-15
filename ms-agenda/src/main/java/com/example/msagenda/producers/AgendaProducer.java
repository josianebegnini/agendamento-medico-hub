package com.example.msagenda.producers;

import com.example.msagenda.dtos.EmailPacienteDTO;
import org.springframework.stereotype.Service;
import com.example.msagenda.config.RabbitMQConfig;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;

@Service
public class AgendaProducer {
    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void enviarEmailPaciente(EmailPacienteDTO dto) {
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE_NAME,
                RabbitMQConfig.ROUTING_KEY,
                dto
        );
    }

}
