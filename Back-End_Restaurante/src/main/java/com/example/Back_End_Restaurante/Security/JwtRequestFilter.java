package com.example.Back_End_Restaurante.Security; // <-- PACOTE CORRIGIDO

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        final String authorizationHeader = request.getHeader("Authorization");

        String username = null;
        String jwt = null;

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.substring(7);
            try {
                username = jwtUtil.extractUsername(jwt);
            } catch (ExpiredJwtException e) {
                logger.warn("Token JWT expirou: " + e.getMessage());
            } catch (Exception e) {
                logger.warn("Erro ao processar token JWT: " + e.getMessage());
            }
        } else {
            // Não loga warnings para endpoints públicos comuns
            if (!request.getRequestURI().equals("/api/auth/login") && // Não loga para o login
                    !request.getRequestURI().equals("/api/clientes") && // Não loga para cadastro
                    !request.getRequestURI().startsWith("/api/mesas") && // Não loga para ver mesas
                    !request.getRequestURI().startsWith("/api/produtos") && // Não loga para ver produtos
                    !request.getRequestURI().startsWith("/api/categorias")) // Não loga para ver categorias
            {
                logger.warn("Header Authorization não encontrado ou não começa com Bearer (Path: "
                        + request.getRequestURI() + ")");
            }
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

                if (jwtUtil.validateToken(jwt, userDetails)) {
                    logger.debug("Token JWT VÁLIDO para usuário: " + username + ". Configurando autenticação...");

                    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());
                    authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    // Coloca o usuário no Contexto de Segurança
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                }
            } catch (org.springframework.security.core.userdetails.UsernameNotFoundException ex) {
                logger.warn("Usuário do token não foi encontrado: " + username);
            } catch (Exception ex) {
                logger.warn("Erro ao validar token JWT para usuário " + username + ": " + ex.getMessage());
            }
        }

        filterChain.doFilter(request, response);
    }
}