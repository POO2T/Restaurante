package com.example.Back_End_Restaurante.Model;

// 3. Entidade Comanda (Atualizada)
// Adicionamos a lista de pagamentos

import com.example.Back_End_Restaurante.Enums.StatusComanda;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "comandas")
public class Comanda {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime dataAbertura;

    @Column
    private LocalDateTime dataFechamento; // <-- Campo novo

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private StatusComanda status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mesa_id", nullable = false)
    private Mesa mesa;

    @OneToMany(mappedBy = "comanda", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Pedido> pedidos = new ArrayList<>();

    // --- NOVO RELACIONAMENTO ---
    @OneToMany(mappedBy = "comanda", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Pagamento> pagamentos = new ArrayList<>();

    // Construtor
    public Comanda() {
        this.dataAbertura = LocalDateTime.now();
        this.status = StatusComanda.ABERTA;
    }

    // --- Getters e Setters (Manuais) ---

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public LocalDateTime getDataAbertura() { return dataAbertura; }
    public void setDataAbertura(LocalDateTime dataAbertura) { this.dataAbertura = dataAbertura; }

    public LocalDateTime getDataFechamento() { return dataFechamento; } // NOVO
    public void setDataFechamento(LocalDateTime dataFechamento) { this.dataFechamento = dataFechamento; } // NOVO

    public StatusComanda getStatus() { return status; }
    public void setStatus(StatusComanda status) { this.status = status; }

    public Cliente getCliente() { return cliente; }
    public void setCliente(Cliente cliente) { this.cliente = cliente; }

    public Mesa getMesa() { return mesa; }
    public void setMesa(Mesa mesa) { this.mesa = mesa; }

    public List<Pedido> getPedidos() { return pedidos; }
    public void setPedidos(List<Pedido> pedidos) { this.pedidos = pedidos; }

    public List<Pagamento> getPagamentos() { return pagamentos; } // NOVO
    public void setPagamentos(List<Pagamento> pagamentos) { this.pagamentos = pagamentos; } // NOVO

    // Helper para adicionar pagamento
    public void addPagamento(Pagamento pagamento) {
        pagamentos.add(pagamento);
        pagamento.setComanda(this);
    }
}