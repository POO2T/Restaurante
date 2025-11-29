package com.example.Back_End_Restaurante.Controllers;



import com.example.Back_End_Restaurante.Dto.ComandaAberturaRequestDTO;

import com.example.Back_End_Restaurante.Dto.ComandaDetalhadaDTO;

import com.example.Back_End_Restaurante.Dto.ComandaResponseDTO;

import com.example.Back_End_Restaurante.Dto.PagamentoRequestDTO;

import com.example.Back_End_Restaurante.Enums.StatusComanda;

import com.example.Back_End_Restaurante.Model.Comanda;

import com.example.Back_End_Restaurante.Repositorio.ComandaRepository;

import com.example.Back_End_Restaurante.Security.CustomUserDetails;

import com.example.Back_End_Restaurante.Services.ComandaService;

import com.example.Back_End_Restaurante.Services.QrCodeService;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpStatus;

import org.springframework.http.MediaType;

import org.springframework.http.ResponseEntity;

import org.springframework.security.access.prepost.PreAuthorize;

import org.springframework.security.core.Authentication;

import org.springframework.web.bind.annotation.*;

import org.springframework.web.server.ResponseStatusException;



import java.net.URI;

import java.util.List;



@RestController

@RequestMapping("/api/comandas")

public class ComandaController {



    @Autowired

    private ComandaService comandaService;



    @Autowired

    private ComandaRepository comandaRepository;



    @Autowired

    private QrCodeService qrCodeService;



    // --- Endpoints de Abertura ---



    @PostMapping("/visitante")

    @PreAuthorize("permitAll")

    public ResponseEntity<ComandaResponseDTO> abrirComandaVisitante(@RequestBody ComandaAberturaRequestDTO request) {

        ComandaResponseDTO response = comandaService.abrirComandaVisitante(request);

        URI location = URI.create(String.format("/api/comandas/%s", response.getId()));

        return ResponseEntity.created(location).body(response);

    }



    @PostMapping("/autenticada")

    @PreAuthorize("hasRole('CLIENTE')")

    public ResponseEntity<ComandaResponseDTO> abrirComandaAutenticada(

            @RequestBody ComandaAberturaRequestDTO request,

            Authentication authentication

    ) {

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        String emailCliente = userDetails.getUsername();

        ComandaResponseDTO response = comandaService.abrirComandaAutenticada(request, emailCliente);

        URI location = URI.create(String.format("/api/comandas/%s", response.getId()));

        return ResponseEntity.created(location).body(response);

    }



    // --- Endpoints de Gest찾o ---



    @GetMapping("/{comandaId}/detalhes")

    @PreAuthorize("hasAnyRole('GARCOM', 'GERENTE', 'ADMINISTRADOR') or @comandaSecurity.checkClienteIsComandaOwner(#comandaId)")

    public ResponseEntity<ComandaDetalhadaDTO> getDetalhesDaComanda(@PathVariable Long comandaId) {

        ComandaDetalhadaDTO detalhes = comandaService.getDetalhesComanda(comandaId);

        return ResponseEntity.ok(detalhes);

    }



    @PostMapping("/{comandaId}/pagar")

    @PreAuthorize("hasAnyRole('GARCOM', 'GERENTE', 'ADMINISTRADOR') or @comandaSecurity.checkClienteIsComandaOwner(#comandaId)")

    public ResponseEntity<ComandaDetalhadaDTO> fecharComanda(@PathVariable Long comandaId, @RequestBody PagamentoRequestDTO pagamentoRequest) {

        ComandaDetalhadaDTO comandaFechada = comandaService.fecharComanda(comandaId, pagamentoRequest);

        return ResponseEntity.ok(comandaFechada);

    }



    @GetMapping("/meu-historico")

    @PreAuthorize("hasRole('CLIENTE')")

    public ResponseEntity<List<ComandaDetalhadaDTO>> getHistoricoCliente(Authentication authentication) {

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        String emailCliente = userDetails.getUsername();

        List<ComandaDetalhadaDTO> historico = comandaService.listarHistoricoCliente(emailCliente);

        return ResponseEntity.ok(historico);

    }



    // --- NOVO ENDPOINT: QR Code ---

    

    /**

     * Gera um QR Code para comprovar que a comanda foi paga.

     * S처 funciona se a comanda estiver FECHADA.

     */

    @GetMapping(value = "/{id}/qrcode", produces = MediaType.IMAGE_PNG_VALUE)

    @PreAuthorize("hasAnyRole('GARCOM', 'GERENTE', 'ADMINISTRADOR') or @comandaSecurity.checkClienteIsComandaOwner(#id)")

    public ResponseEntity<byte[]> gerarQrCodePagamento(@PathVariable Long id) {

        try {

            Comanda comanda = comandaRepository.findById(id)

                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Comanda n찾o encontrada"));



            if (comanda.getStatus() != StatusComanda.FECHADA) {

                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A comanda ainda n찾o foi paga/fechada.");

            }



            String conteudoQrCode = String.format(

                    "CONFIRMACAO_PAGAMENTO|ID:%d|MESA:%d|VALOR:%.2f|DATA:%s|STATUS:%s",

                    comanda.getId(),

                    comanda.getMesa().getNumero(),

                    comanda.getPagamentos().stream().mapToDouble(p -> p.getValor()).sum(),

                    comanda.getDataFechamento() != null ? comanda.getDataFechamento().toString() : "N/A",

                    comanda.getStatus()

            );



            byte[] qrCodeImage = qrCodeService.generateQrCodeImage(conteudoQrCode, 300, 300);



            return ResponseEntity.ok().body(qrCodeImage);



        } catch (Exception e) {

            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erro ao gerar QR Code: " + e.getMessage());

        }

    }

}