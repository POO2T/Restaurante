package com.example.Back_End_Restaurante.Dto;



// @Data, @NoArgsConstructor, @AllArgsConstructor REMOVIDOS
public class MesaDTO {

    private Long id;
    private Integer numero;
    private String nome;
    private String status; // Usamos String no DTO

    // Construtor vazio (padrão)
    public MesaDTO() {
    }

    // Construtor com todos os argumentos (usado no service)
    public MesaDTO(Long id, Integer numero, String nome, String status) {
        this.id = id;
        this.numero = numero;
        this.nome = nome;
        this.status = status;
    }

    // --- Getters e Setters manuais ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getNumero() {
        return numero;
    }

    public void setNumero(Integer numero) {
        this.numero = numero;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}


