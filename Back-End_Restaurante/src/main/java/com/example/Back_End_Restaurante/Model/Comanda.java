package com.example.Back_End_Restaurante.Model;

import com.example.Back_End_Restaurante.Enums.StatusComanda;
import jakarta.persistence.*;
import lombok.Data;
import java.util.Date;
import java.util.List;

@Data
@Entity
@Table(name = "comandas")
public class Comanda {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false, updatable = false)
    private Date dataAbertura;

    @Temporal(TemporalType.TIMESTAMP)
    private Date dataFechamento;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private StatusComanda status; // Usando o Enum StatusComanda

    // Muitas comandas podem pertencer a um cliente
    @ManyToOne(fetch = FetchType.LAZY) // LAZY: Carrega o cliente só quando necessário
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;

    // Muitas comandas podem estar associadas a uma mesa
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mesa_id", nullable = false)
    private Mesa mesa;

    // Uma comanda tem uma lista de pedidos [cite: 5]
    @OneToMany(mappedBy = "comanda", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Pedido> pedidos;

    // Uma comanda pode ter vários pagamentos (caso de conta dividida)
    @OneToMany(mappedBy = "comanda", cascade = CascadeType.ALL)
    private List<Pagamento> pagamentos;
}
