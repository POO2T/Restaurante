package com.example.Back_End_Restaurante.Repositorio;

import com.example.Back_End_Restaurante.Model.Produto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProdutoRepository extends JpaRepository<Produto, Long> {

    // Busca todos os produtos de uma categoria específica
    List<Produto> findByCategoriaId(Long categoriaId);

    // Busca todos os produtos de uma categoria específica e que estejam disponíveis
    List<Produto> findByCategoriaIdAndDisponibilidade(Long categoriaId, com.example.Back_End_Restaurante.Enums.StatusProduto disponibilidade);
}
