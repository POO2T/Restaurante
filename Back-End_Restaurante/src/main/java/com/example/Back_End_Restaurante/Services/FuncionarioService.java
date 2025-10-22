package com.example.Back_End_Restaurante.Services;


import com.example.Back_End_Restaurante.Dto.FuncionarioDTO;
import com.example.Back_End_Restaurante.Enums.TipoFuncionario;
import com.example.Back_End_Restaurante.Model.Funcionario;
import com.example.Back_End_Restaurante.Repositorio.FuncionarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class FuncionarioService
{
    @Autowired
    private FuncionarioRepository funcionarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public List<Funcionario> ListarFuncionarios() {
        return funcionarioRepository.findAll();
    }

    public Funcionario SalvarFuncionario(FuncionarioDTO funcionarioDTO) {
        // Adicione a validação de e-mail que você usou no Cliente!
        if (funcionarioRepository.existsByEmail(funcionarioDTO.getEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Este e-mail já está em uso.");
        }

        Funcionario funcionario = new Funcionario();
        funcionario.setNome(funcionarioDTO.getNome());
        funcionario.setEmail(funcionarioDTO.getEmail());
        funcionario.setSenha(passwordEncoder.encode(funcionarioDTO.getSenha()));

        // Seta os novos campos
        funcionario.setSalario(funcionarioDTO.getSalario());
        funcionario.setAtivo(funcionarioDTO.isAtivo()); // ou true por padrão

        try {
            funcionario.setCargo(TipoFuncionario.valueOf(funcionarioDTO.getCargo().toUpperCase()));
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O cargo '" + funcionarioDTO.getCargo() + "' é inválido.");
        }

        return funcionarioRepository.save(funcionario);
    }

    public void DeletarFuncionario(Long id) {
        funcionarioRepository.deleteById(id);
    }

    public Funcionario AtualizarFuncionario(Long id, FuncionarioDTO funcionarioDTO) {
        Funcionario funcionarioExistente = funcionarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Funcionário não encontrado"));

        // Valida o e-mail (se ele foi alterado e já existe em OUTRA pessoa)
        if (!funcionarioExistente.getEmail().equals(funcionarioDTO.getEmail()) &&
                funcionarioRepository.existsByEmail(funcionarioDTO.getEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Este e-mail já está em uso por outro usuário.");
        }

        funcionarioExistente.setNome(funcionarioDTO.getNome());
        funcionarioExistente.setEmail(funcionarioDTO.getEmail());
        funcionarioExistente.setSalario(funcionarioDTO.getSalario());
        funcionarioExistente.setAtivo(funcionarioDTO.isAtivo());

        // 👇 HASHEAR A SENHA SE ELA FOI FORNECIDA PARA ATUALIZAÇÃO 👇
        // (Verificar se a senha no DTO não é nula ou vazia antes de hashear)
        if (funcionarioDTO.getSenha() != null && !funcionarioDTO.getSenha().isEmpty()) {
            // Aqui você pode adicionar lógica extra, como verificar se a senha antiga bate,
            // mas para simplificar, vamos apenas atualizar com a nova senha hasheada.
            funcionarioExistente.setSenha(passwordEncoder.encode(funcionarioDTO.getSenha()));
        }


        // Faz a mesma conversão de String para Enum do método Salvar
        try {
            funcionarioExistente.setCargo(TipoFuncionario.valueOf(funcionarioDTO.getCargo().toUpperCase()));
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O cargo '" + funcionarioDTO.getCargo() + "' é inválido.");
        }

        return funcionarioRepository.save(funcionarioExistente);
    }


}
