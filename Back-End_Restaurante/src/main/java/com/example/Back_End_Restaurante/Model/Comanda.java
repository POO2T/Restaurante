package com.example.Back_End_Restaurante.Model;

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
    private LocalDateTime dataFechamento;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private StatusComanda status;

    // Relacionamento com Cliente (pode ser nulo, para visitantes)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;

    // Relacionamento com Mesa (obrigatório)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mesa_id", nullable = false)
    private Mesa mesa;


    @OneToMany(mappedBy = "comanda", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Pedido> pedidos = new ArrayList<>();

    // Construtores
    public Comanda() {
        this.dataAbertura = LocalDateTime.now();
        this.status = StatusComanda.ABERTA;
    }

    // Getters e Setters (Manuais)

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getDataAbertura() {
        return dataAbertura;
    }

    public void setDataAbertura(LocalDateTime dataAbertura) {
        this.dataAbertura = dataAbertura;
    }

    public LocalDateTime getDataFechamento() {
        return dataFechamento;
    }

    public void setDataFechamento(LocalDateTime dataFechamento) {
        this.dataFechamento = dataFechamento;
    }

    public StatusComanda getStatus() {
        return status;
    }

    public void setStatus(StatusComanda status) {
        this.status = status;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public Mesa getMesa() {
        return mesa;
    }

    public void setMesa(Mesa mesa) {
        this.mesa = mesa;
    }

    // Getters e Setters para Pedidos (a ser adicionado no futuro)
    // public List<Pedido> getPedidos() {
    //     return pedidos;
    // }
    //
    // public void setPedidos(List<Pedido> pedidos) {
    //     this.pedidos = pedidos;
    // }
}