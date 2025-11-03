package com.example.Back_End_Restaurante.Dto;

public class LoginRequest {
    private String email;
    private String senha;

    // Construtor vazio (opcional, mas bom ter)
    public LoginRequest() {
    }

    // Getters são ESSENCIAIS para o Jackson (a biblioteca que o Spring usa
    // para converter JSON em objetos Java) funcionar corretamente.
    public String getEmail() {
        return email;
    }

    public String getSenha() {
        return senha;
    }

    // Setters são úteis se você precisar criar ou modificar
    // um objeto LoginRequest no código, mas não são estritamente
    // necessários apenas para receber o JSON.
    public void setEmail(String email) {
        this.email = email;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }
}
