package com.example.Back_End_Restaurante.Dto;

public class LoginResponse {
    private String token;
    private String tipoUsuario; // "CLIENTE" ou "FUNCIONARIO"
    // Armazena os dados do usuário (DTO específico: ClienteDTO ou FuncionarioDTO)
    // Usamos o nome 'usuario' para ser compatível com o frontend esperado.
    private Object usuario;
    private String message;

    // Construtores
    public LoginResponse() {}

    public LoginResponse(String token) {
        this.token = token;
    }

    public LoginResponse(String token, String tipoUsuario, Object usuario, String message) {
        this.token = token;
        this.tipoUsuario = tipoUsuario;
        this.usuario = usuario;
        this.message = message;
    }

    // Getters e Setters
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getTipoUsuario() {
        return tipoUsuario;
    }

    public void setTipoUsuario(String tipoUsuario) {
        this.tipoUsuario = tipoUsuario;
    }

    public Object getUsuario() {
        return usuario;
    }

    public void setUsuario(Object usuario) {
        this.usuario = usuario;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}