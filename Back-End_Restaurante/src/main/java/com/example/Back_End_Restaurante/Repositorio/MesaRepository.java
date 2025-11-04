package com.example.Back_End_Restaurante.Repositorio;

import com.example.Back_End_Restaurante.Model.Mesa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MesaRepository extends JpaRepository<Mesa, Long> {

    // Método para buscar uma mesa pelo número (usado no service)
    Optional<Mesa> findByNumero(Integer numero);

    // Método para verificar se um número de mesa já existe
    boolean existsByNumero(Integer numero);

    // Método para listar mesas ordenadas (o que tínhamos antes)
    List<Mesa> findAllByOrderByNumeroAsc();
}
