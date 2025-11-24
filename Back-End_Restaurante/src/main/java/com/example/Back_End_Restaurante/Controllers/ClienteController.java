package com.example.Back_End_Restaurante.Controllers;

import com.example.Back_End_Restaurante.Dto.ClienteDTO;
import com.example.Back_End_Restaurante.Model.Cliente;
import com.example.Back_End_Restaurante.Model.Usuario; // Importar
import com.example.Back_End_Restaurante.Security.CustomUserDetails; // Importar
import com.example.Back_End_Restaurante.Services.ClienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication; // Importar
import org.springframework.security.core.context.SecurityContextHolder; // Importar
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/clientes")
// @CrossOrigin removido (configurado globalmente)
public class ClienteController {

    @Autowired
    private ClienteService clienteService;

    // ... (Métodos salvarCliente, listarClientes, deletarCliente MANTIDOS IGUAIS) ...
    @PostMapping
    public ResponseEntity<ClienteDTO> salvarCliente(@RequestBody ClienteDTO clienteDTO, UriComponentsBuilder uriBuilder) {
        Cliente clienteSalvo = clienteService.SalvarCliente(clienteDTO);
        ClienteDTO dtoResposta = clienteService.converterParaDTO(clienteSalvo);
        URI location = uriBuilder.path("/api/clientes/{id}").buildAndExpand(clienteSalvo.getId()).toUri();
        return ResponseEntity.created(location).body(dtoResposta);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'GERENTE')")
    public ResponseEntity<List<ClienteDTO>> listarClientes() {
        List<ClienteDTO> listaDtos = clienteService.listarTodosClientes();
        return ResponseEntity.ok(listaDtos);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'GERENTE')")
    public ResponseEntity<Void> deletarCliente(@PathVariable Long id) {
        clienteService.DeletarCliente(id);
        return ResponseEntity.noContent().build();
    }

    // --- NOVO ENDPOINT: Ver Meu Perfil (com pontos) ---
    @GetMapping("/meu-perfil")
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<ClienteDTO> getMeuPerfil(Authentication authentication) {
        // Pega o usuário logado do token
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Usuario usuario = userDetails.getUsuario();

        // Garante que é um Cliente (embora @PreAuthorize já filtre a role)
        if (usuario instanceof Cliente) {
            // Retorna o DTO do cliente (que inclui os pontos de fidelidade)
            return ResponseEntity.ok(clienteService.converterParaDTO((Cliente) usuario));
        }

        return ResponseEntity.status(403).build();
    }
}