package com.example.Back_End_Restaurante.Security;

import com.example.Back_End_Restaurante.Model.Cliente;
import com.example.Back_End_Restaurante.Model.Funcionario;
import com.example.Back_End_Restaurante.Model.Usuario; // Importar
import com.example.Back_End_Restaurante.Repositorio.ClienteRepository;
import com.example.Back_End_Restaurante.Repositorio.FuncionarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Serviço que o Spring Security usa para carregar os detalhes de um usuário
 * durante o processo de login (autenticação).
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private FuncionarioRepository funcionarioRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    /**
     * Este método é chamado pelo AuthenticationManager.
     * Ele tenta encontrar um usuário pelo email (username) no banco.
     * Ele procura primeiro em Funcionarios, depois em Clientes.
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // Tenta encontrar como Funcionário primeiro
        Optional<Funcionario> funcionarioOpt = funcionarioRepository.findByEmail(email);

        if (funcionarioOpt.isPresent()) {
            Funcionario funcionario = funcionarioOpt.get();
            // Retorna nossa classe customizada que guarda o funcionário
            return new CustomUserDetails(funcionario);
        }

        // Se não for Funcionário, tenta encontrar como Cliente
        Optional<Cliente> clienteOpt = clienteRepository.findByEmail(email);

        if (clienteOpt.isPresent()) {
            Cliente cliente = clienteOpt.get();
            // Retorna nossa classe customizada que guarda o cliente
            return new CustomUserDetails(cliente);
        }

        // Se não encontrou em nenhum dos dois, lança a exceção
        throw new UsernameNotFoundException("Usuário não encontrado com o email: " + email);
    }
}