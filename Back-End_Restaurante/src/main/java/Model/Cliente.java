package Model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "clientes")
public class Cliente extends Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 20)
    private String telefone;

    @ManyToOne // Um cliente pode ter um plano, um plano pode ter muitos clientes
    @JoinColumn(name = "plano_fidelidade_id")
    private PlanoFidelidade planoFidelidade;

    // Um cliente pode ter várias comandas (histórico) [cite: 7]
    @OneToMany(mappedBy = "cliente")
    private List<Comanda> historicoConsumo;
}