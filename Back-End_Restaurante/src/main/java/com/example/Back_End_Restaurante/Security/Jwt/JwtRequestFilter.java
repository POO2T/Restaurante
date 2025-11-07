package com.example.Back_End_Restaurante.Security.Jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull; // Para anotação @NonNull
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter; // Importante usar este

import java.io.IOException;

@Component // Marca como Bean para ser injetado no SecurityConfig
public class JwtRequestFilter extends OncePerRequestFilter { // Estende OncePerRequestFilter

    @Autowired
    private UserDetailsService userDetailsService; // Nosso UserDetailsServiceImpl

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request, // Requisição HTTP
            @NonNull HttpServletResponse response, // Resposta HTTP
            @NonNull FilterChain filterChain) // Cadeia de filtros
            throws ServletException, IOException {

        final String authorizationHeader = request.getHeader("Authorization"); // Pega o header Authorization

        String username = null;
        String jwt = null;

        // Verifica se o header existe e começa com "Bearer "
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.substring(7); // Extrai o token (remove "Bearer ")
            try {
                username = jwtUtil.extractUsername(jwt); // Extrai o email (username) do token
            } catch (Exception e) {
                // Token inválido (expirado, assinatura errada, etc.) - não fazer nada aqui,
                // apenas logar seria bom. O usuário não será autenticado.
                logger.warn("Não foi possível extrair username do token JWT: " + e.getMessage());
            }
        } else {
            logger.warn("Header Authorization não encontrado ou não começa com Bearer");
        }


        // Se extraiu o username e NÃO HÁ autenticação no contexto atual
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

            // Se o token for válido para este usuário
            if (jwtUtil.validateToken(jwt, userDetails)) {

                // Cria o objeto de autenticação
                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities()); // Credenciais são null pois usamos token

                // Associa detalhes da requisição web à autenticação
                usernamePasswordAuthenticationToken
                        .setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // Define a autenticação no contexto de segurança do Spring
                // A partir daqui, o usuário é considerado autenticado para esta requisição
                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                logger.debug("Usuário autenticado via JWT: " + username);
            } else {
                logger.warn("Token JWT inválido para o usuário: " + username);
            }
        } else {
            // Se não encontrou token ou já existe autenticação, não faz nada
            if(username == null && authorizationHeader != null && !authorizationHeader.startsWith("Bearer ")) {
                logger.warn("Formato inválido do header Authorization.");
            }
        }


        // Continua a execução da cadeia de filtros (essencial!)
        filterChain.doFilter(request, response);
    }
}

