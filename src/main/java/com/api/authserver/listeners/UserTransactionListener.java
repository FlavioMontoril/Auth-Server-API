package com.api.authserver.listeners;

import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.api.authserver.domain.dtos.user.events.UserCreatedEventDTO;
import com.api.authserver.producer.UserProducer;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class UserTransactionListener {

    private final UserProducer userProducer;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleUserCreatedCommit(UserCreatedEventDTO internalEvent) {
        UserCreatedEventDTO kafkaEvent = new UserCreatedEventDTO(
                internalEvent.id().toString(),
                internalEvent.name(),
                internalEvent.email());
        userProducer.sendUserCreatedEvent(kafkaEvent);
    }
}