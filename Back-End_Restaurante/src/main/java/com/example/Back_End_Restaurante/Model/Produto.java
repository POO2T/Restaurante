package com.example.Back_End_Restaurante.Model;

import com.example.Back_End_Restaurante.Enums.StatusProduto;
import jakarta.persistence.*;

@Entity
@Table(name = "produtos")
public class Produto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String nome;

    @Column(columnDefinition = "TEXT")
    private String descricao;

    @Column(nullable = false)
    private Double preco;

    @Column(nullable = false)
    private Integer quantidadeEstoque; // Para controle de inventário

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private StatusProduto disponibilidade;

    // Relacionamento: Muitos produtos pertencem a UMA categoria
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_id", nullable = false)
    private Categoria categoria;

    // Relacionamento futuro com ItemPedido
    // @OneToMany(mappedBy = "produto")
    // private List<ItemPedido> itensPedido = new ArrayList<>();

    // Construtores
    public Produto() {
        this.disponibilidade = StatusProduto.DISPONIVEL; // Padrão
        this.quantidadeEstoque = 0; // Padrão
    }

    // Getters e Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Double getPreco() {
        return preco;
    }

    public void setPreco(Double preco) {
        this.preco = preco;
    }

    public Integer getQuantidadeEstoque() {
        return quantidadeEstoque;
    }

    public void setQuantidadeEstoque(Integer quantidadeEstoque) {
        this.quantidadeEstoque = quantidadeEstoque;
    }

    public StatusProduto getDisponibilidade() {
        return disponibilidade;
    }

    public void setDisponibilidade(StatusProduto disponibilidade) {
        this.disponibilidade = disponibilidade;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }
}