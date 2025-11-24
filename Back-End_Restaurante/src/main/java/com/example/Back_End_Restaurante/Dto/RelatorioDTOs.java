package com.example.Back_End_Restaurante.Dto;

/**
 * Agrupa classes DTO para diferentes tipos de relatórios.
 * Usamos classes estáticas para manter tudo organizado em um único arquivo.
 */
public class RelatorioDTOs {

    // DTO 1: Relatório de Faturamento (Diário/Mensal)
    public static class FaturamentoDTO {
        private String periodo; // ex: "11/11/2025"
        private Double totalFaturado;
        private Long qtdPedidos;

        public FaturamentoDTO(String periodo, Double totalFaturado, Long qtdPedidos) {
            this.periodo = periodo;
            this.totalFaturado = totalFaturado;
            this.qtdPedidos = qtdPedidos;
        }

        public String getPeriodo() { return periodo; }
        public void setPeriodo(String periodo) { this.periodo = periodo; }
        public Double getTotalFaturado() { return totalFaturado; }
        public void setTotalFaturado(Double totalFaturado) { this.totalFaturado = totalFaturado; }
        public Long getQtdPedidos() { return qtdPedidos; }
        public void setQtdPedidos(Long qtdPedidos) { this.qtdPedidos = qtdPedidos; }
    }

    // DTO 2: Relatório de Produtos Mais Vendidos
    public static class ProdutoVendidoDTO {
        private String nomeProduto;
        private Long quantidadeTotal;
        private Double valorTotal;

        public ProdutoVendidoDTO(String nomeProduto, Long quantidadeTotal, Double valorTotal) {
            this.nomeProduto = nomeProduto;
            this.quantidadeTotal = quantidadeTotal;
            this.valorTotal = valorTotal;
        }

        public String getNomeProduto() { return nomeProduto; }
        public void setNomeProduto(String nomeProduto) { this.nomeProduto = nomeProduto; }
        public Long getQuantidadeTotal() { return quantidadeTotal; }
        public void setQuantidadeTotal(Long quantidadeTotal) { this.quantidadeTotal = quantidadeTotal; }
        public Double getValorTotal() { return valorTotal; }
        public void setValorTotal(Double valorTotal) { this.valorTotal = valorTotal; }
    }

    // DTO 3: Dashboard Resumo (Totais gerais para o topo da tela)
    public static class DashboardResumoDTO {
        private Long totalPedidosHoje;
        private Double faturamentoHoje;
        private Long mesasOcupadasAgora;

        public DashboardResumoDTO() {}

        public Long getTotalPedidosHoje() { return totalPedidosHoje; }
        public void setTotalPedidosHoje(Long totalPedidosHoje) { this.totalPedidosHoje = totalPedidosHoje; }
        public Double getFaturamentoHoje() { return faturamentoHoje; }
        public void setFaturamentoHoje(Double faturamentoHoje) { this.faturamentoHoje = faturamentoHoje; }
        public Long getMesasOcupadasAgora() { return mesasOcupadasAgora; }
        public void setMesasOcupadasAgora(Long mesasOcupadasAgora) { this.mesasOcupadasAgora = mesasOcupadasAgora; }
    }
}