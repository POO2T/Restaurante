package com.example.Back_End_Restaurante.Dto;

import com.example.Back_End_Restaurante.Enums.StatusPedido;
import java.time.LocalDateTime;
import java.util.List;

// --- DTO para a RESPOSTA de um item de pedido ---
// (Usado em ComandaDetalhadaDTO)
public class ItemPedidoResponseDTO {
    private Long id;
    private Integer quantidade;
    private Double precoUnitario;
    private String nomeProduto;

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Integer getQuantidade() { return quantidade; }
    public void setQuantidade(Integer quantidade) { this.quantidade = quantidade; }
    public Double getPrecoUnitario() { return precoUnitario; }
    public void setPrecoUnitario(Double precoUnitario) { this.precoUnitario = precoUnitario; }
    public String getNomeProduto() { return nomeProduto; }
    public void setNomeProduto(String nomeProduto) { this.nomeProduto = nomeProduto; }
}
