package Model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "planos_fidelidade")
public class PlanoFidelidade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String nome;

    @Column(columnDefinition = "TEXT")
    private String descricao;

    private int pontos;
}
