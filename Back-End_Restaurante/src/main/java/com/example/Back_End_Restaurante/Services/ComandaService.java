package com.example.Back_End_Restaurante.Services;

import com.example.Back_End_Restaurante.Dto.*;
import com.example.Back_End_Restaurante.Enums.FormaPagamento;
import com.example.Back_End_Restaurante.Enums.StatusComanda;
import com.example.Back_End_Restaurante.Enums.StatusMesa;
import com.example.Back_End_Restaurante.Model.*;
import com.example.Back_End_Restaurante.Repositorio.ClienteRepository;
import com.example.Back_End_Restaurante.Repositorio.ComandaRepository;
import com.example.Back_End_Restaurante.Repositorio.MesaRepository;
import com.example.Back_End_Restaurante.Repositorio.PagamentoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ComandaService {

    @Autowired
    private ComandaRepository comandaRepository;
    @Autowired
    private MesaRepository mesaRepository;
    @Autowired
    private ClienteRepository clienteRepository;
    @Autowired
    private PagamentoRepository pagamentoRepository;

    // ... (Métodos de abertura de comanda MANTIDOS IGUAIS) ...
    @Transactional
    public ComandaResponseDTO abrirComandaVisitante(ComandaAberturaRequestDTO request) {
        Mesa mesa = buscarEMudarStatusMesa(request.getMesaId());
        Comanda novaComanda = new Comanda();
        novaComanda.setMesa(mesa);
        novaComanda.setCliente(null);
        Comanda comandaSalva = comandaRepository.save(novaComanda);
        return converterParaResponseDTO(comandaSalva);
    }

    @Transactional
    public ComandaResponseDTO abrirComandaAutenticada(ComandaAberturaRequestDTO request, String emailCliente) {
        Cliente cliente = clienteRepository.findByEmail(emailCliente)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente não encontrado"));
        Mesa mesa = buscarEMudarStatusMesa(request.getMesaId());
        Comanda novaComanda = new Comanda();
        novaComanda.setMesa(mesa);
        novaComanda.setCliente(cliente);
        Comanda comandaSalva = comandaRepository.save(novaComanda);
        return converterParaResponseDTO(comandaSalva);
    }

    // ... (Método getDetalhesComanda MANTIDO IGUAL) ...
    @Transactional(readOnly = true)
    public ComandaDetalhadaDTO getDetalhesComanda(Long comandaId) {
        Comanda comanda = comandaRepository.findById(comandaId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Comanda não encontrada"));
        return converterParaDetalhadaDTO(comanda);
    }

    // Lista todas as comandas com status ABERTA (usado pela tela de
    // pedidos/cozinha)
    @Transactional(readOnly = true)
    public List<ComandaDetalhadaDTO> listarComandasAbertas() {
        return comandaRepository.findByStatus(StatusComanda.ABERTA)
                .stream()
                .map(this::converterParaDetalhadaDTO)
                .collect(Collectors.toList());
    }

    // --- NOVO: LISTAR HISTÓRICO DO CLIENTE ---
    @Transactional(readOnly = true)
    public List<ComandaDetalhadaDTO> listarHistoricoCliente(String emailCliente) {
        Cliente cliente = clienteRepository.findByEmail(emailCliente)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente não encontrado"));

        return comandaRepository.findByClienteOrderByDataAberturaDesc(cliente)
                .stream()
                .map(this::converterParaDetalhadaDTO)
                .collect(Collectors.toList());
    }

    // --- ATUALIZADO: FECHAR COMANDA COM PONTOS DE FIDELIDADE ---
    @Transactional
    public ComandaDetalhadaDTO fecharComanda(Long comandaId, PagamentoRequestDTO pagamentoRequest) {
        Comanda comanda = comandaRepository.findById(comandaId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Comanda não encontrada"));

        if (comanda.getStatus() == StatusComanda.FECHADA) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Esta comanda já está fechada.");
        }

        double totalCalculado = calcularTotalComanda(comanda);
        double totalPago = comanda.getPagamentos().stream().mapToDouble(Pagamento::getValor).sum();
        double valorRecebido = pagamentoRequest.getValorPago();

        double saldoPendente = totalCalculado - totalPago;
        if (valorRecebido < (saldoPendente - 0.01)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Valor pago (R$" + valorRecebido + ") é insuficiente. Saldo pendente: R$" + saldoPendente);
        }

        Pagamento novoPagamento = new Pagamento();
        try {
            novoPagamento.setForma(FormaPagamento.valueOf(pagamentoRequest.getFormaPagamento().toUpperCase()));
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Forma de pagamento inválida.");
        }
        novoPagamento.setValor(valorRecebido);
        comanda.addPagamento(novoPagamento);

        comanda.setStatus(StatusComanda.FECHADA);
        comanda.setDataFechamento(LocalDateTime.now());

        Mesa mesa = comanda.getMesa();
        mesa.setStatus(StatusMesa.DISPONIVEL);
        mesaRepository.save(mesa);

        // --- LÓGICA DE FIDELIDADE (NOVO) ---
        if (comanda.getCliente() != null) {
            // Regra simples: 1 ponto para cada 1 Real gasto
            int pontosGanhos = (int) totalCalculado;
            comanda.getCliente().adicionarPontos(pontosGanhos);
            clienteRepository.save(comanda.getCliente()); // Salva os pontos
        }
        // -----------------------------------

        Comanda comandaFechada = comandaRepository.save(comanda);
        return converterParaDetalhadaDTO(comandaFechada);
    }

    // ... (Métodos helpers e conversores MANTIDOS IGUAIS) ...
    private Mesa buscarEMudarStatusMesa(Long mesaId) {
        if (mesaId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O ID da mesa é obrigatório");
        }
        Mesa mesa = mesaRepository.findById(mesaId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Mesa não encontrada"));
        if (mesa.getStatus() != StatusMesa.DISPONIVEL) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Mesa " + mesa.getNumero() + " já está ocupada");
        }
        mesa.setStatus(StatusMesa.OCUPADA);
        mesaRepository.save(mesa);
        return mesa;
    }

    private ComandaResponseDTO converterParaResponseDTO(Comanda comanda) {
        Long clienteId = (comanda.getCliente() != null) ? comanda.getCliente().getId() : null;
        return new ComandaResponseDTO(
                comanda.getId(), comanda.getStatus().name(), comanda.getDataAbertura(),
                comanda.getMesa().getId(), clienteId, comanda.getMesa().getNumero());
    }

    private Double calcularTotalComanda(Comanda comanda) {
        if (comanda.getPedidos() == null)
            return 0.0;
        double total = 0.0;
        for (Pedido pedido : comanda.getPedidos()) {
            if (pedido.getItens() != null) {
                for (ItemPedido item : pedido.getItens()) {
                    total += (item.getPrecoUnitario() * item.getQuantidade());
                }
            }
        }
        return total;
    }

    private ComandaDetalhadaDTO converterParaDetalhadaDTO(Comanda comanda) {
        ComandaDetalhadaDTO dto = new ComandaDetalhadaDTO();
        dto.setId(comanda.getId());
        dto.setDataAbertura(comanda.getDataAbertura());
        dto.setDataFechamento(comanda.getDataFechamento());
        dto.setStatus(comanda.getStatus());
        if (comanda.getMesa() != null) {
            dto.setNomeMesa(comanda.getMesa().getNome());
            dto.setNumeroMesa(comanda.getMesa().getNumero());
        }
        if (comanda.getCliente() != null) {
            dto.setNomeCliente(comanda.getCliente().getNome());
        }
        double totalCalculado = calcularTotalComanda(comanda);
        double totalPago = comanda.getPagamentos().stream().mapToDouble(Pagamento::getValor).sum();
        dto.setTotalCalculado(totalCalculado);
        dto.setTotalPago(totalPago);
        dto.setSaldoPendente(totalCalculado - totalPago);

        dto.setPedidos(
                comanda.getPedidos().stream().map(this::converterPedidoParaResponseDTO).collect(Collectors.toList()));
        dto.setPagamentos(comanda.getPagamentos().stream().map(this::converterPagamentoParaResponseDTO)
                .collect(Collectors.toList()));
        return dto;
    }

    private PedidoResponseDTO converterPedidoParaResponseDTO(Pedido pedido) {
        PedidoResponseDTO response = new PedidoResponseDTO();
        response.setId(pedido.getId());
        response.setDataHora(pedido.getDataHora());
        response.setStatus(pedido.getStatus());
        List<ItemPedidoResponseDTO> itensDTO = pedido.getItens().stream().map(item -> {
            ItemPedidoResponseDTO itemDTO = new ItemPedidoResponseDTO();
            itemDTO.setId(item.getId());
            itemDTO.setNomeProduto(item.getProduto().getNome());
            itemDTO.setQuantidade(item.getQuantidade());
            itemDTO.setPrecoUnitario(item.getPrecoUnitario());
            return itemDTO;
        }).collect(Collectors.toList());
        response.setTotalPedido(itensDTO.stream().mapToDouble(i -> i.getPrecoUnitario() * i.getQuantidade()).sum());
        response.setItens(itensDTO);
        return response;
    }

    private PagamentoResponseDTO converterPagamentoParaResponseDTO(Pagamento pagamento) {
        PagamentoResponseDTO dto = new PagamentoResponseDTO();
        dto.setId(pagamento.getId());
        dto.setDataPagamento(pagamento.getDataPagamento());
        dto.setForma(pagamento.getForma());
        dto.setValor(pagamento.getValor());
        dto.setComandaId(pagamento.getComanda().getId());
        return dto;
    }
}