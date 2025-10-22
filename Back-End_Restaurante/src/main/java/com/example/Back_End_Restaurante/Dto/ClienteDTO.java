package com.example.Back_End_Restaurante.Dto;



public class ClienteDTO  {
    private String telefone;
    private String nome;
    private String email;
    private String senha;


    public ClienteDTO() {

    }
    public ClienteDTO(String telefone, String nome, String email) {
        this.telefone = telefone;
        this.nome = nome;
        this.email = email;
    }
    public String getSenha() {
        return senha;
    }
    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getNome() {
        return nome;

    }

    public void setNome(String nome) {
        this.nome = nome;}

    public String getEmail() {
        return email;}

    public void setEmail(String email) {
        this.email = email;
    }
}
