package com.api.authserver.listeners;

import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import com.api.authserver.domain.dtos.user.UserPresenceDTO;
import com.api.authserver.domain.repositories.UserRepository;
import com.api.authserver.services.UserPresenceRegistry;

import java.security.Principal;
import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

@Component
@RequiredArgsConstructor 
public class UserPresenceEventListener {

    private final SimpMessagingTemplate messagingTemplate;
    private final UserRepository userRepository;
    private final TaskScheduler taskScheduler;
    private final UserPresenceRegistry presenceRegistry;

    // Armazena desconexões pendentes por email do usuário
    private final ConcurrentHashMap<String, ScheduledFuture<?>> pendingDisconnects = new ConcurrentHashMap<>();

    @EventListener
    public void handleConnect(SessionConnectedEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        Principal principal = accessor.getUser();

        if (principal != null) {
            String email = principal.getName();
            presenceRegistry.markConnected(email); // Marca como conectado

            // Cancela qualquer desconexão pendente para este usuário (caso de F5/refresh)
            ScheduledFuture<?> pending = pendingDisconnects.remove(email);
            if (pending != null) {
                pending.cancel(false);
                // Não envia evento de conexão, pois o disconnect nunca foi notificado
                return;
            }

            // Envia evento de conexão normalmente (conexão nova de fato)
            sendPresenceEvent(email, true);
        }
    }

    @EventListener
    public void handleDisconnect(SessionDisconnectEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        Principal principal = accessor.getUser();

        if (principal != null) {
            String email = principal.getName();

            // Agenda o envio do evento de desconexão com delay de 5 segundos
            ScheduledFuture<?> scheduled = taskScheduler.schedule(
                () -> {
                    pendingDisconnects.remove(email);
                    presenceRegistry.markDisconnected(email); // Remove registro após os 5s
                    sendPresenceEvent(email, false);
                },
                Instant.now().plusSeconds(5)
            );

            // Se já havia uma desconexão pendente para este usuário, cancela a anterior
            ScheduledFuture<?> previous = pendingDisconnects.put(email, scheduled);
            if (previous != null) {
                previous.cancel(false);
            }
        }
    }

    private void sendPresenceEvent(String email, boolean isConnected) {
        userRepository.findByEmail(email).ifPresent(user -> {
            UserPresenceDTO dto = new UserPresenceDTO(
                user.getId(),
                user.getName(),
                user.getAvatar(),
                isConnected
            );
            messagingTemplate.convertAndSend("/topic/presence", dto);
        });
    }
}
