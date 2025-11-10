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

/**
 * Serviço que contém a lógica de negócio para gerenciar Mesas.
 * É chamado pelo MesaController.
 */
@Service
public class MesaService {

    @Autowired
    private MesaRepository mesaRepository;

    // --- Métodos de Listagem (Públicos) ---

    /**
     * Lista todas as mesas cadastradas, ordenadas por número.
     * @return Lista de MesaDTO
     */
    public List<MesaDTO> listarTodas() {
        return mesaRepository.findAllByOrderByNumeroAsc()
                .stream()
                .map(this::converterParaDTO) // Converte cada Entidade para DTO
                .collect(Collectors.toList());
    }

    /**
     * Busca uma mesa específica pelo seu ID.
     * @param id O ID da mesa
     * @return MesaDTO
     */
    public MesaDTO buscarPorId(Long id) {
        Mesa mesa = mesaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Mesa não encontrada"));
        return converterParaDTO(mesa);
    }

    // --- Métodos de Gerenciamento (Restritos) ---

    /**
     * Cria uma nova mesa (Apenas Gerente/Admin).
     * @param mesaDTO DTO com os dados da nova mesa (nome e numero)
     * @return MesaDTO da mesa salva
     */
    public MesaDTO criarMesa(MesaDTO mesaDTO) {
        // Validação: Não permitir mesas com mesmo número
        if (mesaRepository.existsByNumero(mesaDTO.getNumero())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Já existe uma mesa com o número: " + mesaDTO.getNumero());
        }

        // Criar nova entidade Mesa
        Mesa novaMesa = new Mesa();
        novaMesa.setNome(mesaDTO.getNome());
        novaMesa.setNumero(mesaDTO.getNumero());

        // --- CORREÇÃO IMPORTANTE ---
        // Define o status padrão para "DISPONIVEL" ao criar
        novaMesa.setStatus(StatusMesa.DISPONIVEL);
        // -------------------------

        // Salvar no banco
        Mesa mesaSalva = mesaRepository.save(novaMesa);

        // Retornar o DTO da mesa salva
        return converterParaDTO(mesaSalva);
    }

    /**
     * Atualiza os dados de uma mesa (nome ou número) (Apenas Gerente/Admin).
     * @param id O ID da mesa a ser atualizada
     * @param mesaDTO DTO com os novos dados
     * @return MesaDTO da mesa atualizada
     */
    public MesaDTO atualizarMesa(Long id, MesaDTO mesaDTO) {
        Mesa mesaExistente = mesaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Mesa não encontrada"));

        // Validação: Se o número foi alterado, verifica se o novo número já existe em OUTRA mesa
        if (mesaDTO.getNumero() != null && !mesaDTO.getNumero().equals(mesaExistente.getNumero()) &&
                mesaRepository.existsByNumero(mesaDTO.getNumero())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Já existe outra mesa com o número: " + mesaDTO.getNumero());
        }

        // Atualiza os campos
        if (mesaDTO.getNome() != null) {
            mesaExistente.setNome(mesaDTO.getNome());
        }
        if (mesaDTO.getNumero() != null) {
            mesaExistente.setNumero(mesaDTO.getNumero());
        }
        // Nota: O status não é atualizado aqui, e sim no método 'atualizarStatusMesa'

        Mesa mesaAtualizada = mesaRepository.save(mesaExistente);
        return converterParaDTO(mesaAtualizada);
    }

    /**
     * Atualiza APENAS o status de uma mesa (Garçom/Gerente/Admin).
     * @param id O ID da mesa
     * @param statusString O novo status (ex: "OCUPADO", "DISPONIVEL")
     * @return MesaDTO da mesa atualizada
     */
    public MesaDTO atualizarStatusMesa(Long id, String statusString) {
        Mesa mesa = mesaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Mesa não encontrada"));

        // Converte a String do DTO/JSON para o Enum StatusMesa
        try {
            StatusMesa novoStatus = StatusMesa.valueOf(statusString.toUpperCase());
            mesa.setStatus(novoStatus);
            Mesa mesaAtualizada = mesaRepository.save(mesa);
            return converterParaDTO(mesaAtualizada);
        } catch (IllegalArgumentException e) {
            // Se o frontend enviar um status inválido (ex: "QUEBRADA")
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Status inválido: " + statusString);
        }
    }

    /**
     * Deleta uma mesa (Apenas Gerente/Admin).
     * @param id O ID da mesa a ser deletada
     */
    public void deletarMesa(Long id) {
        if (!mesaRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Mesa não encontrada");
        }
        // Adicionar verificação futura: não deletar mesa se tiver comanda aberta
        // ...

        mesaRepository.deleteById(id);
    }


    // --- Método Auxiliar ---

    /**
     * Converte uma entidade Mesa em um MesaDTO (para enviar ao frontend).
     * @param mesa A entidade Mesa
     * @return O objeto MesaDTO
     */
    private MesaDTO converterParaDTO(Mesa mesa) {
        return new MesaDTO(
                mesa.getId(),
                mesa.getNumero(),
                mesa.getNome(),
                mesa.getStatus().name() // Converte o Enum (ex: StatusMesa.DISPONIVEL) para String ("DISPONIVEL")
        );
    }
}