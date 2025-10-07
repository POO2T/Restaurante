package Repositorio;

import Model.Produto;
import Model.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProdutoRepository extends JpaRepository<Produto, Long> {
    List<Produto> findByDisponivel(Boolean disponivel);
    List<Produto> findByCategoria(Categoria categoria);
    List<Produto> findByCategoriaAndDisponivel(Categoria categoria, Boolean disponivel);
    List<Produto> findByDisponivelOrderByCategoriaAscNomeAsc(Boolean disponivel);
}