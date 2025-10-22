package com.example.Back_End_Restaurante.Services;


import com.example.Back_End_Restaurante.Dto.ClienteDTO;
import com.example.Back_End_Restaurante.Model.Cliente;
import com.example.Back_End_Restaurante.Repositorio.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClienteService {

    @Autowired
    private ClienteRepository clienteRepository;

    public List<Cliente> ListarTodosClientes() {
        return clienteRepository.findAll();
    }

    public Cliente SalvarCliente(ClienteDTO clienteDTO) {
        Cliente cliente = new Cliente();
        cliente.setNome(clienteDTO.getNome());
        cliente.setTelefone(clienteDTO.getTelefone());
        cliente.setSenha(clienteDTO.getSenha());

        try {
            if(clienteRepository.existsByEmail(clienteDTO.getEmail())) {
                throw new RuntimeException("O email '" + clienteDTO.getEmail() + "' já está em uso.");
            }
            cliente.setEmail(clienteDTO.getEmail());
        }
        catch (IllegalArgumentException e) {
            throw new RuntimeException("O email '" + clienteDTO.getEmail() + "' é inválido.");
        }



        return clienteRepository.save(cliente);
    }

    public void DeletarCliente(Long id) {
        clienteRepository.deleteById(id);
    }


}
