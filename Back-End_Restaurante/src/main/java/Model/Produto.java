package Model;

import Enums.StatusProduto;
import jakarta.persistence.*;
import lombok.Data;

@Data
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

    private double preco;

    private int quantidadeEstoque;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private StatusProduto disponibilidade;

    // Muitos produtos pertencem a uma categoria
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_id", nullable = false)
    private Categoria categoria;
}
