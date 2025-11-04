package com.example.Back_End_Restaurante.Model;

import com.example.Back_End_Restaurante.Enums.StatusMesa;
import jakarta.persistence.*;
// import lombok.Data; // REMOVIDO
// import lombok.NoArgsConstructor; // REMOVIDO
// import lombok.AllArgsConstructor; // REMOVIDO

@Entity
@Table(name = "mesas")
// @Data, @NoArgsConstructor, @AllArgsConstructor REMOVIDOS
public class Mesa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Integer numero; // O número da mesa

    @Column(nullable = false, length = 100)
    private String nome; // Um nome/descrição (ex: "Janela", "Perto da Cozinha")

    @Enumerated(EnumType.STRING) // Armazena o nome do enum (DISPONIVEL, OCUPADO)
    @Column(nullable = false)
    private StatusMesa status;

    // Construtor vazio (necessário para o JPA)
    public Mesa() {
    }

    // Construtor com todos os argumentos
    public Mesa(Long id, Integer numero, String nome, StatusMesa status) {
        this.id = id;
        this.numero = numero;
        this.nome = nome;
        this.status = status;
    }

    // --- Getters e Setters manuais ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getNumero() {
        return numero;
    }

    public void setNumero(Integer numero) {
        this.numero = numero;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public StatusMesa getStatus() {
        return status;
    }

    public void setStatus(StatusMesa status) {
        this.status = status;
    }
}

