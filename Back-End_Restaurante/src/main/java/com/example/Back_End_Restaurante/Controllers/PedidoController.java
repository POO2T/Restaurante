package com.example.Back_End_Restaurante.Controllers;

import com.example.Back_End_Restaurante.Dto.PedidoCozinhaDTO; // Importar
import com.example.Back_End_Restaurante.Dto.PedidoRequestDTO;
import com.example.Back_End_Restaurante.Dto.PedidoResponseDTO;
import com.example.Back_End_Restaurante.Services.PedidoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
// import org.springframework.security.core.Authentication; // Não é mais necessário no parâmetro

import java.util.List; // Importar

@RestController
@RequestMapping("/api") // A base é /api
// @CrossOrigin(origins = "*") // Removido - Controlado globalmente pelo SecurityConfig
public class PedidoController {

    @Autowired
    private PedidoService pedidoService;

    /**
     * Adiciona um novo pedido (com múltiplos itens) a uma comanda existente.
     * Requer autenticação:
     * - Ou o usuário é um FUNCIONÁRIO (GARCOM, GERENTE, ADMIN)
     * - Ou o usuário é um CLIENTE e é o DONO da comanda (verificado pelo @comandaSecurity)
     */
    // 👇👇👇 AQUI ESTÁ A CORREÇÃO 👇👇👇
    @PostMapping("/comandas/{comandaId}/pedidos")
    @PreAuthorize("hasAnyRole('GARCOM', 'GERENTE', 'ADMINISTRADOR') or @comandaSecurity.checkClienteIsComandaOwner(#comandaId)")
    public ResponseEntity<PedidoResponseDTO> adicionarPedido(
            @PathVariable Long comandaId,
            @RequestBody PedidoRequestDTO pedidoRequest
            // Removemos o 'Authentication authentication' do parâmetro,
            // pois o ComandaSecurity pega a autenticação do Contexto de Segurança.
    ) {

        PedidoResponseDTO pedidoSalvo = pedidoService.adicionarPedido(comandaId, pedidoRequest);
        // Retorna 200 OK (ou 201 Created)
        return ResponseEntity.ok(pedidoSalvo);
    }

    // --- ENDPOINT DA TELA DA COZINHA (NOVO) ---
    /**
     * Lista todos os pedidos que estão com status PENDENTE, em ordem de chegada.
     * Protegido: Apenas funcionários (Cozinheiro, Garçom, Gerente, Admin) podem ver.
     */
    @GetMapping("/pedidos/pendentes")
    @PreAuthorize("hasAnyRole('COZINHEIRO', 'GARCOM', 'GERENTE', 'ADMINISTRADOR')")
    public ResponseEntity<List<PedidoCozinhaDTO>> getPedidosPendentesParaCozinha() {
        List<PedidoCozinhaDTO> pedidos = pedidoService.listarPedidosPendentes();
        return ResponseEntity.ok(pedidos);
    }

    // Futuramente:
    // @DeleteMapping("/pedidos/itens/{itemId}")
    // @PreAuthorize(...)
    // public ResponseEntity<Void> removerItem(...) { ... }

    // @PutMapping("/pedidos/{pedidoId}/status")
    // @PreAuthorize("hasAnyRole('COZINHEIRO', 'GERENTE', 'ADMINISTRADOR')")
    // public ResponseEntity<PedidoResponseDTO> atualizarStatusPedido(...) { ... }
}