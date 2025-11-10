package com.example.Back_End_Restaurante.Repositorio;

import com.example.Back_End_Restaurante.Model.Comanda;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ComandaRepository extends JpaRepository<Comanda, Long> {
    // Aqui podemos adicionar consultas futuras,
    // como buscar comandas abertas de um cliente específico.
}