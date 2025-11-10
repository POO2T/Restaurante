package com.example.Back_End_Restaurante.Security.Jwt;

import com.example.Back_End_Restaurante.Security.UserDetailsServiceImpl;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
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

/**
 * Este filtro intercepta CADA requisição (apenas uma vez) para verificar o JWT.
 */
@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // 1. Pega o cabeçalho "Authorization" da requisição
        final String authorizationHeader = request.getHeader("Authorization");

        String username = null;
        String jwt = null;

        // 2. Verifica se o cabeçalho existe e começa com "Bearer "
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.substring(7); // Remove "Bearer " para pegar só o token
            try {
                username = jwtUtil.extractUsername(jwt); // Extrai o email do token
            } catch (ExpiredJwtException e) {
                System.out.println("Erro: Token JWT expirou!");
            } catch (UnsupportedJwtException e) {
                System.out.println("Erro: Token JWT não suportado!");
            } catch (MalformedJwtException e) {
                System.out.println("Erro: Token JWT mal formado!");
            } catch (SignatureException e) {
                System.out.println("Erro: Assinatura do Token JWT inválida!");
            } catch (IllegalArgumentException e) {
                System.out.println("Erro: Claims do JWT estão vazias!");
            }
        } else {
            // Comente esta linha se ficar muito verbosa no log
            System.out.println("Requisição para " + request.getRequestURI() + " não continha Bearer Token.");
        }

        // 3. Se temos um username e o usuário ainda NÃO está autenticado no contexto
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // Carrega os detalhes do usuário (do nosso DB)
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

            // 4. Valida o token (compara com UserDetails e verifica expiração)
            if (jwtUtil.validateToken(jwt, userDetails)) {

                // --- SUCESSO! ---
                System.out.println("Token JWT VÁLIDO para usuário: " + username + ". Configurando autenticação...");

                // Cria a autenticação
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());

                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // Coloca o usuário no Contexto de Segurança do Spring
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            } else {
                System.out.println("Token JWT inválido para usuário: " + username);
            }
        }

        // 5. Continua a cadeia de filtros (deixa a requisição passar)
        filterChain.doFilter(request, response);
    }
}