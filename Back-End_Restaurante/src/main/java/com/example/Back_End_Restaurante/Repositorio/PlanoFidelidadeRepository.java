package com.example.Back_End_Restaurante.Repositorio;

import com.example.Back_End_Restaurante.Model.PlanoFidelidade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlanoFidelidadeRepository extends JpaRepository<PlanoFidelidade, Long> {
    boolean existsByNome(String nome);
}