package com.example.Back_End_Restaurante.Dto;

public class ClienteDTO {

    private Long id;
    private String nome;
    private String email;
    private String telefone;
    private String senha; // Write-only (só para cadastro)

    // --- NOVO CAMPO ---
    private Integer pontosFidelidade;
    // ------------------

    // Construtores
    public ClienteDTO() {}

    public ClienteDTO(Long id, String nome, String email, String telefone, Integer pontosFidelidade) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.telefone = telefone;
        this.pontosFidelidade = pontosFidelidade;
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }
    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }

    public Integer getPontosFidelidade() { return pontosFidelidade; }
    public void setPontosFidelidade(Integer pontosFidelidade) { this.pontosFidelidade = pontosFidelidade; }
}