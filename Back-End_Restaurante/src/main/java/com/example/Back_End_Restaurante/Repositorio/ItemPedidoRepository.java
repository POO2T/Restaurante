package com.example.Back_End_Restaurante.Repositorio;

import com.example.Back_End_Restaurante.Dto.RelatorioDTOs; // Importar
import com.example.Back_End_Restaurante.Model.ItemPedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemPedidoRepository extends JpaRepository<ItemPedido, Long> {

    // Busca os produtos mais vendidos (Agrupados por nome)
    // Ordena do maior para o menor (DESC)
    // O '$' é usado para acessar a classe interna estática no JPQL
    @Query("SELECT new com.example.Back_End_Restaurante.Dto.RelatorioDTOs$ProdutoVendidoDTO(" +
            "i.produto.nome, SUM(i.quantidade), SUM(i.quantidade * i.precoUnitario)) " +
            "FROM ItemPedido i " +
            "GROUP BY i.produto.nome " +
            "ORDER BY SUM(i.quantidade) DESC")
    List<RelatorioDTOs.ProdutoVendidoDTO> findTopSellingProducts();
}