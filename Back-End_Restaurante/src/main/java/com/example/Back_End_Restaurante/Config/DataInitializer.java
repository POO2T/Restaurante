package com.example.Back_End_Restaurante.Config;

import com.example.Back_End_Restaurante.Enums.TipoFuncionario;
import com.example.Back_End_Restaurante.Model.Funcionario;
import com.example.Back_End_Restaurante.Repositorio.FuncionarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * Esta classe é executada automaticamente na inicialização do Spring Boot.
 * Ela verifica se um usuário Admin ou Gerente já existe. Se não existir,
 * ela cria um usuário ADMIN padrão para que o sistema possa ser configurado.
 */
@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private FuncionarioRepository funcionarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder; // Injeta o hasher de senha

    @Override
    public void run(String... args) throws Exception {
        // Lista de tipoFuncionarios de administração
        List<TipoFuncionario> adminRoles = Arrays.asList(TipoFuncionario.ADMINISTRADOR, TipoFuncionario.GERENTE);

        // Verifica se já existe algum usuário com um desses tipoFuncionarios
        if (!funcionarioRepository.existsBytipoFuncionarioIn(adminRoles)) {
            // Se nenhum admin/gerente existir, cria um
            System.out.println("Nenhum usuário ADMIN ou GERENTE encontrado. Criando usuário admin padrão...");

            Funcionario adminUser = new Funcionario();
            adminUser.setNome("Administrador Padrão");
            adminUser.setEmail("admin@restaurante.com");
            // SENHA: "admin123" (hasheada)
            adminUser.setSenha(passwordEncoder.encode("admin123"));
            adminUser.settipoFuncionario(TipoFuncionario.ADMINISTRADOR); // Define o tipoFuncionario
            adminUser.setSalario(0.0); // Salário simbólico
            adminUser.setAtivo(true); // Usuário está ativo

            funcionarioRepository.save(adminUser);

            System.out.println("Usuário admin padrão criado:");
            System.out.println("Email: admin@restaurante.com");
            System.out.println("Senha: admin123");
        } else {
            System.out.println("Usuário de administração já existe. Nenhum usuário padrão foi criado.");
        }
    }
}
