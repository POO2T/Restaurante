package com.example.Back_End_Restaurante.Controllers;

import com.example.Back_End_Restaurante.Dto.RelatorioDTOs;
import com.example.Back_End_Restaurante.Services.RelatorioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/relatorios")
public class RelatorioController {

    @Autowired
    private RelatorioService relatorioService;

    /**
     * Retorna o resumo do dia (faturamento, pedidos, mesas ocupadas).
     * Endpoint protegido: Apenas GERENTE ou ADMINISTRADOR.
     */
    @GetMapping("/dashboard")
    @PreAuthorize("hasAnyRole('GERENTE', 'ADMINISTRADOR')")
    public ResponseEntity<RelatorioDTOs.DashboardResumoDTO> getDashboard() {
        return ResponseEntity.ok(relatorioService.getResumoHoje());
    }

    /**
     * Retorna a lista dos produtos mais vendidos.
     * Endpoint protegido: Apenas GERENTE ou ADMINISTRADOR.
     */
    @GetMapping("/produtos-mais-vendidos")
    @PreAuthorize("hasAnyRole('GERENTE', 'ADMINISTRADOR')")
    public ResponseEntity<List<RelatorioDTOs.ProdutoVendidoDTO>> getTopProdutos() {
        return ResponseEntity.ok(relatorioService.getProdutosMaisVendidos());
    }
}