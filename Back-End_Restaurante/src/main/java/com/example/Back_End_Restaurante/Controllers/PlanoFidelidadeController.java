package com.example.Back_End_Restaurante.Controllers;

import com.example.Back_End_Restaurante.Dto.PlanoFidelidadeDTO;
import com.example.Back_End_Restaurante.Services.PlanoFidelidadeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/planos-fidelidade")
public class PlanoFidelidadeController {

    @Autowired
    private PlanoFidelidadeService planoService;

    // Público: Todos podem ver os planos disponíveis
    @GetMapping
    @PreAuthorize("permitAll")
    public ResponseEntity<List<PlanoFidelidadeDTO>> listarPlanos() {
        return ResponseEntity.ok(planoService.listarTodos());
    }

    @GetMapping("/{id}")
    @PreAuthorize("permitAll")
    public ResponseEntity<PlanoFidelidadeDTO> buscarPlano(@PathVariable Long id) {
        return ResponseEntity.ok(planoService.buscarPorId(id));
    }

    // Restrito: Apenas Gerente/Admin pode gerenciar planos
    @PostMapping
    @PreAuthorize("hasAnyRole('GERENTE', 'ADMINISTRADOR')")
    public ResponseEntity<PlanoFidelidadeDTO> criarPlano(@RequestBody PlanoFidelidadeDTO dto, UriComponentsBuilder uriBuilder) {
        PlanoFidelidadeDTO salvo = planoService.criarPlano(dto);
        URI uri = uriBuilder.path("/api/planos-fidelidade/{id}").buildAndExpand(salvo.getId()).toUri();
        return ResponseEntity.created(uri).body(salvo);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('GERENTE', 'ADMINISTRADOR')")
    public ResponseEntity<PlanoFidelidadeDTO> atualizarPlano(@PathVariable Long id, @RequestBody PlanoFidelidadeDTO dto) {
        return ResponseEntity.ok(planoService.atualizarPlano(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('GERENTE', 'ADMINISTRADOR')")
    public ResponseEntity<Void> deletarPlano(@PathVariable Long id) {
        planoService.deletarPlano(id);
        return ResponseEntity.noContent().build();
    }
}