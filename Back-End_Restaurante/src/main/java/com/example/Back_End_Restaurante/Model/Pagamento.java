package com.example.Back_End_Restaurante.Model;

// 2. Entidade Pagamento (Novo)
// (Usando Getters/Setters manuais, sem Lombok)

import com.example.Back_End_Restaurante.Enums.FormaPagamento;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "pagamentos")
public class Pagamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime dataPagamento;

    @Column(nullable = false)
    private Double valor;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private FormaPagamento forma;

    // Relacionamento: Muitos pagamentos para UMA comanda
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comanda_id", nullable = false)
    private Comanda comanda;

    // Construtor
    public Pagamento() {
        this.dataPagamento = LocalDateTime.now();
    }

    // --- Getters e Setters ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getDataPagamento() {
        return dataPagamento;
    }

    public void setDataPagamento(LocalDateTime dataPagamento) {
        this.dataPagamento = dataPagamento;
    }

    public Double getValor() {
        return valor;
    }

    public void setValor(Double valor) {
        this.valor = valor;
    }

    public FormaPagamento getForma() {
        return forma;
    }

    public void setForma(FormaPagamento forma) {
        this.forma = forma;
    }

    public Comanda getComanda() {
        return comanda;
    }

    public void setComanda(Comanda comanda) {
        this.comanda = comanda;
    }
}