package com.example.Back_End_Restaurante.Security;

import com.example.Back_End_Restaurante.Model.Cliente;
import com.example.Back_End_Restaurante.Model.Funcionario;
import com.example.Back_End_Restaurante.Model.Usuario;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

/**
 * Implementação customizada de UserDetails.
 * Esta classe armazena os detalhes do usuário (seja Cliente ou Funcionário)
 * que o Spring Security usa para autenticação e autorização.
 */
public class CustomUserDetails implements UserDetails {

    private final Usuario usuario; // Armazena nossa entidade (Cliente ou Funcionario)

    // Construtor
    public CustomUserDetails(Usuario usuario) {
        this.usuario = usuario;
    }

    /**
     * Retorna o objeto Usuario (Cliente ou Funcionario) completo.
     * Usado pelo AuthController para pegar os dados e retornar no LoginResponse.
     */
    public Usuario getUsuario() {
        return usuario;
    }

    /**
     * Retorna as "Roles" (Permissões) do usuário.
     * Ex: ROLE_CLIENTE, ROLE_GERENTE, ROLE_GARCOM
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Se for um funcionário...
        if (usuario instanceof Funcionario) {
            Funcionario funcionario = (Funcionario) usuario;
            // Retorna o cargo dele (ex: "ROLE_GERENTE")
            return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + funcionario.getCargo().name()));
        }
        // Se for um cliente...
        if (usuario instanceof Cliente) {
            // Retorna uma role fixa para clientes
            return Collections.singletonList(new SimpleGrantedAuthority("ROLE_CLIENTE"));
        }
        // Se não for nenhum (não deve acontecer), retorna lista vazia
        return Collections.emptyList();
    }

    /**
     * Retorna a SENHA HASHEADA do usuário.
     * O Spring Security usará isso para comparar com a senha enviada.
     */
    @Override
    public String getPassword() {
        return usuario.getSenha();
    }

    /**
     * Retorna o USERNAME do usuário (nosso email).
     */
    @Override
    public String getUsername() {
        return usuario.getEmail();
    }

    // --- Métodos de status da conta ---
    // Você pode ligar isso a campos reais (ex: 'isAtivo' no Funcionario)

    @Override
    public boolean isAccountNonExpired() {
        return true; // Por padrão, a conta não expira
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // Por padrão, a conta não é bloqueada
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // Por padrão, as credenciais não expiram
    }

    @Override
    public boolean isEnabled() {
        // Se for funcionário, podemos verificar o campo 'ativo'
        if (usuario instanceof Funcionario) {
            return ((Funcionario) usuario).isAtivo();
        }
        // Para clientes, consideramos sempre ativos por padrão
        return true;
    }
}