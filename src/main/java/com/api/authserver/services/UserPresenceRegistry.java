package com.api.authserver.services;

import org.springframework.stereotype.Component;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class UserPresenceRegistry {

    private final Set<String> connectedEmails = ConcurrentHashMap.newKeySet();

    public void markConnected(String email) {
        connectedEmails.add(email);
    }

    public void markDisconnected(String email) {
        connectedEmails.remove(email);
    }

    public boolean isConnected(String email) {
        return connectedEmails.contains(email);
    }
}

// ADICIONA OS USUARIOS CONECTADOS E OU REMOVE(DESCONECTADOS) A UMA LISTA E DEVOLVE NO SERVICE POR MEIO DO DTO