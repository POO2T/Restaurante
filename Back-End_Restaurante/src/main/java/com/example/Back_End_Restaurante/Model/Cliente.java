package com.example.Back_End_Restaurante.Model;

import jakarta.persistence.*;
import java.util.List;
import java.util.ArrayList; // Importar ArrayList

@Entity
@Table(name = "clientes")
public class Cliente extends Usuario {

    @Column(length = 20)
    private String telefone;

    // --- NOVO CAMPO: Pontos de Fidelidade ---
    @Column(nullable = false)
    private Integer pontosFidelidade = 0; // Começa com 0
    // ---------------------------------------

    @ManyToOne
    @JoinColumn(name = "plano_fidelidade_id")
    private PlanoFidelidade planoFidelidade;

    @OneToMany(mappedBy = "cliente")
    private List<Comanda> historicoConsumo = new ArrayList<>();

    // Getters e Setters
    public String getTelefone() {
        return telefone;
    }
    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public Integer getPontosFidelidade() {
        return pontosFidelidade;
    }
    public void setPontosFidelidade(Integer pontosFidelidade) {
        this.pontosFidelidade = pontosFidelidade;
    }

    // Métodos herdados de Usuario (precisa sobrescrever para o Lombok ou uso manual funcionar bem com herança se não usar @Data)
    @Override
    public Long getId() { return super.getId(); }
    @Override
    public void setId(Long id) { super.setId(id); }
    @Override
    public String getNome() { return super.getNome(); }
    @Override
    public void setNome(String nome) { super.setNome(nome); }
    @Override
    public String getEmail() { return super.getEmail(); }
    @Override
    public void setEmail(String email) { super.setEmail(email); }
    @Override
    public String getSenha() { return super.getSenha(); }
    @Override
    public void setSenha(String senha) { super.setSenha(senha); }

    // Helper para adicionar pontos
    public void adicionarPontos(int pontos) {
        this.pontosFidelidade += pontos;
    }
}