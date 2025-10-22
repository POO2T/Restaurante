package com.example.Back_End_Restaurante.Model;

import com.example.Back_End_Restaurante.Enums.FormaPagamento;
import jakarta.persistence.*;
import lombok.Data;
import java.util.Date;

@Data
@Entity
@Table(name = "pagamentos")
public class Pagamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date dataPagamento;

    @Column(nullable = false)
    private double valor;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private FormaPagamento forma; // Dinheiro, Débito, Crédito, PIX

    // Muitos pagamentos podem estar associados a uma comanda
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comanda_id", nullable = false)
    private Comanda comanda;
}