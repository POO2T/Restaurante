package com.example.Back_End_Restaurante.Security; // <-- PACOTE CORRIGIDO

import com.example.Back_End_Restaurante.Model.Usuario;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

public class CustomUserDetails implements UserDetails {

    private final String username; // O email
    @JsonIgnore
    private final String password; // A senha hasheada
    private final Collection<? extends GrantedAuthority> authorities; // As ROLES
    private final Usuario usuario; // O objeto Cliente ou Funcionario

    public CustomUserDetails(String username, String password, Collection<? extends GrantedAuthority> authorities, Usuario usuario) {
        this.username = username;
        this.password = password;
        this.authorities = authorities;
        this.usuario = usuario;
    }

    public Usuario getUsuario() {
        return usuario;
    }

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

    @Override
    public boolean isAccountNonExpired() { return true; }
    @Override
    public boolean isAccountNonLocked() { return true; }
    @Override
    public boolean isCredentialsNonExpired() { return true; }
    @Override
    public boolean isEnabled() { return true; }
}