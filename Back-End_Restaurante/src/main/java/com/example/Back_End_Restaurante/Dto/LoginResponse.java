package com.example.Back_End_Restaurante.Dto;

public class LoginResponse {
    private String token;
    private String tipoUsuario; // "CLIENTE" ou "FUNCIONARIO"
    // Mudamos de "Usuario" para "Object" para aceitar DTOs (ClienteDTO ou FuncionarioDTO)
    // Isso evita vazar a senha hasheada.
    private Object dadosUsuario;
    private String message;

    // Construtores
    public LoginResponse() {}

    public LoginResponse(String token) {
        this.token = token;
    }

    public LoginResponse(String token, String tipoUsuario, Object dadosUsuario, String message) {
        this.token = token;
        this.tipoUsuario = tipoUsuario;
        this.dadosUsuario = dadosUsuario;
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

    public Object getDadosUsuario() {
        return dadosUsuario;
    }

    public void setDadosUsuario(Object dadosUsuario) {
        this.dadosUsuario = dadosUsuario;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}