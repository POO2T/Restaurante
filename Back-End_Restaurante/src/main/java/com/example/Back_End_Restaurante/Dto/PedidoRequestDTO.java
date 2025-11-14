package com.example.Back_End_Restaurante.Dto;

import java.util.List;

// --- DTO para a REQUISIÇÃO de um Pedido ---
public class PedidoRequestDTO {
    private List<ItemPedidoRequestDTO> itens;

    // Getters e Setters
    public List<ItemPedidoRequestDTO> getItens() { return itens; }
    public void setItens(List<ItemPedidoRequestDTO> itens) { this.itens = itens; }
}

