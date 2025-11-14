package com.example.Back_End_Restaurante.Dto;

// --- DTO para a REQUISIÇÃO de UM item ---
public class ItemPedidoRequestDTO {
    private Long produtoId;
    private Integer quantidade;

    // Getters e Setters
    public Long getProdutoId() { return produtoId; }
    public void setProdutoId(Long produtoId) { this.produtoId = produtoId; }
    public Integer getQuantidade() { return quantidade; }
    public void setQuantidade(Integer quantidade) { this.quantidade = quantidade; }
}