package com.example.Back_End_Restaurante.Dto;



import com.fasterxml.jackson.annotation.JsonProperty;

public class ClienteDTO {

    private Long id; // <<< ADICIONADO PARA RESPOSTAS
    private String nome;
    private String email;
    private String telefone;

    // Usamos @JsonProperty para garantir que o campo 'senha'
    // possa ser recebido (write-only), mas não será enviado de volta (read=false)
    // No entanto, como nosso 'converterParaDTO' já omite, deixamos simples
    private String senha;


    // Construtores
    public ClienteDTO() {
    }

    public ClienteDTO(Long id, String nome, String email, String telefone) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.telefone = telefone;
    }

    // --- Getters e Setters ---

    public Long getId() { // <<< ADICIONADO
        return id;
    }

    public void setId(Long id) { // <<< ADICIONADO
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getSenha() { // <<< ADICIONADO
        return senha;
    }

    public void setSenha(String senha) { // <<< ADICIONADO
        this.senha = senha;
    }
}