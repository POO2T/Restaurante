package com.example.Back_End_Restaurante.Services;

import com.example.Back_End_Restaurante.Dto.RelatorioDTOs;
import com.example.Back_End_Restaurante.Enums.StatusMesa;
import com.example.Back_End_Restaurante.Repositorio.ComandaRepository;
import com.example.Back_End_Restaurante.Repositorio.ItemPedidoRepository;
import com.example.Back_End_Restaurante.Repositorio.MesaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RelatorioService {

    @Autowired
    private ComandaRepository comandaRepository;

    @Autowired
    private ItemPedidoRepository itemPedidoRepository;

    @Autowired
    private MesaRepository mesaRepository;

    // Gera um resumo rápido para o dashboard (Dados de HOJE)
    public RelatorioDTOs.DashboardResumoDTO getResumoHoje() {
        LocalDateTime inicioDia = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        LocalDateTime fimDia = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);

        // Busca vendas e faturamento de hoje
        Long totalPedidos = comandaRepository.countVendasNoPeriodo(inicioDia, fimDia);
        Double faturamento = comandaRepository.sumFaturamentoNoPeriodo(inicioDia, fimDia);

        // Conta quantas mesas estão ocupadas AGORA
        Long mesasOcupadas = mesaRepository.findAll().stream()
                .filter(m -> m.getStatus() == StatusMesa.OCUPADA)
                .count();

        RelatorioDTOs.DashboardResumoDTO resumo = new RelatorioDTOs.DashboardResumoDTO();
        resumo.setTotalPedidosHoje(totalPedidos);
        resumo.setFaturamentoHoje(faturamento);
        resumo.setMesasOcupadasAgora(mesasOcupadas);

        return resumo;
    }

    // Lista os top 5 produtos mais vendidos
    public List<RelatorioDTOs.ProdutoVendidoDTO> getProdutosMaisVendidos() {
        return itemPedidoRepository.findTopSellingProducts().stream()
                .limit(5) // Pega apenas os top 5
                .collect(Collectors.toList());
    }
}