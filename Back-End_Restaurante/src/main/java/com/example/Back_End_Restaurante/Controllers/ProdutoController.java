package com.example.Back_End_Restaurante.Controllers;

import com.example.Back_End_Restaurante.Dto.ProdutoRequestDTO;
import com.example.Back_End_Restaurante.Dto.ProdutoResponseDTO;
import com.example.Back_End_Restaurante.Services.ProdutoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/produtos")
public class ProdutoController {

    @Autowired
    private ProdutoService produtoService;

    // --- ENDPOINTS PÚBLICOS (Cardápio) ---

    @GetMapping
    @PreAuthorize("permitAll")
    public ResponseEntity<List<ProdutoResponseDTO>> listarTodosProdutos() {
        return ResponseEntity.ok(produtoService.listarTodos());
    }

    @GetMapping("/categoria/{categoriaId}")
    @PreAuthorize("permitAll")
    public ResponseEntity<List<ProdutoResponseDTO>> listarProdutosPorCategoria(@PathVariable Long categoriaId) {
        return ResponseEntity.ok(produtoService.listarPorCategoria(categoriaId));
    }

    @GetMapping("/{id}")
    @PreAuthorize("permitAll")
    public ResponseEntity<ProdutoResponseDTO> buscarProdutoPorId(@PathVariable Long id) {
        return ResponseEntity.ok(produtoService.buscarPorId(id));
    }

    // --- ENDPOINTS RESTRITOS (Gerenciamento) ---

    @PostMapping
    @PreAuthorize("hasAnyRole('GERENTE', 'ADMINISTRADOR')")
    public ResponseEntity<ProdutoResponseDTO> criarProduto(@RequestBody ProdutoRequestDTO dto, UriComponentsBuilder uriBuilder) {
        ProdutoResponseDTO salvo = produtoService.criarProduto(dto);
        URI uri = uriBuilder.path("/api/produtos/{id}").buildAndExpand(salvo.getId()).toUri();
        return ResponseEntity.created(uri).body(salvo);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('GERENTE', 'ADMINISTRADOR')")
    public ResponseEntity<ProdutoResponseDTO> atualizarProduto(@PathVariable Long id, @RequestBody ProdutoRequestDTO dto) {
        ProdutoResponseDTO atualizado = produtoService.atualizarProduto(id, dto);
        return ResponseEntity.ok(atualizado);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('GERENTE', 'ADMINISTRADOR')")
    public ResponseEntity<Void> deletarProduto(@PathVariable Long id) {
        produtoService.deletarProduto(id);
        return ResponseEntity.noContent().build();
    }
}