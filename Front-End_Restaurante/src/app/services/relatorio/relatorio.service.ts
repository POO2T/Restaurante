import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { catchError, tap, timeout } from 'rxjs/operators';
import { ApiService } from '../api';
import { Comanda } from '../../models/comanda.model';

export interface PedidoAPI {
  id: number;
  dataHora: string;
  status: string;
  itens: ItemPedidoAPI[];
  comanda?: any;
}

export interface ItemPedidoAPI {
  id: number;
  quantidade: number;
  precoUnitario: number;
  produto: any;
}

export interface DadosFinanceiros {
  mes: string;
  receita: number;
  despesa: number;
  lucro: number;
}

export interface ProdutoTop {
  nome: string;
  quantidade: number;
  receita: number;
}

// Tipos retornados pelos endpoints de relatórios do backend
export interface DashboardResumoDTO {
  totalPedidosHoje: number;
  faturamentoHoje: number;
  mesasOcupadasAgora: number;
}

export interface ProdutoVendidoDTO {
  nomeProduto: string;
  quantidadeTotal: number;
  valorTotal: number;
}

// Comanda detalhada retornada pelo backend (cliente)
export interface PedidoResponseDTO {
  id: number;
  dataHora: string; // ISO datetime
  status: string;
  itens: Array<{
    id: number;
    nomeProduto?: string;
    quantidade: number;
    precoUnitario?: number;
  }>;
  totalPedido?: number;
}

export interface ComandaDetalhadaDTO {
  id: number;
  dataAbertura?: string;
  dataFechamento?: string | null;
  status?: string;
  nomeMesa?: string;
  numeroMesa?: number;
  nomeCliente?: string | null;
  pedidos: PedidoResponseDTO[];
  totalCalculado?: number;
  totalPago?: number;
}

@Injectable({
  providedIn: 'root',
})
export class RelatorioService {
  private api = inject(ApiService);

  constructor() {}

  /**
   * Busca todos os pedidos da API
   */
  // obterPedidos(): Observable<PedidoAPI[]> {
  //   console.log('🔄 Chamando API via ApiService: /pedidos');
  //   return this.api.get<PedidoAPI[]>('/pedidos').pipe(
  //     timeout(10000) // 10 segundos de timeout
  //   );
  // }

  getComandasDetalhadas(comanda: Comanda): Observable<ComandaDetalhadaDTO[]> {
    console.log(`🔄 Chamando API via ApiService: /${comanda.id}/detalhes`);
    // Este método foi removido pois o backend não expõe um endpoint
    // que retorne todas as comandas detalhadas. Para cálculos mensais
    // o backend deve fornecer um endpoint de relatórios apropriado.
    throw new Error(
      'getComandasDetalhadas não suportado: endpoint inexistente'
    );
  }

  /**
   * Busca o resumo do dia (dashboard) do backend
   */
  obterDashboard(): Observable<DashboardResumoDTO> {
    console.log('🔄 Chamando API via ApiService: /relatorios/dashboard');
    return this.api
      .get<DashboardResumoDTO>('/relatorios/dashboard')
      .pipe(timeout(8000));
  }

  /**
   * Busca top produtos já calculados pelo backend
   */
  obterTopProdutos(): Observable<ProdutoVendidoDTO[]> {
    console.log(
      '🔄 Chamando API via ApiService: /relatorios/produtos-mais-vendidos'
    );
    return this.api
      .get<ProdutoVendidoDTO[]>('/relatorios/produtos-mais-vendidos')
      .pipe(timeout(8000));
  }

  /**
   * Busca o histórico de comandas do cliente logado
   */
  // obterHistoricoCliente(): Observable<ComandaDetalhadaDTO[]> {
  //  console.log('🔄 Chamando API via ApiService: /comandas/meu-historico');
  //   return this.api.get<ComandaDetalhadaDTO[]>('/comandas/meu-historico').pipe(
  //     timeout(8000)
  //   );
  // }

