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
    private PasswordEncoder passwordEncoder;

    public List<ClienteDTO> listarTodosClientes() {
        return clienteRepository.findAll()
                .stream()
                .map(this::converterParaDTO)
                .collect(Collectors.toList());
    }

    public Cliente SalvarCliente(ClienteDTO clienteDTO) {
        if (clienteDTO.getEmail() == null || clienteDTO.getEmail().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O e-mail não pode ser vazio.");
        }
        if (clienteRepository.existsByEmail(clienteDTO.getEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "O e-mail '" + clienteDTO.getEmail() + "' já está em uso.");
        }
        if (clienteDTO.getSenha() == null || clienteDTO.getSenha().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A senha não pode ser vazia.");
        }

        Cliente cliente = new Cliente();
        cliente.setNome(clienteDTO.getNome());
        cliente.setTelefone(clienteDTO.getTelefone());
        cliente.setEmail(clienteDTO.getEmail());
        cliente.setSenha(passwordEncoder.encode(clienteDTO.getSenha()));

        // Pontos iniciam com 0 (já definido na classe, mas bom garantir)
        cliente.setPontosFidelidade(0);

        return clienteRepository.save(cliente);
    }

    public void DeletarCliente(Long id) {
        if (!clienteRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente não encontrado com o ID: " + id);
        }
        clienteRepository.deleteById(id);
    }

    // --- ATUALIZAÇÃO AQUI ---
    public ClienteDTO converterParaDTO(Cliente cliente) {
        if (cliente == null) {
            return null;
        }
        ClienteDTO dto = new ClienteDTO();
        dto.setId(cliente.getId());
        dto.setNome(cliente.getNome());
        dto.setEmail(cliente.getEmail());
        dto.setTelefone(cliente.getTelefone());

        // Inclui os pontos na resposta
        dto.setPontosFidelidade(cliente.getPontosFidelidade());

        return dto;
    }
}