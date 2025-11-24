package com.example.Back_End_Restaurante.Dto;

import com.example.Back_End_Restaurante.Enums.FormaPagamento;
import java.time.LocalDateTime;

// --- DTO para a RESPOSTA de Pagamento ---
// (O que o backend retorna)

public class PagamentoResponseDTO {
    private Long id;
    private LocalDateTime dataPagamento;
    private Double valor;
    private FormaPagamento forma;
    private Long comandaId;

    // Getters e Setters (Garanti que todos são public)
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public LocalDateTime getDataPagamento() { return dataPagamento; }
    public void setDataPagamento(LocalDateTime dataPagamento) { this.dataPagamento = dataPagamento; }

    public Double getValor() { return valor; }
    public void setValor(Double valor) { this.valor = valor; }

    public FormaPagamento getForma() { return forma; }
    public void setForma(FormaPagamento forma) { this.forma = forma; }

    public Long getComandaId() { return comandaId; }
    public void setComandaId(Long comandaId) { this.comandaId = comandaId; }
}