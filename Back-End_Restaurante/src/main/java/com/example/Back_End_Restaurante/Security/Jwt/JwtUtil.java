package com.example.Back_End_Restaurante.Security.Jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value; // Para ler do application.properties
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component // Marca como um Bean do Spring para poder ser injetado
public class JwtUtil {

    // --- Configuração (Leia do application.properties) ---

    // Chave secreta FORTE para assinar o token (deve ser longa e aleatória)
    // Gere uma chave segura, por ex, usando sites online ou código
    // Exemplo: openssl rand -base64 32 (no terminal)
    // Coloque no application.properties: jwt.secret=SUA_CHAVE_SECRETA_MUITO_LONGA_AQUI
    @Value("${jwt.secret}")
    private String secretKeyString;

    // Tempo de expiração do token em milissegundos (ex: 1 hora)
    // Coloque no application.properties: jwt.expiration=3600000
    @Value("${jwt.expiration}")
    private long jwtExpiration;

    // --- Geração do Token ---

    // Gera o token para um usuário (UserDetails)
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        // Você pode adicionar claims extras aqui se precisar (ex: roles)
        // claims.put("roles", userDetails.getAuthorities().stream()...);
        return createToken(claims, userDetails.getUsername()); // Username aqui é o email
    }

    private String createToken(Map<String, Object> claims, String subject) {
        SecretKey key = getSigningKey();
        return Jwts.builder()
                .claims(claims)
                .subject(subject) // Define o 'sub' (subject) do token como o email do usuário
                .issuedAt(new Date(System.currentTimeMillis())) // Data de emissão
                .expiration(new Date(System.currentTimeMillis() + jwtExpiration)) // Data de expiração
                .signWith(key, Jwts.SIG.HS256) // Assina com a chave secreta e algoritmo HS256
                .compact(); // Constrói o token como uma string compacta
    }

    // --- Validação e Extração ---

    // Extrai o email (subject) do token
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // Extrai a data de expiração do token
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    // Extrai um claim específico usando uma função
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // Faz o parse do token para extrair todos os claims
    private Claims extractAllClaims(String token) {
        SecretKey key = getSigningKey();
        return Jwts.parser()
                .verifyWith(key) // Verifica a assinatura usando a chave
                .build()
                .parseSignedClaims(token)
                .getPayload(); // Obtém o corpo (claims)
    }

    // Verifica se o token expirou
    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    // Valida o token: verifica se pertence ao usuário e não expirou
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    // --- Helper ---

    // Converte a string da chave secreta em um objeto SecretKey
    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(this.secretKeyString);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
