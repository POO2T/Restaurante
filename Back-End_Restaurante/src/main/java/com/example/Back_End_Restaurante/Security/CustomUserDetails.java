package com.example.Back_End_Restaurante.Security;

import com.example.Back_End_Restaurante.Model.Usuario;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

/**
 * Classe customizada de UserDetails para carregar também o objeto Usuário.
 * Isso otimiza o AuthController, evitando uma segunda busca no banco de dados.
 */
public class CustomUserDetails implements UserDetails {

    private final String username; // O email
    @JsonIgnore // Garante que a senha hasheada nunca seja serializada
    private final String password; // A senha hasheada
    private final Collection<? extends GrantedAuthority> authorities; // As ROLES (ex: ROLE_GERENTE)
    private final Usuario usuario; // O objeto Cliente ou Funcionario (sem a senha)

    public CustomUserDetails(String username, String password, Collection<? extends GrantedAuthority> authorities, Usuario usuario) {
        this.username = username;
        this.password = password;
        this.authorities = authorities;
        this.usuario = usuario;
    }

    // Método que criamos para pegar o usuário completo no AuthController
    public Usuario getUsuario() {
        return usuario;
    }

    // --- Métodos padrão da interface UserDetails ---

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username; // Retorna o email
    }

    // Abaixo, métodos para verificar status da conta.
    // Deixamos como 'true' por padrão, mas você pode implementar
    // uma lógica mais complexa (ex: checar 'usuario.isAtivo()')

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        // Você pode ligar isso ao campo 'ativo' do Funcionario, por exemplo
        return true;
    }
}
