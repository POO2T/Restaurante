package com.example.Back_End_Restaurante.Repositorio;

import com.example.Back_End_Restaurante.Enums.StatusPedido; // Importar
import com.example.Back_End_Restaurante.Model.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List; // Importar

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    // NOVO MÉTODO: Busca todos os pedidos com um status específico,
    // ordenados pela data/hora em ordem ascendente (o mais antigo primeiro).
    List<Pedido> findByStatusOrderByDataHoraAsc(StatusPedido status);
}