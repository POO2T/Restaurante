package com.example.Back_End_Restaurante.Dto;

// DTO para a REQUISIÇÃO de Pagamento
// (O que o frontend envia)
public class PagamentoRequestDTO {
    private String formaPagamento; // "PIX", "DINHEIRO", "CREDITO", "DEBITO"
    private Double valorPago; // O valor que está sendo pago

    // Getters e Setters (já são public por padrão)
    public String getFormaPagamento() {
        return formaPagamento;
    }
    public void setFormaPagamento(String formaPagamento) {
        this.formaPagamento = formaPagamento;
    }
    public Double getValorPago() {
        return valorPago;
    }
    public void setValorPago(Double valorPago) {
        this.valorPago = valorPago;
    }
}