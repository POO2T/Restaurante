package com.example.Back_End_Restaurante.Repositorio;

import com.example.Back_End_Restaurante.Model.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
    // Verifica se já existe uma categoria com este nome
    boolean existsByNome(String nome);
}
