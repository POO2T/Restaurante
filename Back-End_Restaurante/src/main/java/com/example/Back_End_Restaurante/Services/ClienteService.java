package com.example.Back_End_Restaurante.Services;

import com.example.Back_End_Restaurante.Dto.ClienteDTO;
import com.example.Back_End_Restaurante.Model.Cliente;
import com.example.Back_End_Restaurante.Repositorio.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ClienteService {

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private PasswordEncoder passwordEncoder; // Injeta o codificador de senhas

    /**
     * Lista todos os clientes e os converte para DTOs (sem senha).
     * Chamado pelo ClienteController (GET /api/clientes).
     */
    public List<ClienteDTO> listarTodosClientes() {
        return clienteRepository.findAll()
                .stream()
                .map(this::converterParaDTO) // Converte cada Cliente para ClienteDTO
                .collect(Collectors.toList());
    }

    /**
     * Salva um novo cliente, validando os dados e hasheando a senha.
     * Chamado pelo ClienteController (POST /api/clientes).
     */
    public Cliente SalvarCliente(ClienteDTO clienteDTO) {
        // 1. Validação de E-mail (antes de criar o objeto)
        if (clienteDTO.getEmail() == null || clienteDTO.getEmail().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O e-mail não pode ser vazio.");
        }
        if (clienteRepository.existsByEmail(clienteDTO.getEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "O e-mail '" + clienteDTO.getEmail() + "' já está em uso.");
        }

        // 2. Validação de Senha
        if (clienteDTO.getSenha() == null || clienteDTO.getSenha().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A senha não pode ser vazia.");
        }

        // 3. Criar a entidade Cliente
        Cliente cliente = new Cliente();
        cliente.setNome(clienteDTO.getNome());
        cliente.setTelefone(clienteDTO.getTelefone());
        cliente.setEmail(clienteDTO.getEmail());

        // 4. Hashear a senha antes de salvar
        cliente.setSenha(passwordEncoder.encode(clienteDTO.getSenha()));

        // Você pode adicionar outros campos do DTO aqui (ex: endereço)
        // cliente.setEndereco(clienteDTO.getEndereco());

        // 5. Salvar no banco
        return clienteRepository.save(cliente);
    }

    /**
     * Deleta um cliente pelo ID.
     * Chamado pelo ClienteController (DELETE /api/clientes/{id}).
     */
    public void DeletarCliente(Long id) {
        if (!clienteRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente não encontrado com o ID: " + id);
        }
        clienteRepository.deleteById(id);
    }

    /**
     * Método auxiliar para converter uma Entidade Cliente em um ClienteDTO.
     * Isso garante que a senha (mesmo hasheada) NUNCA seja enviada para o frontend.
     */
    public ClienteDTO converterParaDTO(Cliente cliente) {
        if (cliente == null) {
            return null;
        }
        ClienteDTO dto = new ClienteDTO();
        dto.setId(cliente.getId()); // Adicionando ID ao DTO (útil para o front)
        dto.setNome(cliente.getNome());
        dto.setEmail(cliente.getEmail());
        dto.setTelefone(cliente.getTelefone());
        // Note: A SENHA NÃO É INCLUÍDA AQUI (propositalmente)
        // dto.setPlanoFidelidade(cliente.getPlanoFidelidade()); // Adicionar se necessário
        return dto;
    }
}