package com.example.Back_End_Restaurante.Security; // <-- PACOTE CORRIGIDO

import com.example.Back_End_Restaurante.Model.Comanda;
import com.example.Back_End_Restaurante.Model.Usuario;
import com.example.Back_End_Restaurante.Repositorio.ComandaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component("comandaSecurity")
public class ComandaSecurity {

    @Autowired
    private ComandaRepository comandaRepository;

    public boolean checkClienteIsComandaOwner(Long comandaId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        String emailDoUsuarioLogado = authentication.getName();
        if (emailDoUsuarioLogado == null) {
            return false;
        }

        Optional<Comanda> comandaOpt = comandaRepository.findById(comandaId);
        if (comandaOpt.isEmpty()) {
            return false;
        }

        Comanda comanda = comandaOpt.get();
        Usuario donoDaComanda = comanda.getCliente();

        if (donoDaComanda == null) {
            return false;
        }

        return donoDaComanda.getEmail().equals(emailDoUsuarioLogado);
    }
}