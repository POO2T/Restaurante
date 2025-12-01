package com.example.Back_End_Restaurante.Repositorio;

import com.example.Back_End_Restaurante.Enums.StatusComanda;
import com.example.Back_End_Restaurante.Model.Cliente;
import com.example.Back_End_Restaurante.Model.Comanda;
import com.example.Back_End_Restaurante.Model.Mesa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query; // Importar
import org.springframework.data.repository.query.Param; // Importar
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ComandaRepository extends JpaRepository<Comanda, Long> {

    Optional<Comanda> findByMesaAndStatus(Mesa mesa, StatusComanda status);

    List<Comanda> findByClienteAndStatus(Cliente cliente, StatusComanda status);

    List<Comanda> findByClienteOrderByDataAberturaDesc(Cliente cliente);

    // --- CONSULTAS PARA RELATÓRIOS ---

    // Conta quantas comandas foram FECHADAS em um período (ex: Hoje)
    @Query("SELECT COUNT(c) FROM Comanda c WHERE c.status = 'FECHADA' AND c.dataFechamento BETWEEN :inicio AND :fim")
    Long countVendasNoPeriodo(@Param("inicio") LocalDateTime inicio, @Param("fim") LocalDateTime fim);

    // Soma o total de pagamentos recebidos em comandas fechadas no período
    @Query("SELECT COALESCE(SUM(p.valor), 0.0) FROM Pagamento p WHERE p.comanda.status = 'FECHADA' AND p.dataPagamento BETWEEN :inicio AND :fim")
    Double sumFaturamentoNoPeriodo(@Param("inicio") LocalDateTime inicio, @Param("fim") LocalDateTime fim);

    // Busca comandas por status (ex: ABERTA)
    List<Comanda> findByStatus(StatusComanda status);
}