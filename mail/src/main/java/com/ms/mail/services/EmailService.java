package com.ms.mail.services;

import com.ms.mail.enums.StatusEmail;
import com.ms.mail.models.Email;
import com.ms.mail.models.EmailStatusResponse;
import com.ms.mail.repositories.EmailRepository;
import jakarta.transaction.Transactional;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class EmailService {
    private final EmailRepository emailRepository;
    private final JavaMailSender javaMailSender;
    private final RabbitTemplate rabbitTemplate;

    @Value("${broker.queue.email.response}")
    private String emailResponseQueue;

    public EmailService(EmailRepository emailRepository, JavaMailSender javaMailSender, RabbitTemplate rabbitTemplate) {
        this.emailRepository = emailRepository;
        this.javaMailSender = javaMailSender;
        this.rabbitTemplate = rabbitTemplate;
    }

    @Value(value = "${spring.mail.username}")
    private String emailFrom;

    @Transactional
    public Email sendEmail(Email email){
        try {
            email.setSendDateEmail(LocalDateTime.now());
            email.setEmailFromn(emailFrom);

            SimpleMailMessage message =  new SimpleMailMessage();
            message.setTo(email.getEmailTo());
            message.setSubject(email.getSubject());
            message.setText(email.getText());
            javaMailSender.send(message);

            email.setStatusEmail(StatusEmail.SENT);
        }catch (MailException e){
            email.setStatusEmail(StatusEmail.ERROR);
        }finally {

           email =  emailRepository.save(email);

            EmailStatusResponse respose =  new EmailStatusResponse(
                    email.getId(),
                    email.getEmailTo(),
                    email.getStatusEmail().toString(),
                    email.getUserId()
            );

            rabbitTemplate.convertAndSend(emailResponseQueue,respose);



            return email;
        }
    }


}
