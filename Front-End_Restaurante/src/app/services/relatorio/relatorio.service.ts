import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

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

@Injectable({
  providedIn: 'root'
})
export class RelatorioService {
  private apiUrl = 'http://localhost:8080/api';

  constructor(private http: HttpClient) {}

  /**
   * Busca todos os pedidos da API
   */
  obterPedidos(): Observable<PedidoAPI[]> {
    return this.http.get<PedidoAPI[]>(`${this.apiUrl}/pedidos`);
  }

  /**
   * Calcula dados financeiros mensais a partir dos pedidos
   */
  calcularDadosFinanceiros(pedidos: PedidoAPI[]): DadosFinanceiros[] {
    const mesesMap = new Map<number, { receita: number; despesa: number }>();

    // Meses em português
    const mesesPT = ['Jan', 'Fev', 'Mar', 'Abr', 'Mai', 'Jun', 'Jul', 'Ago', 'Set', 'Out', 'Nov', 'Dez'];

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
        return acc + (item.quantidade * item.precoUnitario);
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
    const produtosMap = new Map<string, { quantidade: number; receita: number }>();

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
          produtosMap.set(nomeProduto, { quantidade: item.quantidade, receita });
        }
      });
    });

    // Converte para array e ordena por receita (descending)
    const resultado = Array.from(produtosMap.entries()).map(([nome, dados]) => ({
      nome,
      quantidade: dados.quantidade,
      receita: dados.receita,
    }));

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
        return acc + (item.quantidade * item.precoUnitario);
      }, 0);

      totalReceita += totalPedido;
      totalDespesa += totalPedido * 0.3; // Estima despesa como 30%
    });

    const totalLucro = totalReceita - totalDespesa;
    const margemLucro = totalReceita > 0 ? (totalLucro / totalReceita) * 100 : 0;
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
