package com.example.Back_End_Restaurante.Repositorio;

// 4. PagamentoRepository (Novo)
import com.example.Back_End_Restaurante.Model.Pagamento;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PagamentoRepository extends JpaRepository<Pagamento, Long> {
}