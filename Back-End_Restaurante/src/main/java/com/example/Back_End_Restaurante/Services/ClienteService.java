package com.example.Back_End_Restaurante.Services;


import com.example.Back_End_Restaurante.Dto.ClienteDTO;
import com.example.Back_End_Restaurante.Model.Cliente;
import com.example.Back_End_Restaurante.Repositorio.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus; // Importar HttpStatus
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException; // Importar ResponseStatusException

import java.util.List;

@Service
public class ClienteService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ClienteRepository clienteRepository;

    public List<Cliente> ListarTodosClientes() {
        return clienteRepository.findAll();
    }

    public Cliente SalvarCliente(ClienteDTO clienteDTO) {

        // --- LÓGICA DE VALIDAÇÃO CORRIGIDA ---
        // 1. Validar primeiro, assim como no FuncionarioService
        if (clienteRepository.existsByEmail(clienteDTO.getEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "O email '" + clienteDTO.getEmail() + "' já está em uso.");
        }

        // 2. Validar senha (boa prática)
        if (clienteDTO.getSenha() == null || clienteDTO.getSenha().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A senha não pode ser nula ou vazia.");
        }
        // --- FIM DA CORREÇÃO ---

        Cliente cliente = new Cliente();
        cliente.setNome(clienteDTO.getNome());
        cliente.setTelefone(clienteDTO.getTelefone());
        cliente.setEmail(clienteDTO.getEmail()); // Agora pode setar sem o try-catch

        // Hashear a senha
        cliente.setSenha(passwordEncoder.encode(clienteDTO.getSenha()));

        // Adicione outros campos do DTO se necessário (ex: endereço)
        // cliente.setEndereco(clienteDTO.getEndereco());

        return clienteRepository.save(cliente);
    }

    public void DeletarCliente(Long id) {
        clienteRepository.deleteById(id);
    }

    // Você pode adicionar um método AtualizarCliente aqui depois,
    // lembrando de hashear a senha se ela for alterada.
}

