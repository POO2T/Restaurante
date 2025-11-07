package com.example.Back_End_Restaurante.Dto;

import java.util.List;

public class CardapioDTO {
    private CategoriaDTO categoria;
    private List<ProdutoDTO> produtos;

    // Construtores
    public CardapioDTO() {}

    public CardapioDTO(CategoriaDTO categoria, List<ProdutoDTO> produtos) {
        this.categoria = categoria;
        this.produtos = produtos;
}
    public CategoriaDTO getCategoria() { return categoria; }
    public void setCategoria(CategoriaDTO categoria) { this.categoria = categoria; }

    public List<ProdutoDTO> getProdutos() { return produtos; }
    public void setProdutos(List<ProdutoDTO> produtos) { this.produtos = produtos; }
}