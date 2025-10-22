package com.example.Back_End_Restaurante.Repositorio;

import com.example.Back_End_Restaurante.Model.Funcionario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FuncionarioRepository extends JpaRepository<Funcionario, Long> {
    boolean existsByEmail(String email);
    Optional<Funcionario> findByEmail(String email);
}
