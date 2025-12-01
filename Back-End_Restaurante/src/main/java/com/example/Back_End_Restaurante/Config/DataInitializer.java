package com.example.Back_End_Restaurante.Config;

import com.example.Back_End_Restaurante.Enums.StatusMesa;
import com.example.Back_End_Restaurante.Enums.StatusProduto;
import com.example.Back_End_Restaurante.Enums.TipoFuncionario;
import com.example.Back_End_Restaurante.Model.Categoria;
import com.example.Back_End_Restaurante.Model.Funcionario;
import com.example.Back_End_Restaurante.Model.Mesa;
import com.example.Back_End_Restaurante.Model.Produto;
import com.example.Back_End_Restaurante.Repositorio.CategoriaRepository;
import com.example.Back_End_Restaurante.Repositorio.FuncionarioRepository;
import com.example.Back_End_Restaurante.Repositorio.MesaRepository;
import com.example.Back_End_Restaurante.Repositorio.ProdutoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Inicializador de dados para ambiente de desenvolvimento.
 * Insere Mesas, Categorias, Produtos e cria um usuário administrador padrão
 * caso não existam.
 */
@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private MesaRepository mesaRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Autowired
    private ProdutoRepository produtoRepository;

    @Autowired
    private FuncionarioRepository funcionarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        initMesas();
        initCategoriasEProdutos();
        initAdminUsuario();
    }

    private void initMesas() {
        long count = mesaRepository.count();
        if (count > 0) {
            System.out.println("DataInitializer: Mesas já existem (count=" + count + ")");
            return;
        }

        List<Mesa> mesas = new ArrayList<>();
        for (int i = 1; i <= 8; i++) {
            Mesa m = new Mesa();
            m.setNumero(i);
            m.setNome("Mesa " + i);
            m.setStatus(StatusMesa.DISPONIVEL);
            mesas.add(m);
        }
        mesaRepository.saveAll(mesas);
        System.out.println("DataInitializer: Inseridas " + mesas.size() + " mesas padrão.");
    }

    private void initCategoriasEProdutos() {
        long catCount = categoriaRepository.count();
        long prodCount = produtoRepository.count();

        if (catCount > 0 && prodCount > 0) {
            System.out.println(
                    "DataInitializer: Categorias e produtos já existem (cat=" + catCount + ", prod=" + prodCount + ")");
            return;
        }

        // Criar categorias padrão
        Categoria bebidas = new Categoria();
        bebidas.setNome("Bebidas");

        Categoria pratos = new Categoria();
        pratos.setNome("Pratos Principais");

        Categoria sobremesas = new Categoria();
        sobremesas.setNome("Sobremesas");

        categoriaRepository.saveAll(Arrays.asList(bebidas, pratos, sobremesas));

        List<Produto> produtos = new ArrayList<>();

        Produto p1 = new Produto();
        p1.setNome("Refrigerante Lata");
        p1.setDescricao("Refrigerante gelado de 350ml");
        p1.setPreco(6.50);
        p1.setQuantidadeEstoque(50);
        p1.setDisponibilidade(StatusProduto.DISPONIVEL);
        p1.setCategoria(bebidas);
        produtos.add(p1);

        Produto p2 = new Produto();
        p2.setNome("Suco Natural");
        p2.setDescricao("Suco natural do dia");
        p2.setPreco(8.00);
        p2.setQuantidadeEstoque(30);
        p2.setDisponibilidade(StatusProduto.DISPONIVEL);
        p2.setCategoria(bebidas);
        produtos.add(p2);

        Produto p3 = new Produto();
        p3.setNome("Bife Acebolado");
        p3.setDescricao("Prato principal com arroz e salada");
        p3.setPreco(28.00);
        p3.setQuantidadeEstoque(20);
        p3.setDisponibilidade(StatusProduto.DISPONIVEL);
        p3.setCategoria(pratos);
        produtos.add(p3);

        Produto p4 = new Produto();
        p4.setNome("Lasanha Bolonhesa");
        p4.setDescricao("Lasanha caseira com molho bolonhesa");
        p4.setPreco(32.00);
        p4.setQuantidadeEstoque(15);
        p4.setDisponibilidade(StatusProduto.DISPONIVEL);
        p4.setCategoria(pratos);
        produtos.add(p4);

        Produto p5 = new Produto();
        p5.setNome("Pudim");
        p5.setDescricao("Pudim de leite condensado");
        p5.setPreco(7.50);
        p5.setQuantidadeEstoque(25);
        p5.setDisponibilidade(StatusProduto.DISPONIVEL);
        p5.setCategoria(sobremesas);
        produtos.add(p5);

        produtoRepository.saveAll(produtos);
        System.out.println("DataInitializer: Inseridas categorias e " + produtos.size() + " produtos padrão.");
    }

    private void initAdminUsuario() {
        List<TipoFuncionario> adminRoles = Arrays.asList(TipoFuncionario.ADMINISTRADOR, TipoFuncionario.GERENTE);
        if (!funcionarioRepository.existsByCargoIn(adminRoles)) {
            System.out.println("Nenhum usuário ADMIN ou GERENTE encontrado. Criando usuário admin padrão...");

            Funcionario adminUser = new Funcionario();
            adminUser.setNome("Administrador Padrão");
            adminUser.setEmail("admin@restaurante.com");
            adminUser.setSenha(passwordEncoder.encode("admin123"));
            adminUser.setCargo(TipoFuncionario.ADMINISTRADOR);
            adminUser.setSalario(0.0);
            adminUser.setAtivo(true);

            funcionarioRepository.save(adminUser);

            System.out.println("Usuário admin padrão criado: admin@restaurante.com / admin123");
        } else {
            System.out.println("Usuário de administração já existe. Nenhum usuário padrão foi criado.");
        }
    }
}
