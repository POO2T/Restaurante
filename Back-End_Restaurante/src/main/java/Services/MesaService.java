package Services;



import Dto.MesaDTO;
import Enums.StatusMesa;

import Model.Mesa;
import Repositorio.MesaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class MesaService {

    @Autowired
    private MesaRepository mesaRepository;

    public List<Mesa> listarTodasMesas() {
        return mesaRepository.findAllByOrderByNumeroAsc();
    }

    public Optional<Mesa> buscarPorId(Long id) {
        return mesaRepository.findById(id);
    }

    public Optional<Mesa> buscarPorNumero(Integer numero) {
        return mesaRepository.findByNumero(numero);
    }

    public Mesa ocuparMesa(Long id) {
        Mesa mesa = mesaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Mesa não encontrada"));

        if (mesa.getStatus() != StatusMesa.DISPONIVEL) {
            throw new RuntimeException("Mesa não está livre para ocupação");
        }

        mesa.setStatus(StatusMesa.OCUPADO);
        return mesaRepository.save(mesa);
    }

    public Mesa liberarMesa(Long id) {
        Mesa mesa = mesaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Mesa não encontrada"));

        mesa.setStatus(StatusMesa.DISPONIVEL);
        return mesaRepository.save(mesa);
    }



    public List<Mesa> listarPorStatus(StatusMesa status) {
        return mesaRepository.findByStatus(status);
    }

    public Mesa criarMesa(MesaDTO mesaDTO) {


        Mesa novaMesa = new Mesa();


        novaMesa.setNome(mesaDTO.getNome());


        novaMesa.setStatus(StatusMesa.DISPONIVEL);


        return mesaRepository.save(novaMesa);
    }
}
