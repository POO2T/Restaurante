package Controllers;

import Dto.MesaDTO;
import Enums.StatusMesa;
import Model.Mesa;
import Services.MesaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/mesas")
@CrossOrigin(origins = "*")
public class MesaController {

    @Autowired
    private MesaService mesaService;

    @GetMapping
    public ResponseEntity<List<MesaDTO>> listarMesas() {
        List<Mesa> mesas = mesaService.listarTodasMesas();
        List<MesaDTO> mesasDTO = mesas.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(mesasDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MesaDTO> buscarMesa(@PathVariable Long id) {
        return mesaService.buscarPorId(id)
                .map(mesa -> ResponseEntity.ok(convertToDTO(mesa)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{id}/ocupar")
    public ResponseEntity<MesaDTO> ocuparMesa(@PathVariable Long id) {
        try {
            Mesa mesa = mesaService.ocuparMesa(id);
            return ResponseEntity.ok(convertToDTO(mesa));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/{id}/liberar")
    public ResponseEntity<MesaDTO> liberarMesa(@PathVariable Long id) {
        try {
            Mesa mesa = mesaService.liberarMesa(id);
            return ResponseEntity.ok(convertToDTO(mesa));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }



    @GetMapping("/status/{status}")
    public ResponseEntity<List<MesaDTO>> listarPorStatus(@PathVariable StatusMesa status) {
        List<Mesa> mesas = mesaService.listarPorStatus(status);
        List<MesaDTO> mesasDTO = mesas.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(mesasDTO);
    }

    private MesaDTO convertToDTO(Mesa mesa) {
        return new MesaDTO(mesa.getId(), mesa.getNome(),
                 mesa.getStatus());
    }

    @PostMapping
    public ResponseEntity<MesaDTO> criarMesa(@RequestBody MesaDTO mesaDTO) {
        // 1. O service cria a entidade e a salva no banco
        Mesa novaMesa = mesaService.criarMesa(mesaDTO);

        // 2. Converte a entidade salva de volta para um DTO para a resposta
        MesaDTO mesaRespostaDTO = convertToDTO(novaMesa);

        // 3. Retorna o status 201 Created com o DTO no corpo
        return ResponseEntity.status(HttpStatus.CREATED).body(mesaRespostaDTO);
    }
}
