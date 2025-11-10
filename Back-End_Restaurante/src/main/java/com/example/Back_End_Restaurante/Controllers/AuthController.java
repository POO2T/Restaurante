package com.example.Back_End_Restaurante.Controllers;

import com.example.Back_End_Restaurante.Dto.ClienteDTO;
import com.example.Back_End_Restaurante.Dto.FuncionarioDTO;
import com.example.Back_End_Restaurante.Dto.LoginRequest;
import com.example.Back_End_Restaurante.Dto.LoginResponse;
import com.example.Back_End_Restaurante.Model.Cliente;
import com.example.Back_End_Restaurante.Model.Funcionario;
import com.example.Back_End_Restaurante.Model.Usuario;
import com.example.Back_End_Restaurante.Security.CustomUserDetails; // <<< IMPORTAR
import com.example.Back_End_Restaurante.Security.Jwt.JwtUtil;
import com.example.Back_End_Restaurante.Services.ClienteService;
import com.example.Back_End_Restaurante.Services.FuncionarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/auth")
// @CrossOrigin(origins = "*") // Removido - Agora é controlado globalmente pelo SecurityConfig
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    // Injeta os services para conversão de DTO
    @Autowired
    private FuncionarioService funcionarioService;

    @Autowired
    private ClienteService clienteService;

    @PostMapping("/login")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody LoginRequest loginRequest) {
        try {
            // 1. Tenta autenticar
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getSenha())
            );

            // 2. Autenticação bem-sucedida. Pega os detalhes do usuário
            //    que foram carregados pelo UserDetailsServiceImpl
            CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();

            // 3. Pega o objeto Usuário (Cliente ou Funcionario) de dentro dos UserDetails
            //    (Isso economiza uma busca no banco de dados!)
            Usuario usuario = customUserDetails.getUsuario();

            String
                    tipoUsuario;
            Object dadosUsuarioDTO; // Usará DTO para não vazar a senha

            // 4. Verifica o tipo e converte para o DTO apropriado
            if (usuario instanceof Funcionario) {
                tipoUsuario = "FUNCIONARIO";
                dadosUsuarioDTO = funcionarioService.converterParaDTO((Funcionario) usuario); // Usa o método do service
            } else if (usuario instanceof Cliente) {
                tipoUsuario = "CLIENTE";
                dadosUsuarioDTO = clienteService.converterParaDTO((Cliente) usuario); // Usa o método do service
            } else {
                // Caso de segurança, não deve acontecer
                throw new IllegalStateException("Tipo de usuário desconhecido após autenticação.");
            }

            // 5. Gerar token JWT
            final String token = jwtUtil.generateToken(customUserDetails);

            // 6. Criar resposta completa com token, tipo e dados do usuário (em DTO)
            // Agora retornamos o objeto no campo 'usuario' (nome padronizado)
            LoginResponse response = new LoginResponse(
                    token,
                    tipoUsuario,
                    dadosUsuarioDTO, // Retorna o DTO (sem senha) -> será preenchido em 'usuario'
                    "Login realizado com sucesso!"
            );

            return ResponseEntity.ok(response);

        } catch (BadCredentialsException e) {
            return ResponseEntity.status(401).body("Credenciais inválidas");
        } catch (Exception e) {
            e.printStackTrace(); // Para debug
            return ResponseEntity.status(500).body("Erro durante a autenticação: " + e.getMessage());
        }
    }
}