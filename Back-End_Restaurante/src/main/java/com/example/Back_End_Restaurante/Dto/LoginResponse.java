package com.example.Back_End_Restaurante.Dto;

public class LoginResponse {
    private String token;
    private Object usuario; // pode ser ClienteDTO ou FuncionarioDTO
    private String tipoUsuario;
    private long expiresIn;

    public LoginResponse() {}

    public LoginResponse(String token, Object usuario, String tipoUsuario, long expiresIn) {
        this.token = token;
        this.usuario = usuario;
        this.tipoUsuario = tipoUsuario;
        this.expiresIn = expiresIn;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Object getUsuario() {
        return usuario;
    }

    public void setUsuario(Object usuario) {
        this.usuario = usuario;
    }

    public String getTipoUsuario() {
        return tipoUsuario;
    }

    public void setTipoUsuario(String tipoUsuario) {
        this.tipoUsuario = tipoUsuario;
    }

    public long getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(long expiresIn) {
        this.expiresIn = expiresIn;
    }
}
