package com.api.authserver.producers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.api.authserver.domain.dtos.user.events.UserCreatedEventDTO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j 
@Component
@RequiredArgsConstructor 
public class UserEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${app.kafka.topics.user-created}")
    private String userCreatedTopic;

    public void sendUserCreatedEvent(UserCreatedEventDTO event) {
        // Usa o ID do usuário como mensagem Key para garantir ordenação e distribuição correta por partição
        kafkaTemplate.send(userCreatedTopic, String.valueOf(event.id()), event)
                .whenComplete((result, ex) -> {
                    if (ex == null) {
                        log.info("Evento UserCreatedEvent enviado com sucesso. Offset: [{}]", 
                                result.getRecordMetadata().offset());
                    } else {
                        log.error("Falha ao enviar evento UserCreatedEvent para o id: {}", event.id(), ex);
                    }
                });
    }
}