package com.example.Back_End_Restaurante.Dto;

import java.time.LocalDateTime;

/**
 * DTO para a Resposta da comanda (o que o backend devolve).
 */
public class ComandaResponseDTO {
    private Long id;
    private String status;
    private LocalDateTime dataAbertura;
    private Long mesaId;
    private Long clienteId; // Pode ser null se for visitante
    private Integer numeroMesa; // Útil para o frontend

    // Construtores
    public ComandaResponseDTO() {}

    public ComandaResponseDTO(Long id, String status, LocalDateTime dataAbertura, Long mesaId, Long clienteId, Integer numeroMesa) {
        this.id = id;
        this.status = status;
        this.dataAbertura = dataAbertura;
        this.mesaId = mesaId;
        this.clienteId = clienteId;
        this.numeroMesa = numeroMesa;
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getDataAbertura() { return dataAbertura; }
    public void setDataAbertura(LocalDateTime dataAbertura) { this.dataAbertura = dataAbertura; }
    public Long getMesaId() { return mesaId; }
    public void setMesaId(Long mesaId) { this.mesaId = mesaId; }
    public Long getClienteId() { return clienteId; }
    public void setClienteId(Long clienteId) { this.clienteId = clienteId; }
    public Integer getNumeroMesa() { return numeroMesa; }
    public void setNumeroMesa(Integer numeroMesa) { this.numeroMesa = numeroMesa; }
}