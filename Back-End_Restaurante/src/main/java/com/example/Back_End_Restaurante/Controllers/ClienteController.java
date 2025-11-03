package com.example.Back_End_Restaurante.Controllers;


import com.example.Back_End_Restaurante.Dto.ClienteDTO;
import com.example.Back_End_Restaurante.Model.Cliente;
import com.example.Back_End_Restaurante.Services.ClienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/clientes")
@CrossOrigin(origins = "*")
public class ClienteController {

    @Autowired
    private ClienteService clienteService;


    @GetMapping
    public ResponseEntity<List<ClienteDTO>> listarClientes() {
        List<Cliente> clientes = clienteService.ListarTodosClientes();
        List<ClienteDTO> ClienteDTO = clientes.stream()
                .map(cliente -> {
                    ClienteDTO dto = new ClienteDTO();
                    dto.setNome(cliente.getNome());
                    dto.setEmail(cliente.getEmail());
                    dto.setTelefone(cliente.getTelefone());
                    return dto;
                })
                .toList();

        return ResponseEntity.ok(ClienteDTO);
    }
    @PostMapping
    public ResponseEntity<ClienteDTO> salvarCliente(@RequestBody ClienteDTO clienteDTO) {

        // 1. O Service recebe o DTO e salva a Entidade
        Cliente clienteSalvo = clienteService.SalvarCliente(clienteDTO);

        // 2. Converte a Entidade salva de volta para um DTO para a resposta
        ClienteDTO dtoResposta = new ClienteDTO();
        dtoResposta.setNome(clienteSalvo.getNome());
        dtoResposta.setEmail(clienteSalvo.getEmail());
        dtoResposta.setTelefone(clienteSalvo.getTelefone());
        // dtoResposta.setId(clienteSalvo.getId()); // Idealmente, o DTO teria o ID

        // 3. Cria a URI para o novo recurso (ex: /clientes/1)
        URI location = UriComponentsBuilder.fromPath("/clientes/{id}")
                .buildAndExpand(clienteSalvo.getId())
                .toUri();

        // 4. Retorna status 201 CREATED com a URI no header 'Location' e o DTO no corpo
        return ResponseEntity.created(location).body(dtoResposta);
    }

    /**
     * Endpoint para DELETAR um cliente pelo ID.
     * Mapeado para: DELETE /clientes/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarCliente(@PathVariable Long id) {
        clienteService.DeletarCliente(id);

        // Retorna status 204 NO CONTENT, indicando sucesso sem corpo de resposta
        return ResponseEntity.noContent().build();
    }
}

