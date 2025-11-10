package com.example.Back_End_Restaurante.Controllers;


import com.example.Back_End_Restaurante.Dto.ClienteDTO;
import com.example.Back_End_Restaurante.Model.Cliente;
import com.example.Back_End_Restaurante.Services.ClienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize; // Importar
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/clientes")
// @CrossOrigin(origins = "*") // Removido - Controlado globalmente pelo SecurityConfig
public class ClienteController {

    @Autowired
    private ClienteService clienteService;

    /**
     * Endpoint público para criar um novo cliente (cadastro).
     * Mapeado para: POST /api/clientes
     */
    @PostMapping
    // @PreAuthorize("permitAll()") // Já está no SecurityConfig, mas garante
    public ResponseEntity<ClienteDTO> salvarCliente(@RequestBody ClienteDTO clienteDTO, UriComponentsBuilder uriBuilder) {
        // 1. O Service recebe o DTO e salva a Entidade (com senha hasheada)
        Cliente clienteSalvo = clienteService.SalvarCliente(clienteDTO);

        // 2. Converte a Entidade salva de volta para um DTO (SEM SENHA)
        ClienteDTO dtoResposta = clienteService.converterParaDTO(clienteSalvo);

        // 3. Cria a URI para o novo recurso (ex: /api/clientes/1)
        URI location = uriBuilder.path("/api/clientes/{id}")
                .buildAndExpand(clienteSalvo.getId())
                .toUri();

        // 4. Retorna status 201 CREATED com a URI no header 'Location' e o DTO no corpo
        return ResponseEntity.created(location).body(dtoResposta);
    }

    /**
     * Endpoint para LISTAR todos os clientes.
     * Mapeado para: GET /api/clientes
     * Somente Administradores ou Gerentes podem ver a lista de TODOS os clientes.
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'GERENTE')")
    public ResponseEntity<List<ClienteDTO>> listarClientes() {
        List<ClienteDTO> listaDtos = clienteService.listarTodosClientes();
        return ResponseEntity.ok(listaDtos);
    }

    /**
     * Endpoint para DELETAR um cliente pelo ID.
     * Mapeado para: DELETE /api/clientes/{id}
     * Somente Administradores ou Gerentes podem deletar clientes.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'GERENTE')")
    public ResponseEntity<Void> deletarCliente(@PathVariable Long id) {
        clienteService.DeletarCliente(id);
        // Retorna status 204 NO CONTENT, indicando sucesso sem corpo de resposta
        return ResponseEntity.noContent().build();
    }

    // Você pode adicionar outros endpoints aqui, como:
    // @GetMapping("/{id}")
    // @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'GERENTE') or #id == authentication.principal.usuario.id")
    // (Permite Admin/Gerente ver qualquer um, ou o próprio cliente ver seus dados)
    // ...
}