package com.example.Back_End_Restaurante.Controllers;

import com.example.Back_End_Restaurante.Dto.ComandaAberturaRequestDTO;
import com.example.Back_End_Restaurante.Dto.ComandaResponseDTO;
import com.example.Back_End_Restaurante.Security.CustomUserDetails;
import com.example.Back_End_Restaurante.Services.ComandaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/comandas")
public class ComandaController {

    @Autowired
    private ComandaService comandaService;

    /**
     * Endpoint para ABRIR uma comanda como VISITANTE (anônimo).
     * Público, não requer autenticação.
     */
    @PostMapping("/visitante")
    @PreAuthorize("permitAll") // Permite acesso público
    public ResponseEntity<ComandaResponseDTO> abrirComandaVisitante(@RequestBody ComandaAberturaRequestDTO request) {
        ComandaResponseDTO response = comandaService.abrirComandaVisitante(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Endpoint para ABRIR uma comanda como CLIENTE (logado).
     * Requer autenticação (JWT) e o usuário deve ter a role CLIENTE.
     */
    @PostMapping("/autenticada")
    @PreAuthorize("hasRole('CLIENTE')") // Só permite acesso se for um CLIENTE logado
    public ResponseEntity<ComandaResponseDTO> abrirComandaAutenticada(
            @RequestBody ComandaAberturaRequestDTO request,
            Authentication authentication // Spring injeta os dados do usuário logado (do token)
    ) {
        // Pega o CustomUserDetails que criamos, que contém o email (username)
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        String emailCliente = userDetails.getUsername();

        ComandaResponseDTO response = comandaService.abrirComandaAutenticada(request, emailCliente);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // --- Outros Endpoints Futuros ---
    // GET /api/comandas/{id} (Para buscar detalhes da comanda)
    // POST /api/comandas/{id}/adicionar-item (Requisito #3)
    // POST /api/comandas/{id}/fechar (Requisito #4)
    // POST /api/comandas/{id}/pagar (Requisito #9)
    // GET /api/comandas/minhas-comandas (Para histórico do cliente)
}