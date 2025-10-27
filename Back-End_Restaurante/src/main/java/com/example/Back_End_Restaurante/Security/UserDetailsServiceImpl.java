package com.example.Back_End_Restaurante.Security;

import com.example.Back_End_Restaurante.Model.Funcionario;
import com.example.Back_End_Restaurante.Model.Cliente;
import com.example.Back_End_Restaurante.Repositorio.FuncionarioRepository;
import com.example.Back_End_Restaurante.Repositorio.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections; // Para criar a lista de roles

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private FuncionarioRepository funcionarioRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    // Primeiro tenta buscar um funcionário (prioriza funcionários)
    var maybeFunc = funcionarioRepository.findByEmail(email);
    if (maybeFunc.isPresent()) {
        Funcionario funcionario = maybeFunc.get();
        Collection<? extends GrantedAuthority> authorities =
            Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + funcionario.getCargo().name()));
        return new User(funcionario.getEmail(), funcionario.getSenha(), authorities);
    }

    // Se não for funcionário, tenta buscar como cliente
    var maybeCliente = clienteRepository.findByEmail(email);
    if (maybeCliente.isPresent()) {
        Cliente cliente = maybeCliente.get();
        Collection<? extends GrantedAuthority> authorities =
            Collections.singletonList(new SimpleGrantedAuthority("ROLE_CLIENTE"));
        return new User(cliente.getEmail(), cliente.getSenha(), authorities);
    }

    // Não encontrado em nenhum dos repositórios
    throw new UsernameNotFoundException("Usuário não encontrado com o email: " + email);
    }
}