package com.ms.mail.consumers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ms.mail.config.RabbitMQConfig;
import com.ms.mail.dtos.EmailPacienteDTO;
import com.ms.mail.services.EmailService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class EmailConsumer {

    private final EmailService emailService;
    private final ObjectMapper objectMapper;

    public EmailConsumer(EmailService emailService, ObjectMapper objectMapper) {
        this.emailService = emailService;
        this.objectMapper = objectMapper;
    }

    @RabbitListener(queues = RabbitMQConfig.QUEUE_NAME)
    public void consumirMensagem(EmailPacienteDTO dto) {
        try {
            System.out.println("📩 Mensagem recebida da fila: " + dto);

            // Aqui você chama seu serviço para enviar o e-mail
            emailService.enviarEmail(dto);

            System.out.println("✅ E-mail enviado com sucesso para: " + dto.getEmail());
        } catch (Exception e) {
            System.err.println("❌ Erro ao processar mensagem: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
