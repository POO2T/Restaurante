package com.example.Back_End_Restaurante.Services;

import com.example.Back_End_Restaurante.Dto.FuncionarioDTO;
import com.example.Back_End_Restaurante.Enums.TipoFuncionario;
import com.example.Back_End_Restaurante.Model.Funcionario;
import com.example.Back_End_Restaurante.Repositorio.FuncionarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FuncionarioService {

    @Autowired
    private FuncionarioRepository funcionarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder; // Injeta o codificador

    /**
     * Lista todos os funcionários e os converte para DTOs (sem senha).
     * Chamado pelo FuncionarioController (GET /api/funcionarios).
     */
    @PreAuthorize("hasAnyRole('GERENTE', 'ADMINISTRADOR')")
    public List<FuncionarioDTO> ListarFuncionarios() {
        return funcionarioRepository.findAll()
                .stream()
                .map(this::converterParaDTO) // Usa o método auxiliar
                .collect(Collectors.toList());
    }

    /**
     * Salva um novo funcionário, validando e hasheando a senha.
     * Chamado pelo FuncionarioController (POST /api/funcionarios).
     */
    @PreAuthorize("hasAnyRole('GERENTE', 'ADMINISTRADOR')")
    public Funcionario SalvarFuncionario(FuncionarioDTO funcionarioDTO) {
        // 1. Validação de E-mail
        if (funcionarioDTO.getEmail() == null || funcionarioDTO.getEmail().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O e-mail não pode ser vazio.");
        }
        if (funcionarioRepository.existsByEmail(funcionarioDTO.getEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Este e-mail já está em uso.");
        }

        // 2. Validação de Senha
        if (funcionarioDTO.getSenha() == null || funcionarioDTO.getSenha().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A senha não pode ser vazia.");
        }

        // 3. Criar a entidade Funcionario
        Funcionario funcionario = new Funcionario();
        funcionario.setNome(funcionarioDTO.getNome());
        funcionario.setEmail(funcionarioDTO.getEmail());
        // 4. Hashear a senha
        funcionario.setSenha(passwordEncoder.encode(funcionarioDTO.getSenha()));

        funcionario.setSalario(funcionarioDTO.getSalario());
        funcionario.setAtivo(funcionarioDTO.isAtivo());

        // 5. Converter tipoFuncionario (String do DTO para Enum da Entidade)
        try {
            funcionario.settipoFuncionario(TipoFuncionario.valueOf(funcionarioDTO.gettipoFuncionario().toUpperCase()));
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O tipoFuncionario '" + funcionarioDTO.gettipoFuncionario() + "' é inválido.");
        }

        // 6. Salvar no banco
        return funcionarioRepository.save(funcionario);
    }

    /**
     * Deleta um funcionário pelo ID.
     * Chamado pelo FuncionarioController (DELETE /api/funcionarios/{id}).
     */
    @PreAuthorize("hasAnyRole('GERENTE', 'ADMINISTRADOR')")
    public void DeletarFuncionario(Long id) {
        if (!funcionarioRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Funcionário não encontrado com o ID: " + id);
        }
        funcionarioRepository.deleteById(id);
    }

    /**
     * Atualiza um funcionário existente.
     * Chamado pelo FuncionarioController (PUT /api/funcionarios/{id}).
     */
    @PreAuthorize("hasAnyRole('GERENTE', 'ADMINISTRADOR')")
    public Funcionario AtualizarFuncionario(Long id, FuncionarioDTO funcionarioDTO) {
        // 1. Buscar o funcionário existente
        Funcionario funcionarioExistente = funcionarioRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Funcionário não encontrado com o ID: " + id));

        // 2. Validar mudança de e-mail (se o novo e-mail já existe em OUTRO usuário)
        if (funcionarioDTO.getEmail() != null && !funcionarioDTO.getEmail().equals(funcionarioExistente.getEmail()) &&
                funcionarioRepository.existsByEmail(funcionarioDTO.getEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Este e-mail já está em uso por outro usuário.");
        }

        // 3. Atualizar os dados (coalescência nula: só atualiza se o DTO enviou dado novo)
        if (funcionarioDTO.getNome() != null) {
            funcionarioExistente.setNome(funcionarioDTO.getNome());
        }
        if (funcionarioDTO.getEmail() != null) {
            funcionarioExistente.setEmail(funcionarioDTO.getEmail());
        }
        if (funcionarioDTO.getSalario() > 0) { // Exemplo de verificação simples
            funcionarioExistente.setSalario(funcionarioDTO.getSalario());
        }
        // 'ativo' é boolean, então geralmente é enviado (true/false)
        funcionarioExistente.setAtivo(funcionarioDTO.isAtivo());

        // 4. Atualizar a senha (só se uma nova senha foi fornecida)
        if (funcionarioDTO.getSenha() != null && !funcionarioDTO.getSenha().isEmpty()) {
            funcionarioExistente.setSenha(passwordEncoder.encode(funcionarioDTO.getSenha()));
        }

        // 5. Atualizar o tipoFuncionario (só se foi fornecido)
        if (funcionarioDTO.gettipoFuncionario() != null) {
            try {
                funcionarioExistente.settipoFuncionario(TipoFuncionario.valueOf(funcionarioDTO.gettipoFuncionario().toUpperCase()));
            } catch (IllegalArgumentException e) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O tipoFuncionario '" + funcionarioDTO.gettipoFuncionario() + "' é inválido.");
            }
        }

        // 6. Salvar as atualizações
        return funcionarioRepository.save(funcionarioExistente);
    }

    /**
     * Método auxiliar para converter uma Entidade Funcionario em um FuncionarioDTO.
     * Garante que a senha (mesmo hasheada) NUNCA seja enviada para o frontend.
     */
    public FuncionarioDTO converterParaDTO(Funcionario funcionario) {
        if (funcionario == null) {
            return null;
        }
        FuncionarioDTO dto = new FuncionarioDTO();
        dto.setId(funcionario.getId()); // <<< ADICIONADO
        dto.setNome(funcionario.getNome());
        dto.setEmail(funcionario.getEmail());
        dto.settipoFuncionario(funcionario.gettipoFuncionario().name()); // Converte Enum para String
        dto.setSalario(funcionario.getSalario());
        dto.setAtivo(funcionario.isAtivo());
        // SENHA NÃO É INCLUÍDA AQUI (propositalmente)

        return dto;
    }
}