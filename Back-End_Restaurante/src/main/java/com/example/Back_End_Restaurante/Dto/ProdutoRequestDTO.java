package com.example.Back_End_Restaurante.Dto;


/**
 * DTO para Requisição (criar/atualizar) um Produto.
 * O frontend envia apenas o ID da categoria.
 */
public class ProdutoRequestDTO {
    private String nome;
    private String descricao;
    private Double preco;
    private Integer quantidadeEstoque;
    private String disponibilidade; // "DISPONIVEL" ou "INDISPONIVEL"
    private Long categoriaId; // ID da Categoria

    // Getters e Setters
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public Double getPreco() { return preco; }
    public void setPreco(Double preco) { this.preco = preco; }
    public Integer getQuantidadeEstoque() { return quantidadeEstoque; }
    public void setQuantidadeEstoque(Integer quantidadeEstoque) { this.quantidadeEstoque = quantidadeEstoque; }
    public String getDisponibilidade() { return disponibilidade; }
    public void setDisponibilidade(String disponibilidade) { this.disponibilidade = disponibilidade; }
    public Long getCategoriaId() { return categoriaId; }
    public void setCategoriaId(Long categoriaId) { this.categoriaId = categoriaId; }
}