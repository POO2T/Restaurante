package com.example.Back_End_Restaurante.Services;

import com.example.Back_End_Restaurante.Dto.ComandaAberturaRequestDTO;
import com.example.Back_End_Restaurante.Dto.ComandaResponseDTO;
import com.example.Back_End_Restaurante.Enums.StatusComanda;
import com.example.Back_End_Restaurante.Enums.StatusMesa;
import com.example.Back_End_Restaurante.Model.Cliente;
import com.example.Back_End_Restaurante.Model.Comanda;
import com.example.Back_End_Restaurante.Model.Mesa;
import com.example.Back_End_Restaurante.Repositorio.ClienteRepository;
import com.example.Back_End_Restaurante.Repositorio.ComandaRepository;
import com.example.Back_End_Restaurante.Repositorio.MesaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ComandaService {

    @Autowired
    private ComandaRepository comandaRepository;

    @Autowired
    private MesaRepository mesaRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    /**
     * Abre uma nova comanda para um visitante (usuário anônimo).
     */
    @Transactional // Garante que a operação (salvar comanda + atualizar mesa) seja atômica
    public ComandaResponseDTO abrirComandaVisitante(ComandaAberturaRequestDTO request) {
        Mesa mesa = buscarEMudarStatusMesa(request.getMesaId());

        Comanda novaComanda = new Comanda();
        novaComanda.setMesa(mesa);
        novaComanda.setCliente(null); // Visitante não tem cliente associado

        Comanda comandaSalva = comandaRepository.save(novaComanda);
        return converterParaDTO(comandaSalva);
    }

    /**
     * Abre uma nova comanda para um cliente autenticado.
     */
    @Transactional
    public ComandaResponseDTO abrirComandaAutenticada(ComandaAberturaRequestDTO request, String emailCliente) {
        // Busca o cliente autenticado
        Cliente cliente = clienteRepository.findByEmail(emailCliente)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente não encontrado"));

        Mesa mesa = buscarEMudarStatusMesa(request.getMesaId());

        Comanda novaComanda = new Comanda();
        novaComanda.setMesa(mesa);
        novaComanda.setCliente(cliente); // Associa o cliente à comanda

        Comanda comandaSalva = comandaRepository.save(novaComanda);
        return converterParaDTO(comandaSalva);
    }

    /**
     * Método auxiliar para buscar a mesa e validar seu status.
     * Marca a mesa como OCUPADA.
     */
    private Mesa buscarEMudarStatusMesa(Long mesaId) {
        if (mesaId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O ID da mesa é obrigatório");
        }
        Mesa mesa = mesaRepository.findById(mesaId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Mesa não encontrada"));

        if (mesa.getStatus() != StatusMesa.DISPONIVEL) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Mesa " + mesa.getNumero() + " já está ocupada");
        }

        // Atualiza o status da mesa
        mesa.setStatus(StatusMesa.OCUPADA);
        mesaRepository.save(mesa);
        return mesa;
    }

    /**
     * Converte a entidade Comanda para seu DTO de resposta.
     */
    private ComandaResponseDTO converterParaDTO(Comanda comanda) {
        Long clienteId = (comanda.getCliente() != null) ? comanda.getCliente().getId() : null;
        return new ComandaResponseDTO(
                comanda.getId(),
                comanda.getStatus().name(),
                comanda.getDataAbertura(),
                comanda.getMesa().getId(),
                clienteId,
                comanda.getMesa().getNumero()
        );
    }
}
