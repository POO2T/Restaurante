package com.example.Back_End_Restaurante.Security;

import com.example.Back_End_Restaurante.Model.Funcionario;
import com.example.Back_End_Restaurante.Repositorio.FuncionarioRepository;
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

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // Busca o funcionário pelo email no banco
        Funcionario funcionario = funcionarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado com o email: " + email));

        // Cria uma coleção de "authorities" (roles/permissões)
        // Por enquanto, vamos dar uma role simples baseada no cargo, prefixada com ROLE_
        // Ex: Se o cargo for GERENTE, a role será ROLE_GERENTE
        // Você pode customizar isso depois
        Collection<? extends GrantedAuthority> authorities =
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + funcionario.getCargo().name()));


        // Retorna um objeto UserDetails que o Spring Security entende
        // O username aqui é o email, a senha é a senha HASHEADA do banco,
        // e as authorities são as roles.
        return new User(funcionario.getEmail(), funcionario.getSenha(), authorities);
    }
}