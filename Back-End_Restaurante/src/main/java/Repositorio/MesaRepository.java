package Repositorio;

import Model.Mesa;
import Enums.StatusMesa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface MesaRepository extends JpaRepository<Mesa, Long> {
    Optional<Mesa> findByNumero(Integer numero);
    List<Mesa> findByStatus(StatusMesa status);
    List<Mesa> findAllByOrderByNumeroAsc();
}