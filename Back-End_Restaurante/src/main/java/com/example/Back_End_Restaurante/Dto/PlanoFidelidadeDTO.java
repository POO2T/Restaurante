package com.example.Back_End_Restaurante.Dto;

public class PlanoFidelidadeDTO {

    private Long id;
    private String nome;
    private String descricao;
    private Integer pontosNecessarios; // Pontos para atingir este nível

    // Construtores
    public PlanoFidelidadeDTO() {}

    public PlanoFidelidadeDTO(Long id, String nome, String descricao, Integer pontosNecessarios) {
        this.id = id;
        this.nome = nome;
        this.descricao = descricao;
        this.pontosNecessarios = pontosNecessarios;
    }

    // Getters e Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Integer getPontosNecessarios() {
        return pontosNecessarios;
    }

    public void setPontosNecessarios(Integer pontosNecessarios) {
        this.pontosNecessarios = pontosNecessarios;
    }
}