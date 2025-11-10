package com.example.Back_End_Restaurante.Model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "clientes")
public class Cliente extends Usuario {



    @Column(length = 20)
    private String telefone;

    @ManyToOne // Um cliente pode ter um plano, um plano pode ter muitos clientes
    @JoinColumn(name = "plano_fidelidade_id")
    private PlanoFidelidade planoFidelidade;

    // Um cliente pode ter várias comandas (histórico) [cite: 7]
    @OneToMany(mappedBy = "cliente")
    private List<Comanda> historicoConsumo;

    public String getTelefone() {
        return telefone;
    }
    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getSenha() {
        return super.getSenha();
    }
    public void setSenha(String senha) {
        super.setSenha(senha);
    }
    public Long getId() {
        return super.getId();
    }
}