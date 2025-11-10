package com.example.Back_End_Restaurante.Controllers;

import com.example.Back_End_Restaurante.Dto.CategoriaDTO;
import com.example.Back_End_Restaurante.Model.Categoria;
import com.example.Back_End_Restaurante.Services.CategoriaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/categorias")
public class CategoriaController {

    @Autowired
    private CategoriaService categoriaService;

    // ENDPOINT PÚBLICO: Listar todas as categorias
    @GetMapping
    @PreAuthorize("permitAll")
    public ResponseEntity<List<CategoriaDTO>> listarTodas() {
        return ResponseEntity.ok(categoriaService.listarTodas());
    }

    // ENDPOINT PÚBLICO: Buscar categoria por ID
    @GetMapping("/{id}")
    @PreAuthorize("permitAll")
    public ResponseEntity<CategoriaDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(categoriaService.buscarPorId(id));
    }

    // ENDPOINT RESTRITO: Criar nova categoria
    @PostMapping
    @PreAuthorize("hasAnyRole('GERENTE', 'ADMINISTRADOR')")
    public ResponseEntity<CategoriaDTO> criarCategoria(@RequestBody CategoriaDTO dto, UriComponentsBuilder uriBuilder) {
        Categoria categoriaSalva = categoriaService.criarCategoria(dto);
        URI uri = uriBuilder.path("/api/categorias/{id}").buildAndExpand(categoriaSalva.getId()).toUri();
        return ResponseEntity.created(uri).body(categoriaService.converterParaDTO(categoriaSalva));
    }

    // ENDPOINT RESTRITO: Atualizar categoria
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('GERENTE', 'ADMINISTRADOR')")
    public ResponseEntity<CategoriaDTO> atualizarCategoria(@PathVariable Long id, @RequestBody CategoriaDTO dto) {
        CategoriaDTO atualizada = categoriaService.atualizarCategoria(id, dto);
        return ResponseEntity.ok(atualizada);
    }

    // ENDPOINT RESTRITO: Deletar categoria
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('GERENTE', 'ADMINISTRADOR')")
    public ResponseEntity<Void> deletarCategoria(@PathVariable Long id) {
        categoriaService.deletarCategoria(id);
        return ResponseEntity.noContent().build();
    }
}