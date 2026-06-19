package com.api.authserver.infra.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class SecurityFilter extends OncePerRequestFilter {

    private final TokenService tokenService;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // 1. Extrai o token do Header "Authorization"
        var token = this.recoverToken(request);

        if (token != null) {
            // 2. Valida o token e pega o e-mail (username) do usuário
            var login = tokenService.validateToken(token);

            if (login != null) {
                // 3. Busca o usuário no banco de dados
                UserDetails user = userDetailsService.loadUserByUsername(login);

                // 4. Cria o objeto de autenticação do Spring, PASSANDO AS ROLES
                // (getAuthorities)
                var authentication = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());

                // 5. Salva a autenticação no contexto do Spring Security
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        // 6. Segue o fluxo normal da requisição (vai para o próximo filtro ou
        // controller)
        filterChain.doFilter(request, response);
    }

    // Método auxiliar para extrair o token limpo (sem a palavra "Bearer ")
    private String recoverToken(HttpServletRequest request) {
        var authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return null;
        }
        return authHeader.replace("Bearer ", "");
    }
}