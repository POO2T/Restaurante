package com.example.Back_End_Restaurante.Services;

import com.example.Back_End_Restaurante.Dto.*; // Importa todos os DTOs
import com.example.Back_End_Restaurante.Enums.StatusComanda;
import com.example.Back_End_Restaurante.Enums.StatusPedido;
import com.example.Back_End_Restaurante.Enums.StatusProduto;
import com.example.Back_End_Restaurante.Model.Comanda;
import com.example.Back_End_Restaurante.Model.ItemPedido;
import com.example.Back_End_Restaurante.Model.Pedido;
import com.example.Back_End_Restaurante.Model.Produto;
import com.example.Back_End_Restaurante.Repositorio.ComandaRepository;
import com.example.Back_End_Restaurante.Repositorio.PedidoRepository;
import com.example.Back_End_Restaurante.Repositorio.ProdutoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PedidoService {

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private ComandaRepository comandaRepository;

    @Autowired
    private ProdutoRepository produtoRepository;

    // --- LÓGICA DE ADICIONAR PEDIDO (Já feita) ---
    @Transactional
    public PedidoResponseDTO adicionarPedido(Long comandaId, PedidoRequestDTO pedidoRequest) {
        // 1. Encontrar a comanda
        Comanda comanda = comandaRepository.findById(comandaId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Comanda não encontrada"));

        // 2. Regra de Negócio: Não se pode adicionar pedidos a uma comanda fechada
        if (comanda.getStatus() == StatusComanda.FECHADA) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Esta comanda já está fechada");
        }

        // 3. Criar o novo Pedido (a "rodada")
        Pedido novoPedido = new Pedido();
        novoPedido.setComanda(comanda);

        List<ItemPedido> itens = new ArrayList<>();
        double totalPedido = 0.0;

        // 4. Processar cada item do pedido
        for (ItemPedidoRequestDTO itemRequest : pedidoRequest.getItens()) {
            Produto produto = produtoRepository.findById(itemRequest.getProdutoId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Produto com ID " + itemRequest.getProdutoId() + " não encontrado"));

            // 5. Regras de Negócio: Verificar disponibilidade e estoque
            if (produto.getDisponibilidade() == StatusProduto.INDISPONIVEL) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Produto '" + produto.getNome() + "' está indisponível");
            }
            if (produto.getQuantidadeEstoque() < itemRequest.getQuantidade()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Estoque insuficiente para '" + produto.getNome() + "'. Disponível: " + produto.getQuantidadeEstoque());
            }

            // 6. Criar o ItemPedido
            ItemPedido itemPedido = new ItemPedido();
            itemPedido.setProduto(produto);
            itemPedido.setQuantidade(itemRequest.getQuantidade());
            itemPedido.setPrecoUnitario(produto.getPreco()); // "Congela" o preço
            itemPedido.setPedido(novoPedido); // Associa ao novo pedido

            itens.add(itemPedido);
            totalPedido += (produto.getPreco() * itemRequest.getQuantidade());

            // 7. Atualizar o estoque
            produto.setQuantidadeEstoque(produto.getQuantidadeEstoque() - itemRequest.getQuantidade());
            produtoRepository.save(produto); // Salva a atualização do estoque
        }

        novoPedido.setItens(itens);

        // 8. Salvar o pedido (que, por cascata, salvará os itens)
        Pedido pedidoSalvo = pedidoRepository.save(novoPedido);

        // 9. Converter para DTO de resposta
        return converterPedidoParaResponseDTO(pedidoSalvo, totalPedido);
    }

    // --- LÓGICA DA TELA DA COZINHA (NOVO) ---

    /**
     * Busca todos os pedidos que estão com status PENDENTE,
     * em ordem de chegada (mais antigos primeiro).
     */
    @Transactional(readOnly = true)
    public List<PedidoCozinhaDTO> listarPedidosPendentes() {
        // 1. Busca no repositório usando o novo método
        List<Pedido> pedidosPendentes = pedidoRepository.findByStatusOrderByDataHoraAsc(StatusPedido.PENDENTE);

        // 2. Converte a lista de Entidades para a lista de DTOs da Cozinha
        return pedidosPendentes.stream()
                .map(this::converterParaCozinhaDTO)
                .collect(Collectors.toList());
    }

    // --- MÉTODOS HELPERS DE CONVERSÃO ---

    // Converte Pedido -> PedidoCozinhaDTO
    private PedidoCozinhaDTO converterParaCozinhaDTO(Pedido pedido) {
        PedidoCozinhaDTO dto = new PedidoCozinhaDTO();
        dto.setPedidoId(pedido.getId());
        dto.setDataHora(pedido.getDataHora());

        // Pega os dados da Mesa através da Comanda
        dto.setNomeMesa(pedido.getComanda().getMesa().getNome());
        dto.setNumeroMesa(pedido.getComanda().getMesa().getNumero());

        // Converte os itens
        List<ItemPedidoCozinhaDTO> itensDTO = pedido.getItens().stream().map(item -> {
            ItemPedidoCozinhaDTO itemDTO = new ItemPedidoCozinhaDTO();
            itemDTO.setNomeProduto(item.getProduto().getNome());
            itemDTO.setQuantidade(item.getQuantidade());
            return itemDTO;
        }).collect(Collectors.toList());

        dto.setItens(itensDTO);
        return dto;
    }

    // Converte Pedido -> PedidoResponseDTO (usado ao adicionar pedido)
    private PedidoResponseDTO converterPedidoParaResponseDTO(Pedido pedido, Double totalPedido) {
        PedidoResponseDTO response = new PedidoResponseDTO();
        response.setId(pedido.getId());
        response.setDataHora(pedido.getDataHora());
        response.setStatus(pedido.getStatus());
        response.setTotalPedido(totalPedido);

        List<ItemPedidoResponseDTO> itensDTO = pedido.getItens().stream().map(item -> {
            ItemPedidoResponseDTO itemDTO = new ItemPedidoResponseDTO();
            itemDTO.setId(item.getId());
            itemDTO.setNomeProduto(item.getProduto().getNome());
            itemDTO.setQuantidade(item.getQuantidade());
            itemDTO.setPrecoUnitario(item.getPrecoUnitario());
            return itemDTO;
        }).collect(Collectors.toList());

        response.setItens(itensDTO);
        return response;
    }
}