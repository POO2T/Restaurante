package com.example.Back_End_Restaurante.Dto;

/**
 * DTO para a Requisição de abertura de comanda (o que o frontend envia).
 */
public class ComandaAberturaRequestDTO {
    private Long mesaId;
    // Futuramente: private String observacao;

    // Getters e Setters
    public Long getMesaId() {
        return mesaId;
    }

    public void setMesaId(Long mesaId) {
        this.mesaId = mesaId;
    }
}


