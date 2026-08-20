package com.api.authserver.infra.utils;

import jakarta.servlet.http.HttpServletRequest;

public class HttpUtils {

    private HttpUtils() {
        // Construtor privado para evitar a instanciação dessa classe utilitária
    }

    public static String getClientIpAddress(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null || xfHeader.isEmpty()) {
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0].trim();
    }
}
