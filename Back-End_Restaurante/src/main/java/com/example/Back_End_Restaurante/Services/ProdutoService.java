package com.example.Back_End_Restaurante.Services;

import com.example.Back_End_Restaurante.Dto.CategoriaDTO;
import com.example.Back_End_Restaurante.Dto.ProdutoRequestDTO;
import com.example.Back_End_Restaurante.Dto.ProdutoResponseDTO;
import com.example.Back_End_Restaurante.Enums.StatusProduto;
import com.example.Back_End_Restaurante.Model.Categoria;
import com.example.Back_End_Restaurante.Model.Produto;
import com.example.Back_End_Restaurante.Repositorio.CategoriaRepository;
import com.example.Back_End_Restaurante.Repositorio.ProdutoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProdutoService {

    @Autowired
    private ProdutoRepository produtoRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    // Converte Entidade Produto para ProdutoResponseDTO
    public ProdutoResponseDTO converterParaDTO(Produto produto) {
        ProdutoResponseDTO dto = new ProdutoResponseDTO();
        dto.setId(produto.getId());
        dto.setNome(produto.getNome());
        dto.setDescricao(produto.getDescricao());
        dto.setPreco(produto.getPreco());
        dto.setQuantidadeEstoque(produto.getQuantidadeEstoque());
        dto.setDisponibilidade(produto.getDisponibilidade().name());

        // Anexa o DTO da Categoria (para o frontend saber o nome)
        CategoriaDTO categoriaDTO = new CategoriaDTO(
                produto.getCategoria().getId(),
                produto.getCategoria().getNome()
        );
        dto.setCategoria(categoriaDTO);
        return dto;
    }

    // Listar todos os produtos
    public List<ProdutoResponseDTO> listarTodos() {
        return produtoRepository.findAll().stream()
                .map(this::converterParaDTO)
                .collect(Collectors.toList());
    }

    // Listar produtos por categoria
    public List<ProdutoResponseDTO> listarPorCategoria(Long categoriaId) {
        if (!categoriaRepository.existsById(categoriaId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Categoria não encontrada");
        }
        return produtoRepository.findByCategoriaId(categoriaId).stream()
                .map(this::converterParaDTO)
                .collect(Collectors.toList());
    }

    // Buscar produto por ID
    public ProdutoResponseDTO buscarPorId(Long id) {
        Produto produto = produtoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Produto não encontrado"));
        return converterParaDTO(produto);
    }

    // Criar novo produto
    public ProdutoResponseDTO criarProduto(ProdutoRequestDTO dto) {
        Categoria categoria = categoriaRepository.findById(dto.getCategoriaId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Categoria com ID " + dto.getCategoriaId() + " não encontrada"));

        Produto produto = new Produto();
        produto.setNome(dto.getNome());
        produto.setDescricao(dto.getDescricao());
        produto.setPreco(dto.getPreco());
        produto.setQuantidadeEstoque(dto.getQuantidadeEstoque());
        produto.setCategoria(categoria);

        try {
            produto.setDisponibilidade(StatusProduto.valueOf(dto.getDisponibilidade().toUpperCase()));
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Status de disponibilidade inválido: " + dto.getDisponibilidade());
        }

        Produto salvo = produtoRepository.save(produto);
        return converterParaDTO(salvo);
    }

    // Atualizar produto
    public ProdutoResponseDTO atualizarProduto(Long id, ProdutoRequestDTO dto) {
        Produto produto = produtoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Produto não encontrado"));

        // Atualiza campos
        if (dto.getNome() != null) produto.setNome(dto.getNome());
        if (dto.getDescricao() != null) produto.setDescricao(dto.getDescricao());
        if (dto.getPreco() != null) produto.setPreco(dto.getPreco());
        if (dto.getQuantidadeEstoque() != null) produto.setQuantidadeEstoque(dto.getQuantidadeEstoque());

        if (dto.getDisponibilidade() != null) {
            try {
                produto.setDisponibilidade(StatusProduto.valueOf(dto.getDisponibilidade().toUpperCase()));
            } catch (Exception e) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Status de disponibilidade inválido: " + dto.getDisponibilidade());
            }
        }

        // Se o DTO pedir para mudar a categoria
        if (dto.getCategoriaId() != null && !dto.getCategoriaId().equals(produto.getCategoria().getId())) {
            Categoria novaCategoria = categoriaRepository.findById(dto.getCategoriaId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Nova categoria com ID " + dto.getCategoriaId() + " não encontrada"));
            produto.setCategoria(novaCategoria);
        }

        Produto atualizado = produtoRepository.save(produto);
        return converterParaDTO(atualizado);
    }

    // Deletar produto
    public void deletarProduto(Long id) {
        if (!produtoRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Produto não encontrado");
        }
        // Adicionar verificação futura: não deletar produto se estiver em comanda aberta
        // ...

        produtoRepository.deleteById(id);
    }
}