package Model;

import Enums.StatusPedido;
import jakarta.persistence.*;
import lombok.Data;
import java.util.Date;
import java.util.List;

@Data
@Entity
@Table(name = "pedidos")
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date dataHora;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private StatusPedido status; // Pendente, Em preparo, Finalizado [cite: 5]

    // Muitos pedidos pertencem a uma comanda
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comanda_id", nullable = false)
    private Comanda comanda;

    // Um pedido é composto por vários itens
    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemPedido> itens;
}
