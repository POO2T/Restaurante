import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RelatorioService } from '../../services/relatorio/relatorio.service';
import { formatError } from '../../utils/formatError';

interface DadosFinanceiros {
  mes: string;
  receita: number;
  despesa: number;
  lucro: number;
}

interface ProdutoTop {
  nome: string;
  quantidade: number;
  receita: number;
}

@Component({
  selector: 'app-relatorio',
  imports: [CommonModule],
  templateUrl: './relatorio.html',
  styleUrl: './relatorio.css'
})
export class Relatorio implements OnInit {
  // Dados financeiros mensal
  dadosFinanceiros: DadosFinanceiros[] = [];

  // Produtos mais vendidos
  produtosTop: ProdutoTop[] = [];

  // Resumo financeiro geral
  resumoGeral = {
    totalReceita: 0,
    totalDespesa: 0,
    totalLucro: 0,
    margemLucro: 0,
    ticketMedio: 0,
  };

  totalProdutosVendidos: number = 0;
  totalPedidosRealizados: number = 0;
  carregando: boolean = true;
  erro: string | null = null;

  constructor(private relatorioService: RelatorioService) {}

  ngOnInit(): void {
    this.carregarDados();
  }

  carregarDados(): void {
    this.carregando = true;
    this.erro = null;

    this.relatorioService.obterPedidos().subscribe({
      next: (pedidos: any) => {
        try {
          // Total de pedidos realizados
          this.totalPedidosRealizados = pedidos.length;

          // Calcula dados financeiros
          this.dadosFinanceiros = this.relatorioService.calcularDadosFinanceiros(pedidos);

          // Calcula top produtos
          this.produtosTop = this.relatorioService.calcularTopProdutos(pedidos);

          // Calcula resumo geral
          const resumo = this.relatorioService.calcularResumoGeral(pedidos);
          this.resumoGeral = {
            totalReceita: resumo.totalReceita,
            totalDespesa: resumo.totalDespesa,
            totalLucro: resumo.totalLucro,
            margemLucro: resumo.margemLucro,
            ticketMedio: resumo.ticketMedio,
          };

          this.totalProdutosVendidos = resumo.totalItens;

          console.log('✅ Dados carregados com sucesso:', {
            totalPedidos: this.totalPedidosRealizados,
            receita: resumo.totalReceita,
            lucro: resumo.totalLucro,
            itens: resumo.totalItens,
          });
        } catch (e) {
          this.erro = 'Erro ao processar dados dos pedidos';
          console.error('Erro ao processar pedidos:', e);
        } finally {
          this.carregando = false;
        }
      },
      error: (error: any) => {
        this.erro = formatError(error);
        this.carregando = false;
        console.error('Erro ao carregar pedidos:', error);
      },
    });
  }

  // Calcular percentual de crescimento
  obterCrescimento(mesAnterior: DadosFinanceiros, mesAtual: DadosFinanceiros): number {
    if (mesAnterior.receita === 0) return 0;
    return ((mesAtual.receita - mesAnterior.receita) / mesAnterior.receita) * 100;
  }

  // Recarregar dados
  recarregar(): void {
    this.carregarDados();
  }
}
