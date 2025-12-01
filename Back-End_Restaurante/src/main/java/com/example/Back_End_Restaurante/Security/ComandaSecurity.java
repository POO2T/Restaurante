package com.example.Back_End_Restaurante.Security; // <-- PACOTE CORRIGIDO

import com.example.Back_End_Restaurante.Model.Comanda;
import com.example.Back_End_Restaurante.Model.Usuario;
import com.example.Back_End_Restaurante.Repositorio.ComandaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;
import org.springframework.transaction.annotation.Transactional;

@Component("comandaSecurity")
public class ComandaSecurity {

    @Autowired
    private ComandaRepository comandaRepository;

    @Transactional(readOnly = true)
    public boolean checkClienteIsComandaOwner(Long comandaId) {
        // First, try to load the comanda. If it does not exist, deny access.
        Optional<Comanda> comandaOpt = comandaRepository.findById(comandaId);
        if (comandaOpt.isEmpty()) {
            System.out.println("ComandaSecurity: comanda " + comandaId + " não encontrada");
            return false;
        }

        Comanda comanda = comandaOpt.get();
        Usuario donoDaComanda = comanda.getCliente();

        // If the comanda has no cliente it was opened by a visitor (no account).
        // Allow visitor flow: permit anonymous requests to operate on visitor comandas.
        if (donoDaComanda == null) {
            System.out.println("ComandaSecurity: comanda " + comandaId + " pertence a visitante — permitindo acesso");
            return true;
        }

        // For comandas owned by a cliente, we require an authenticated user and
        // verify the logged user's email matches the comanda owner.
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            System.out.println("ComandaSecurity: requisição não autenticada para comanda " + comandaId);
            return false;
        }

        String emailDoUsuarioLogado = authentication.getName();
        if (emailDoUsuarioLogado == null) {
            System.out.println("ComandaSecurity: email do usuário logado é nulo para comanda " + comandaId);
            return false;
        }

        boolean ok = donoDaComanda.getEmail().equals(emailDoUsuarioLogado);
        System.out.println("ComandaSecurity: comanda " + comandaId + " dono=" + donoDaComanda.getEmail() + " user="
                + emailDoUsuarioLogado + " => " + ok);
        return ok;
    }
}