package com.example.Back_End_Restaurante.Controllers;

import com.example.Back_End_Restaurante.Dto.FuncionarioDTO;
import com.example.Back_End_Restaurante.Model.Funcionario;
import com.example.Back_End_Restaurante.Services.FuncionarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize; // Importar
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/funcionarios") // Padronizado para /api/funcionarios
// @CrossOrigin(origins = "*") // Removido - Controlado globalmente pelo SecurityConfig
public class FuncionarioController {

    // Removemos o FuncionarioRepository, o Controller só fala com o Service
    @Autowired
    private FuncionarioService funcionarioService;

    /**
     * Endpoint para LISTAR todos os funcionários.
     * Mapeado para: GET /api/funcionarios
     * Somente Administradores ou Gerentes podem ver.
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'GERENTE')")
    public ResponseEntity<List<FuncionarioDTO>> listarFuncionarios() {
        // O Service agora retorna a lista de DTOs pronta
        List<FuncionarioDTO> dtos = funcionarioService.ListarFuncionarios();
        return ResponseEntity.ok(dtos);
    }

    /**
     * Endpoint para SALVAR um novo funcionário (cadastro).
     * Mapeado para: POST /api/funcionarios
     * Somente Administradores ou Gerentes podem criar novos funcionários.
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'GERENTE')")
    public ResponseEntity<FuncionarioDTO> salvarFuncionario(@RequestBody FuncionarioDTO funcionarioDTO, UriComponentsBuilder uriBuilder) {
        // 1. Service salva a entidade (e hasheia a senha)
        Funcionario funcionarioSalvo = funcionarioService.SalvarFuncionario(funcionarioDTO);

        // 2. Service converte a entidade salva para DTO (sem senha)
        FuncionarioDTO dtoResposta = funcionarioService.converterParaDTO(funcionarioSalvo);

        // 3. Cria a URI do novo recurso
        URI location = uriBuilder.path("/api/funcionarios/{id}")
                .buildAndExpand(funcionarioSalvo.getId())
                .toUri();

        // 4. Retorna 201 CREATED com o DTO de resposta
        return ResponseEntity.created(location).body(dtoResposta);
    }

    /**
     * Endpoint para ATUALIZAR um funcionário existente.
     * Mapeado para: PUT /api/funcionarios/{id}
     * Somente Administradores ou Gerentes podem atualizar.
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'GERENTE')")
    public ResponseEntity<FuncionarioDTO> AtualizarFuncionario(@PathVariable Long id, @RequestBody FuncionarioDTO funcionarioDTO) {
        // 1. Service atualiza a entidade
        Funcionario funcionarioAtualizado = funcionarioService.AtualizarFuncionario(id, funcionarioDTO);

        // 2. Service converte a entidade atualizada para DTO (sem senha)
        FuncionarioDTO dtoResposta = funcionarioService.converterParaDTO(funcionarioAtualizado);

        // 3. Retorna 200 OK com o DTO de resposta
        return ResponseEntity.ok(dtoResposta);
    }

    /**
     * Endpoint para DELETAR um funcionário pelo ID.
     * Mapeado para: DELETE /api/funcionarios/{id}
     * Somente Administradores ou Gerentes podem deletar.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'GERENTE')")
    public ResponseEntity<Void> deletarFuncionario(@PathVariable Long id) {
        funcionarioService.DeletarFuncionario(id);
        return ResponseEntity.noContent().build(); // 204 NO CONTENT
    }

    // O método privado converterParaDTO() foi REMOVIDO daqui.
    // A lógica agora está centralizada no FuncionarioService.
}