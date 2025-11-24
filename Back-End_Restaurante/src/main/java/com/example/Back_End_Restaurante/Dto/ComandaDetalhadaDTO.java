package com.example.Back_End_Restaurante.Dto;

// 6. ComandaDetalhadaDTO (Atualizado)
// Adicionamos o total e a lista de pagamentos

import com.example.Back_End_Restaurante.Enums.StatusComanda;
import java.time.LocalDateTime;
import java.util.List;

public class ComandaDetalhadaDTO {

    private Long id;
    private LocalDateTime dataAbertura;
    private LocalDateTime dataFechamento; // NOVO
    private StatusComanda status;
    private String nomeMesa;
    private Integer numeroMesa;
    private String nomeCliente; // Pode ser nulo se for visitante
    private List<PedidoResponseDTO> pedidos;
    private List<PagamentoResponseDTO> pagamentos; // NOVO
    private Double totalCalculado; // NOVO
    private Double totalPago; // NOVO
    private Double saldoPendente; // NOVO

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public LocalDateTime getDataAbertura() { return dataAbertura; }
    public void setDataAbertura(LocalDateTime dataAbertura) { this.dataAbertura = dataAbertura; }
    public LocalDateTime getDataFechamento() { return dataFechamento; } // NOVO
    public void setDataFechamento(LocalDateTime dataFechamento) { this.dataFechamento = dataFechamento; } // NOVO
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
    public List<PagamentoResponseDTO> getPagamentos() { return pagamentos; } // NOVO
    public void setPagamentos(List<PagamentoResponseDTO> pagamentos) { this.pagamentos = pagamentos; } // NOVO
    public Double getTotalCalculado() { return totalCalculado; } // NOVO
    public void setTotalCalculado(Double totalCalculado) { this.totalCalculado = totalCalculado; } // NOVO
    public Double getTotalPago() { return totalPago; } // NOVO
    public void setTotalPago(Double totalPago) { this.totalPago = totalPago; } // NOVO
    public Double getSaldoPendente() { return saldoPendente; } // NOVO
    public void setSaldoPendente(Double saldoPendente) { this.saldoPendente = saldoPendente; } // NOVO
}