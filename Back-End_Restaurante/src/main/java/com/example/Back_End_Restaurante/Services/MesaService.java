package com.example.Back_End_Restaurante.Services;

import com.example.Back_End_Restaurante.Dto.MesaDTO;
import com.example.Back_End_Restaurante.Enums.StatusMesa;
import com.example.Back_End_Restaurante.Model.Mesa;
import com.example.Back_End_Restaurante.Repositorio.MesaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MesaService {

    @Autowired
    private MesaRepository mesaRepository;

    // Converte Entidade (Mesa) para DTO (MesaDTO)
    private MesaDTO convertToDTO(Mesa mesa) {
        return new MesaDTO(
                mesa.getId(),
                mesa.getNumero(),
                mesa.getNome(),
                mesa.getStatus().name() // Converte Enum para String
        );
    }

    // Lista todas as mesas, ordenadas por número
    public List<MesaDTO> listarTodas() {
        return mesaRepository.findAllByOrderByNumeroAsc().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // Busca uma mesa específica pelo ID
    public MesaDTO buscarPorId(Long id) {
        Mesa mesa = mesaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Mesa não encontrada"));
        return convertToDTO(mesa);
    }

    // Cria uma nova mesa
    public MesaDTO criarMesa(MesaDTO mesaDTO) {
        if (mesaRepository.existsByNumero(mesaDTO.getNumero())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Uma mesa com este número já existe");
        }
        Mesa novaMesa = new Mesa();
        novaMesa.setNome(mesaDTO.getNome());
        novaMesa.setNumero(mesaDTO.getNumero());

        // Define o status com base no DTO, se fornecido; caso contrário usa padrão
        String statusStr = mesaDTO.getStatus();
        if (statusStr == null || statusStr.isBlank()) {
            novaMesa.setStatus(StatusMesa.DISPONIVEL); // Padrão com DISPONIVEL
        } else {
            try {
                novaMesa.setStatus(StatusMesa.valueOf(statusStr.toUpperCase()));
            } catch (IllegalArgumentException e) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Status inválido: " + statusStr);
            }
        }

        Mesa mesaSalva = mesaRepository.save(novaMesa);
        return convertToDTO(mesaSalva);
    }

    // Atualiza uma mesa (Número e Nome)
    public MesaDTO atualizarMesa(Long id, MesaDTO mesaDTO) {
        Mesa mesa = mesaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Mesa não encontrada"));

        // Verifica se o novo número já está em uso por OUTRA mesa
        if (!mesa.getNumero().equals(mesaDTO.getNumero()) && mesaRepository.existsByNumero(mesaDTO.getNumero())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Uma mesa com este novo número já existe");
        }

        mesa.setNome(mesaDTO.getNome());
        mesa.setNumero(mesaDTO.getNumero());
        // O status é atualizado por um método separado

        Mesa mesaAtualizada = mesaRepository.save(mesa);
        return convertToDTO(mesaAtualizada);
    }

    // Atualiza APENAS o status de uma mesa (ex: Garçom ocupando a mesa)
    public MesaDTO atualizarStatusMesa(Long id, String status) {
        Mesa mesa = mesaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Mesa não encontrada"));

        try {
            // Converte a String do DTO para o Enum
            mesa.setStatus(StatusMesa.valueOf(status.toUpperCase()));
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Status inválido: " + status);
        }

        Mesa mesaAtualizada = mesaRepository.save(mesa);
        return convertToDTO(mesaAtualizada);
    }


    // Deleta uma mesa
    public void deletarMesa(Long id) {
        if (!mesaRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Mesa não encontrada");
        }
        // Adicionar verificação futura: não deletar se tiver comanda aberta
        mesaRepository.deleteById(id);
    }
}
