package com.example.Back_End_Restaurante.Dto;

import com.example.Back_End_Restaurante.Enums.StatusPedido;

import java.time.LocalDateTime;
import java.util.List;

public class PedidoResponseDTO {
    private Long id;
    private LocalDateTime dataHora;
    private StatusPedido status;
    private List<ItemPedidoResponseDTO> itens;
    private Double totalPedido;

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public LocalDateTime getDataHora() { return dataHora; }
    public void setDataHora(LocalDateTime dataHora) { this.dataHora = dataHora; }
    public StatusPedido getStatus() { return status; }
    public void setStatus(StatusPedido status) { this.status = status; }
    public List<ItemPedidoResponseDTO> getItens() { return itens; }
    public void setItens(List<ItemPedidoResponseDTO> itens) { this.itens = itens; }
    public Double getTotalPedido() { return totalPedido; }
    public void setTotalPedido(Double totalPedido) { this.totalPedido = totalPedido; }
}