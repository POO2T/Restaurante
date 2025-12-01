package com.example.Back_End_Restaurante.Controllers;

import com.example.Back_End_Restaurante.Dto.ComandaAberturaRequestDTO;
import com.example.Back_End_Restaurante.Dto.ComandaDetalhadaDTO;
import com.example.Back_End_Restaurante.Dto.ComandaResponseDTO;
import com.example.Back_End_Restaurante.Dto.PagamentoRequestDTO;
import com.example.Back_End_Restaurante.Security.CustomUserDetails;
import com.example.Back_End_Restaurante.Services.ComandaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/comandas")
public class ComandaController {

    @Autowired
    private ComandaService comandaService;

    // ... (Endpoints de Abertura MANTIDOS) ...
    @PostMapping("/visitante")
    @PreAuthorize("permitAll")
    public ResponseEntity<ComandaResponseDTO> abrirComandaVisitante(@RequestBody ComandaAberturaRequestDTO request) {
        ComandaResponseDTO response = comandaService.abrirComandaVisitante(request);
        URI location = URI.create(String.format("/api/comandas/%s", response.getId()));
        return ResponseEntity.created(location).body(response);
    }

    @PostMapping("/autenticada")
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<ComandaResponseDTO> abrirComandaAutenticada(@RequestBody ComandaAberturaRequestDTO request,
            Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        String emailCliente = userDetails.getUsername();
        ComandaResponseDTO response = comandaService.abrirComandaAutenticada(request, emailCliente);
        URI location = URI.create(String.format("/api/comandas/%s", response.getId()));
        return ResponseEntity.created(location).body(response);
    }

    // ... (Endpoint Detalhes MANTIDO) ...
    @GetMapping("/{comandaId}/detalhes")
    @PreAuthorize("hasAnyRole('GARCOM', 'GERENTE', 'ADMINISTRADOR', null) or @comandaSecurity.checkClienteIsComandaOwner(#comandaId)")
    public ResponseEntity<ComandaDetalhadaDTO> getDetalhesDaComanda(@PathVariable Long comandaId) {
        ComandaDetalhadaDTO detalhes = comandaService.getDetalhesComanda(comandaId);
        return ResponseEntity.ok(detalhes);
    }

    // ... (Endpoint Pagar MANTIDO) ...
    @PostMapping("/{comandaId}/pagar")
    @PreAuthorize("hasAnyRole('GARCOM', 'GERENTE', 'ADMINISTRADOR') or @comandaSecurity.checkClienteIsComandaOwner(#comandaId)")
    public ResponseEntity<ComandaDetalhadaDTO> fecharComanda(@PathVariable Long comandaId,
            @RequestBody PagamentoRequestDTO pagamentoRequest) {
        ComandaDetalhadaDTO comandaFechada = comandaService.fecharComanda(comandaId, pagamentoRequest);
        return ResponseEntity.ok(comandaFechada);
    }

    // --- NOVO ENDPOINT: Histórico do Cliente ---
    /**
     * Lista todas as comandas (abertas e fechadas) do cliente logado.
     */
    @GetMapping("/meu-historico")
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<List<ComandaDetalhadaDTO>> getHistoricoCliente(Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        String emailCliente = userDetails.getUsername();

        List<ComandaDetalhadaDTO> historico = comandaService.listarHistoricoCliente(emailCliente);
        return ResponseEntity.ok(historico);
    }
}