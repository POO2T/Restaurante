package com.example.Back_End_Restaurante.Model;

import com.example.Back_End_Restaurante.Enums.StatusPedido;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "pedidos")
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime dataHora;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private StatusPedido status;

    // Relacionamento: Muitos pedidos para UMA comanda
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comanda_id", nullable = false)
    private Comanda comanda;

    // Relacionamento: Um pedido tem VÁRIOS itens
    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemPedido> itens = new ArrayList<>();

    // Construtor
    public Pedido() {
        this.dataHora = LocalDateTime.now();
        this.status = StatusPedido.PENDENTE;
    }

    // Getters e Setters (Sem Lombok)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public void setDataHora(LocalDateTime dataHora) {
        this.dataHora = dataHora;
    }

    public StatusPedido getStatus() {
        return status;
    }

    public void setStatus(StatusPedido status) {
        this.status = status;
    }

    public Comanda getComanda() {
        return comanda;
    }

    public void setComanda(Comanda comanda) {
        this.comanda = comanda;
    }

    public List<ItemPedido> getItens() {
        return itens;
    }

    public void setItens(List<ItemPedido> itens) {
        this.itens = itens;
    }

    // Método helper para adicionar item e manter a consistência
    public void addItem(ItemPedido item) {
        itens.add(item);
        item.setPedido(this);
    }
}