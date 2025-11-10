package com.example.Back_End_Restaurante.Services;

import com.example.Back_End_Restaurante.Dto.CategoriaDTO;
import com.example.Back_End_Restaurante.Model.Categoria;
import com.example.Back_End_Restaurante.Repositorio.CategoriaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoriaService {

    @Autowired
    private CategoriaRepository categoriaRepository;

    // Converte Entidade Categoria para CategoriaDTO
    public CategoriaDTO converterParaDTO(Categoria categoria) {
        return new CategoriaDTO(categoria.getId(), categoria.getNome());
    }

    // Listar todas
    public List<CategoriaDTO> listarTodas() {
        return categoriaRepository.findAll()
                .stream()
                .map(this::converterParaDTO)
                .collect(Collectors.toList());
    }

    // Buscar por ID
    public CategoriaDTO buscarPorId(Long id) {
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Categoria não encontrada"));
        return converterParaDTO(categoria);
    }

    // Criar Categoria
    public Categoria criarCategoria(CategoriaDTO categoriaDTO) {
        if (categoriaRepository.existsByNome(categoriaDTO.getNome())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Já existe uma categoria com este nome.");
        }
        Categoria novaCategoria = new Categoria();
        novaCategoria.setNome(categoriaDTO.getNome());
        return categoriaRepository.save(novaCategoria);
    }

    // Atualizar Categoria
    public CategoriaDTO atualizarCategoria(Long id, CategoriaDTO categoriaDTO) {
        Categoria categoriaExistente = categoriaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Categoria não encontrada"));

        if (categoriaDTO.getNome() != null && !categoriaDTO.getNome().equals(categoriaExistente.getNome()) &&
                categoriaRepository.existsByNome(categoriaDTO.getNome())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Já existe outra categoria com este nome.");
        }

        if (categoriaDTO.getNome() != null) {
            categoriaExistente.setNome(categoriaDTO.getNome());
        }

        Categoria atualizada = categoriaRepository.save(categoriaExistente);
        return converterParaDTO(atualizada);
    }

    // Deletar Categoria
    public void deletarCategoria(Long id) {
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Categoria não encontrada"));

        // Regra de negócio: Não pode deletar categoria se ela tiver produtos
        if (!categoria.getProdutos().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Não é possível deletar esta categoria, pois ela contém produtos associados.");
        }

        categoriaRepository.deleteById(id);
    }
}