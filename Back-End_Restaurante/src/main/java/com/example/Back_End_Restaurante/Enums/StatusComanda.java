package com.example.Back_End_Restaurante.Enums;

/**
 * Define os estados possíveis de uma comanda.
 * ABERTA: A comanda está ativa e recebendo pedidos.
 * PENDENTE: Aguardando pagamento (estado intermediário opcional).
 * FECHADA: A comanda foi paga e concluída.
 */
public enum StatusComanda {
    ABERTA,
    PENDENTE,
    FECHADA
}