package com.example.Back_End_Restaurante.Dto;

// --- DTOs PARA A TELA DA COZINHA (NOVOS) ---

/**
 * DTO simplificado para um item na tela da Cozinha.
 */
public class ItemPedidoCozinhaDTO {
    private String nomeProduto;
    private Integer quantidade;
    // Futuramente: private String observacao;

    // Getters e Setters
    public String getNomeProduto() { return nomeProduto; }
    public void setNomeProduto(String nomeProduto) { this.nomeProduto = nomeProduto; }
    public Integer getQuantidade() { return quantidade; }
    public void setQuantidade(Integer quantidade) { this.quantidade = quantidade; }
}