package com.example.Back_End_Restaurante.Dto;

public class FuncionarioDTO  {
    private String nome;
    private String email;
    private String cargo;
    private double salario;
    private boolean ativo;
    private String senha;


    // Adicione um construtor vazio para o Spring (Jackson)
    public FuncionarioDTO() {
    }

    // Construtor completo
    public FuncionarioDTO(String senha ,String nome, String email, String cargo, double salario, boolean ativo) {
        this.nome = nome;
        this.senha = senha;
        this.email = email;
        this.cargo = cargo;
        this.salario = salario;
        this.ativo = ativo;
    }

    // Getters e Setters...

    public String getCargo() { return cargo; }
    public void setCargo(String cargo) { this.cargo = cargo; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getEmail() { return email; }

    // SEU MÉTODO ESTAVA VAZIO. CORRIGIDO:
    public void setEmail(String email) { this.email = email; }

    // GETTERS E SETTERS PARA OS NOVOS CAMPOS
    public double getSalario() { return salario; }
    public void setSalario(double salario) { this.salario = salario; }
    public boolean isAtivo() { return ativo; }
    public void setAtivo(boolean ativo) { this.ativo = ativo; }

    public String getSenha() {
        return senha;
    }
    public void setSenha(String senha) {
        this.senha = senha;
    }
}