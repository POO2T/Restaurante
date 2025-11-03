package com.example.Back_End_Restaurante.Controllers;

import com.example.Back_End_Restaurante.Dto.LoginRequest; // Importa a classe para receber os dados
import com.example.Back_End_Restaurante.Dto.LoginResponse; // <<< ADICIONE ESTE IMPORT
import com.example.Back_End_Restaurante.Security.Jwt.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserDetailsService userDetailsService; // Injete o UserDetailsService que criamos (UserDetailsServiceImpl)

    @Autowired
    private JwtUtil jwtUtil; // Injete seu utilitário JWT quando o criar

    @PostMapping("/login") // Endpoint correto é /api/auth/login
    public ResponseEntity<?> createAuthenticationToken(@RequestBody LoginRequest loginRequest) {
        try {
            // 1. Tenta autenticar
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getSenha())
            );

            // Se chegou aqui, a autenticação foi bem-sucedida

            // 2. Busca os UserDetails (pode pegar do 'authentication' ou buscar novamente)
            // UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            // OU buscar novamente para garantir que tem o objeto completo:
            final UserDetails userDetails = userDetailsService
                    .loadUserByUsername(loginRequest.getEmail());


            // --- PONTO PARA GERAR O TOKEN JWT ---
            final String token = jwtUtil.generateToken(userDetails);


            // 3. Retorna o token na resposta
            // Certifique-se que LoginResponse tem um construtor que aceita String
            return ResponseEntity.ok(new LoginResponse(token)); // Passa o token para o construtor

            // <<< PONTOS E VÍRGULAS REMOVIDOS DAQUI >>>

        } catch (BadCredentialsException e) {
            // Se a autenticação falhar
            return ResponseEntity.status(401).body("Credenciais inválidas");
        } catch (Exception e) {
            // Outros erros
            // Logar o erro aqui é uma boa prática: e.printStackTrace();
            return ResponseEntity.status(500).body("Erro durante a autenticação: " + e.getMessage());
        }
        // <<< PONTOS E VÍRGULAS REMOVIDOS DAQUI >>>
    }
}