  /**
   * Calcula dados financeiros mensais a partir dos pedidos
   */
  calcularDadosFinanceiros(pedidos: PedidoAPI[]): DadosFinanceiros[] {
    const mesesMap = new Map<number, { receita: number; despesa: number }>();

    // Meses em português
    const mesesPT = [
      'Jan',
      'Fev',
      'Mar',
      'Abr',
      'Mai',
      'Jun',
      'Jul',
      'Ago',
      'Set',
      'Out',
      'Nov',
      'Dez',
    ];

    // Inicializa todos os meses com 0
    for (let i = 0; i < 12; i++) {
      mesesMap.set(i, { receita: 0, despesa: 0 });
    }

    // Processa cada pedido
    pedidos.forEach((pedido) => {
      const data = new Date(pedido.dataHora);
      const mesIndex = data.getMonth();

      // Calcula total do pedido
      const totalPedido = pedido.itens.reduce((acc, item) => {
        return acc + item.quantidade * item.precoUnitario;
      }, 0);

      // Adiciona à receita do mês
      const mesDado = mesesMap.get(mesIndex);
      if (mesDado) {
        mesDado.receita += totalPedido;
        // Estima despesa como 30% da receita (pode ser ajustado)
        mesDado.despesa += totalPedido * 0.3;
      }
    });

    // Converte para array e calcula lucro
    const resultado: DadosFinanceiros[] = [];
    for (let i = 0; i < 12; i++) {
      const valor = mesesMap.get(i);
      if (valor) {
        resultado.push({
          mes: mesesPT[i],
          receita: valor.receita,
          despesa: valor.despesa,
          lucro: valor.receita - valor.despesa,
        });
      }
    }

    return resultado;
  }

  /**
   * Calcula top produtos a partir dos pedidos
   */
  calcularTopProdutos(pedidos: PedidoAPI[], limite: number = 4): ProdutoTop[] {
    const produtosMap = new Map<
      string,
      { quantidade: number; receita: number }
    >();

    // Processa cada item de pedido
    pedidos.forEach((pedido) => {
      pedido.itens.forEach((item) => {
        const nomeProduto = item.produto?.nome || 'Produto Desconhecido';
        const receita = item.quantidade * item.precoUnitario;

        if (produtosMap.has(nomeProduto)) {
          const atual = produtosMap.get(nomeProduto)!;
          atual.quantidade += item.quantidade;
          atual.receita += receita;
        } else {
          produtosMap.set(nomeProduto, {
            quantidade: item.quantidade,
            receita,
          });
        }
      });
    });

    // Converte para array e ordena por receita (descending)
    const resultado = Array.from(produtosMap.entries()).map(
      ([nome, dados]) => ({
        nome,
        quantidade: dados.quantidade,
        receita: dados.receita,
      })
    );

    return resultado.sort((a, b) => b.receita - a.receita).slice(0, limite);
  }

  /**
   * Calcula resumo financeiro geral
   */
  calcularResumoGeral(pedidos: PedidoAPI[]): any {
    let totalReceita = 0;
    let totalDespesa = 0;
    let totalItens = 0;

    pedidos.forEach((pedido) => {
      const totalPedido = pedido.itens.reduce((acc, item) => {
        totalItens += item.quantidade;
        return acc + item.quantidade * item.precoUnitario;
      }, 0);

      totalReceita += totalPedido;
      totalDespesa += totalPedido * 0.3; // Estima despesa como 30%
    });

    const totalLucro = totalReceita - totalDespesa;
    const margemLucro =
      totalReceita > 0 ? (totalLucro / totalReceita) * 100 : 0;
    const ticketMedio = pedidos.length > 0 ? totalReceita / pedidos.length : 0;

    return {
      totalReceita,
      totalDespesa,
      totalLucro,
      margemLucro,
      ticketMedio,
      totalItens,
      totalPedidos: pedidos.length,
    };
  }
}
