package com.example.Back_End_Restaurante.Dto;

import com.example.Back_End_Restaurante.Enums.StatusComanda;

import java.time.LocalDateTime;
import java.util.List;

// Este DTO será usado para ver o "estado" completo de uma comanda
public class ComandaDetalhadaDTO {

    private Long id;
    private LocalDateTime dataAbertura;
    private StatusComanda status;
    private String nomeMesa;
    private Integer numeroMesa;
    private String nomeCliente; // Pode ser nulo se for visitante
    private List<PedidoResponseDTO> pedidos;
    private Double totalComanda;

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public LocalDateTime getDataAbertura() { return dataAbertura; }
    public void setDataAbertura(LocalDateTime dataAbertura) { this.dataAbertura = dataAbertura; }
    public StatusComanda getStatus() { return status; }
    public void setStatus(StatusComanda status) { this.status = status; }
    public String getNomeMesa() { return nomeMesa; }
    public void setNomeMesa(String nomeMesa) { this.nomeMesa = nomeMesa; }
    public Integer getNumeroMesa() { return numeroMesa; }
    public void setNumeroMesa(Integer numeroMesa) { this.numeroMesa = numeroMesa; }
    public String getNomeCliente() { return nomeCliente; }
    public void setNomeCliente(String nomeCliente) { this.nomeCliente = nomeCliente; }
    public List<PedidoResponseDTO> getPedidos() { return pedidos; }
    public void setPedidos(List<PedidoResponseDTO> pedidos) { this.pedidos = pedidos; }
    public Double getTotalComanda() { return totalComanda; }
    public void setTotalComanda(Double totalComanda) { this.totalComanda = totalComanda; }
}