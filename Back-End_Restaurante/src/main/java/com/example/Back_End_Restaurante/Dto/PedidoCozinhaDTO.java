package com.example.Back_End_Restaurante.Dto;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO que representa um Pedido na tela da Cozinha.
 */
 public class PedidoCozinhaDTO {
    private Long pedidoId;
    private LocalDateTime dataHora;
    private String nomeMesa;
    private Integer numeroMesa;
    private List<ItemPedidoCozinhaDTO> itens;

    // Getters e Setters
    public Long getPedidoId() { return pedidoId; }
    public void setPedidoId(Long pedidoId) { this.pedidoId = pedidoId; }
    public LocalDateTime getDataHora() { return dataHora; }
    public void setDataHora(LocalDateTime dataHora) { this.dataHora = dataHora; }
    public String getNomeMesa() { return nomeMesa; }
    public void setNomeMesa(String nomeMesa) { this.nomeMesa = nomeMesa; }
    public Integer getNumeroMesa() { return numeroMesa; }
    public void setNumeroMesa(Integer numeroMesa) { this.numeroMesa = numeroMesa; }
    public List<ItemPedidoCozinhaDTO> getItens() { return itens; }
    public void setItens(List<ItemPedidoCozinhaDTO> itens) { this.itens = itens; }
}
