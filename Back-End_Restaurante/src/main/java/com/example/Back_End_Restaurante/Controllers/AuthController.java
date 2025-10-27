package com.example.Back_End_Restaurante.Controllers;

import com.example.Back_End_Restaurante.Dto.LoginRequest;
import com.example.Back_End_Restaurante.Dto.LoginResponse;
import com.example.Back_End_Restaurante.Dto.ClienteDTO;
import com.example.Back_End_Restaurante.Dto.FuncionarioDTO;
import com.example.Back_End_Restaurante.Model.Cliente;
import com.example.Back_End_Restaurante.Model.Funcionario;
import com.example.Back_End_Restaurante.Repositorio.ClienteRepository;
import com.example.Back_End_Restaurante.Repositorio.FuncionarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private FuncionarioRepository funcionarioRepository;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        // Autentica via AuthenticationManager (vai usar seu UserDetailsServiceImpl)
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getSenha())
        );

        // Se a autenticação foi bem-sucedida, retorna informações do usuário
        // Tenta encontrar como funcionário primeiro
        var maybeFunc = funcionarioRepository.findByEmail(request.getEmail());
        if (maybeFunc.isPresent()) {
            Funcionario f = maybeFunc.get();
            FuncionarioDTO dto = new FuncionarioDTO();
            dto.setNome(f.getNome());
            dto.setEmail(f.getEmail());
            dto.setSalario(f.getSalario());
            // Não exponha a senha

            LoginResponse resp = new LoginResponse(null, dto, "FUNCIONARIO", 0L);
            return ResponseEntity.ok(resp);
        }

        var maybeCli = clienteRepository.findByEmail(request.getEmail());
        if (maybeCli.isPresent()) {
            Cliente c = maybeCli.get();
            ClienteDTO dto = new ClienteDTO();
            dto.setNome(c.getNome());
            dto.setEmail(c.getEmail());
            dto.setTelefone(c.getTelefone());

            LoginResponse resp = new LoginResponse(null, dto, "CLIENTE", 0L);
            return ResponseEntity.ok(resp);
        }

        // Se não encontrou (apesar da autenticação), retorna 401
        return ResponseEntity.status(401).build();
    }
}
