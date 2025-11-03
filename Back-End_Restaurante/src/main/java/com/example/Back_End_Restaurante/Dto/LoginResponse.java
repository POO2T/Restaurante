package com.example.Back_End_Restaurante.Dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;




public class LoginResponse {
    private String token; // Campo para armazenar o token

    // --- CONSTRUTOR PADRÃO (SEM ARGUMENTOS) ---
    // É uma boa prática ter um construtor padrão,
    // especialmente se você usar frameworks como Jackson/JPA.
    public LoginResponse() {
    }

    // --- CONSTRUTOR QUE ACEITA O TOKEN ---
    // Este é o construtor que o AuthController está tentando chamar.
    // Certifique-se de que ele existe e está público.
    public LoginResponse(String token) {
        this.token = token;
    }

    // --- GETTER para o token ---
    // Necessário para que o Spring/Jackson possa converter
    // este objeto em JSON na resposta HTTP.
    public String getToken() {
        return token;
    }

    // --- SETTER para o token (Opcional) ---
    // Pode ser útil em outras situações.
    public void setToken(String token) {
        this.token = token;
    }
}
