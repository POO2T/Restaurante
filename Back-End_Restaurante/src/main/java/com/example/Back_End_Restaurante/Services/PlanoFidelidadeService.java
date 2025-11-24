package com.example.Back_End_Restaurante.Services;

import com.example.Back_End_Restaurante.Dto.PlanoFidelidadeDTO;
import com.example.Back_End_Restaurante.Model.PlanoFidelidade;
import com.example.Back_End_Restaurante.Repositorio.PlanoFidelidadeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PlanoFidelidadeService {

    @Autowired
    private PlanoFidelidadeRepository planoRepository;

    // Converter Entidade -> DTO
    public PlanoFidelidadeDTO converterParaDTO(PlanoFidelidade plano) {
        return new PlanoFidelidadeDTO(
                plano.getId(),
                plano.getNome(),
                plano.getDescricao(),
                plano.getPontos() // Assumindo que o campo na entidade é 'pontos'
        );
    }

    // Listar todos
    public List<PlanoFidelidadeDTO> listarTodos() {
        return planoRepository.findAll().stream()
                .map(this::converterParaDTO)
                .collect(Collectors.toList());
    }

    // Buscar por ID
    public PlanoFidelidadeDTO buscarPorId(Long id) {
        PlanoFidelidade plano = planoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Plano não encontrado"));
        return converterParaDTO(plano);
    }

    // Criar
    public PlanoFidelidadeDTO criarPlano(PlanoFidelidadeDTO dto) {
        if (planoRepository.existsByNome(dto.getNome())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Já existe um plano com este nome.");
        }

        PlanoFidelidade plano = new PlanoFidelidade();
        plano.setNome(dto.getNome());
        plano.setDescricao(dto.getDescricao());
        plano.setPontos(dto.getPontosNecessarios());

        return converterParaDTO(planoRepository.save(plano));
    }

    // Atualizar
    public PlanoFidelidadeDTO atualizarPlano(Long id, PlanoFidelidadeDTO dto) {
        PlanoFidelidade plano = planoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Plano não encontrado"));

        if (dto.getNome() != null && !dto.getNome().equals(plano.getNome()) &&
                planoRepository.existsByNome(dto.getNome())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Já existe um plano com este nome.");
        }

        if (dto.getNome() != null) plano.setNome(dto.getNome());
        if (dto.getDescricao() != null) plano.setDescricao(dto.getDescricao());
        if (dto.getPontosNecessarios() != null) plano.setPontos(dto.getPontosNecessarios());

        return converterParaDTO(planoRepository.save(plano));
    }

    // Deletar
    public void deletarPlano(Long id) {
        if (!planoRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Plano não encontrado");
        }
        planoRepository.deleteById(id);
    }
}