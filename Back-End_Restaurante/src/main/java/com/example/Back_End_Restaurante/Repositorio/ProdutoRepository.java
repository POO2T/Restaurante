package com.example.Back_End_Restaurante.Repositorio;

import com.example.Back_End_Restaurante.Model.Produto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProdutoRepository extends JpaRepository<Produto, Long> {

    // Busca todos os produtos de uma categoria específica
    List<Produto> findByCategoriaId(Long categoriaId);

    // Busca todos os produtos de uma categoria específica e que estejam disponíveis
    List<Produto> findByCategoriaIdAndDisponibilidade(Long categoriaId, com.example.Back_End_Restaurante.Enums.StatusProduto disponibilidade);

    // Buscar todos os produtos com a entidade Categoria carregada para evitar LazyInitializationException
    @org.springframework.data.jpa.repository.Query("select p from Produto p join fetch p.categoria")
    java.util.List<Produto> findAllWithCategoria();

    // Buscar por categoria com join fetch
    @org.springframework.data.jpa.repository.Query("select p from Produto p join fetch p.categoria where p.categoria.id = :categoriaId")
    java.util.List<Produto> findByCategoriaIdWithCategoria(Long categoriaId);

    // Buscar por ID com join fetch da categoria
    @org.springframework.data.jpa.repository.Query("select p from Produto p join fetch p.categoria where p.id = :id")
    Optional<Produto> findByIdWithCategoria(Long id);
}
