package com.example.Back_End_Restaurante.Controllers;

import com.example.Back_End_Restaurante.Dto.FuncionarioDTO;
import com.example.Back_End_Restaurante.Enums.TipoFuncionario;
import com.example.Back_End_Restaurante.Model.Funcionario;
import com.example.Back_End_Restaurante.Repositorio.FuncionarioRepository;
import com.example.Back_End_Restaurante.Services.FuncionarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/funcionarios")
@CrossOrigin(origins = "*")
public class FuncionarioController {

    @Autowired
    private FuncionarioRepository funcionarioRepository;

    @Autowired
    private FuncionarioService funcionarioService;

    /**
     * Endpoint para LISTAR todos os funcionários.
     * Mapeado para: GET /funcionarios
     */
    @GetMapping
    public ResponseEntity<List<FuncionarioDTO>> listarFuncionarios() {
        // 1. Busca as Entidades
        List<Funcionario> funcionarios = funcionarioService.ListarFuncionarios();

        // 2. Converte para DTOs
        List<FuncionarioDTO> dtos = funcionarios.stream()
                .map(this::converterParaDTO) // Usa um método auxiliar
                .collect(Collectors.toList());

        // 3. Retorna 200 OK com a lista de DTOs
        return ResponseEntity.ok(dtos);
    }

    /**
     * Endpoint para SALVAR um novo funcionário.
     * Mapeado para: POST /funcionarios
     */
    @PostMapping
    public ResponseEntity<FuncionarioDTO> salvarFuncionario(@RequestBody FuncionarioDTO funcionarioDTO) {

        // 1. Service salva a entidade
        Funcionario funcionarioSalvo = funcionarioService.SalvarFuncionario(funcionarioDTO);

        // 2. Cria a URI do novo recurso
        URI location = UriComponentsBuilder.fromPath("/funcionarios/{id}")
                .buildAndExpand(funcionarioSalvo.getId())
                .toUri();

        // 3. Converte a entidade salva para DTO e retorna 201 CREATED
        return ResponseEntity.created(location).body(converterParaDTO(funcionarioSalvo));
    }

    /**
     * Endpoint para ATUALIZAR um funcionário existente.
     * Mapeado para: PUT /funcionarios/{id}
     */
    @PutMapping("/{id}")
    public Funcionario AtualizarFuncionario(Long id, FuncionarioDTO funcionarioDTO) {
        // 1. Busca o funcionário ou falha
        Funcionario funcionarioExistente = funcionarioRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Funcionário não encontrado"));

        // 2. Valida o e-mail (se ele foi alterado e já existe em OUTRA pessoa)
        if (!funcionarioExistente.getEmail().equals(funcionarioDTO.getEmail()) &&
                funcionarioRepository.existsByEmail(funcionarioDTO.getEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Este e-mail já está em uso por outro usuário.");
        }

        // 3. Atualiza os dados da entidade com os dados do DTO
        funcionarioExistente.setNome(funcionarioDTO.getNome());
        funcionarioExistente.setEmail(funcionarioDTO.getEmail());
        funcionarioExistente.setSalario(funcionarioDTO.getSalario());
        funcionarioExistente.setAtivo(funcionarioDTO.isAtivo());

        // 4. Faz a mesma conversão de String para Enum do método Salvar
        try {
            funcionarioExistente.setCargo(TipoFuncionario.valueOf(funcionarioDTO.getCargo().toUpperCase()));
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O cargo '" + funcionarioDTO.getCargo() + "' é inválido.");
        }

        // 5. Salva a entidade atualizada
        return funcionarioRepository.save(funcionarioExistente);
    }

    /**
     * Endpoint para DELETAR um funcionário pelo ID.
     * Mapeado para: DELETE /funcionarios/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarFuncionario(@PathVariable Long id) {
        funcionarioService.DeletarFuncionario(id);

        // Retorna 204 NO CONTENT
        return ResponseEntity.noContent().build();
    }

    /**
     * Método auxiliar privado para converter Entidade -> DTO.
     * Ajuda a não repetir código.
     */
    private FuncionarioDTO converterParaDTO(Funcionario funcionario) {
        return new FuncionarioDTO(
                funcionario.getSenha(),
                funcionario.getNome(),
                funcionario.getEmail(),
                funcionario.getCargo().name(), // Converte Enum para String
                funcionario.getSalario(),
                funcionario.isAtivo()

        );
    }
}