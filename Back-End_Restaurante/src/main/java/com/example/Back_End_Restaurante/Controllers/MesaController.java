package com.example.Back_End_Restaurante.Controllers;

import com.example.Back_End_Restaurante.Dto.MesaDTO;
import com.example.Back_End_Restaurante.Services.MesaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize; // <<< IMPORTANTE PARA SEGURANÇA
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/mesas")
// @CrossOrigin(origins = "*") // Removido - Agora é controlado globalmente pelo SecurityConfig
public class MesaController {

    @Autowired
    private MesaService mesaService;

    // --- ENDPOINTS PÚBLICOS (QUALQUER UM PODE VER) ---

    @GetMapping
    // Todos (inclusive clientes) podem ver as mesas e seus status
    public ResponseEntity<List<MesaDTO>> listarTodasMesas() {
        List<MesaDTO> mesas = mesaService.listarTodas();
        return ResponseEntity.ok(mesas);
    }

    @GetMapping("/{id}")
    // Todos podem ver uma mesa específica
    public ResponseEntity<MesaDTO> buscarMesaPorId(@PathVariable Long id) {
        MesaDTO mesa = mesaService.buscarPorId(id);
        return ResponseEntity.ok(mesa);
    }

    // --- ENDPOINTS RESTRITOS (SÓ FUNCIONÁRIOS) ---

    @PostMapping
    // Somente GERENTE ou ADMINISTRADOR podem CRIAR mesas
    @PreAuthorize("hasAnyRole('GERENTE', 'ADMINISTRADOR')")
    public ResponseEntity<MesaDTO> criarMesa(@RequestBody MesaDTO mesaDTO, UriComponentsBuilder uriBuilder) {
        MesaDTO mesaSalva = mesaService.criarMesa(mesaDTO);
        URI uri = uriBuilder.path("/api/mesas/{id}").buildAndExpand(mesaSalva.getId()).toUri();
        return ResponseEntity.created(uri).body(mesaSalva);
    }

    @PutMapping("/{id}")
    // Somente GERENTE ou ADMINISTRADOR podem ATUALIZAR (mudar número/nome)
    @PreAuthorize("hasAnyRole('GERENTE', 'ADMINISTRADOR')")
    public ResponseEntity<MesaDTO> atualizarMesa(@PathVariable Long id, @RequestBody MesaDTO mesaDTO) {
        MesaDTO mesaAtualizada = mesaService.atualizarMesa(id, mesaDTO);
        return ResponseEntity.ok(mesaAtualizada);
    }

    @PutMapping("/{id}/status")
    // Garçom, Gerente ou Admin podem mudar o STATUS (ex: Ocupar, Liberar)
    @PreAuthorize("hasAnyRole('GARCOM', 'GERENTE', 'ADMINISTRADOR')")
    public ResponseEntity<MesaDTO> atualizarStatusMesa(@PathVariable Long id, @RequestBody Map<String, String> statusMap) {
        String status = statusMap.get("status");
        if (status == null) {
            return ResponseEntity.badRequest().build(); // Retorna 400 se o JSON não tiver a chave "status"
        }
        MesaDTO mesaAtualizada = mesaService.atualizarStatusMesa(id, status);
        return ResponseEntity.ok(mesaAtualizada);
    }

    @DeleteMapping("/{id}")
    // Somente GERENTE ou ADMINISTRADOR podem DELETAR mesas
    @PreAuthorize("hasAnyRole('GERENTE', 'ADMINISTRADOR')")
    public ResponseEntity<Void> deletarMesa(@PathVariable Long id) {
        mesaService.deletarMesa(id);
        return ResponseEntity.noContent().build();
    }
}