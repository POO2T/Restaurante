package com.example.Back_End_Restaurante.Dto;

public  class FuncionarioDTO {

    private Long id; // <<< ADICIONADO PARA RESPOSTAS
    private String nome;
    private String email;
    private String senha; // <<< PARA REQUISIÇÕES (Cadastro/Atualização)
    private String tipoFuncionario; // String para simplificar a entrada (ex: "GERENTE")
    private double salario;
    private boolean ativo;

    // Construtor vazio (necessário para o Spring/Jackson)
    public FuncionarioDTO() {
    }

    // Construtor completo (útil para testes ou lógica interna)
    // ATENÇÃO: A ordem aqui deve corresponder à ordem no 'converterParaDTO' se for usado.
    public FuncionarioDTO(Long id, String nome, String email, String tipoFuncionario, double salario, boolean ativo) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.tipoFuncionario = tipoFuncionario;
        this.salario = salario;
        this.ativo = ativo;
        // Note: A senha não é incluída no construtor de resposta padrão
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

    public String getSenha() { // <<< ADICIONADO
        return senha;
    }

    public void setSenha(String senha) { // <<< ADICIONADO
        this.senha = senha;
    }

    public String gettipoFuncionario() {
        return tipoFuncionario;
    }

    public void settipoFuncionario(String tipoFuncionario) {
        this.tipoFuncionario = tipoFuncionario;
    }

    public double getSalario() {
        return salario;
    }

    public void setSalario(double salario) {
        this.salario = salario;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }
}