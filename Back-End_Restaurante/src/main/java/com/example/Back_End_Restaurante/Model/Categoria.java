package com.example.Back_End_Restaurante.Model;

import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

@Data
@Entity
@Table(name = "categorias")
public class Categoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String nome;

    // Uma categoria pode ter vários produtos [cite: 8]
    @OneToMany(mappedBy = "categoria")
    private List<Produto> produtos;
}