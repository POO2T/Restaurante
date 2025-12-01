import { Component, OnInit, OnDestroy, ViewChild, ElementRef, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Chart, registerables } from 'chart.js';
// CERTO ✅



// Importação unificada do Service e dos DTOs
import { 
  RelatorioService, 
  DashboardResumoDTO, 
  ProdutoVendidoDTO, 
  ComandaDetalhadaDTO, 
  PedidoResponseDTO 
} from '../../../services/relatorio/relatorio.service';

import { AuthService } from '../../../services/auth/auth.service';
import { formatError } from '../../../utils/formatError';

// Registra os componentes do Chart.js (necessário para os gráficos funcionarem)
Chart.register(...registerables);
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
  imports: [CommonModule],
  selector: 'app-relatorio',
  templateUrl: './relatorio.html',
  styleUrls: ['./relatorio.css']
})
export class Relatorio {
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

  // Dados do dia (dashboard)
  dashboardResumo: DashboardResumoDTO | null = null;

  // Produtos fornecidos pelo backend (top vendidos)
  produtosVendidosBackend: ProdutoVendidoDTO[] = [];

  // Histórico do cliente (quando o usuário for CLIENTE)
  meuHistoricoComandas: ComandaDetalhadaDTO [] | null = null;
  meusPedidosHojeCount: number = 0;
  // Chart.js instance
  private hourlyChart: Chart | null = null;
  @ViewChild('hourlyCanvas', { static: false }) hourlyCanvas?: ElementRef<HTMLCanvasElement>;

  private auth = inject(AuthService);

  constructor(private relatorioService: RelatorioService) {}

  ngOnInit(): void {
    console.log('🚀 Relatório component iniciado');
    // Verifica token antes de tentar carregar dados
    try {
      const token = this.auth.getToken();
      console.log('🔐 Token presente?', !!token);
      if (!token) {
        this.erro = 'Você não está autenticado. Faça login para acessar os relatórios.';
        this.carregando = false;
        return;
      }
    } catch (e) {
      console.warn('Não foi possível ler token de autenticação:', e);
    }

    this.carregarDados();
  }

  // Destrói o chart se existir
  private destroyHourlyChart() {
    try {
      if (this.hourlyChart) {
        this.hourlyChart.destroy();
        this.hourlyChart = null;
      }
    } catch (e) {
      console.warn('Erro ao destruir chart antigo:', e);
    }
  }

  ngOnDestroy(): void {
    this.destroyHourlyChart();
  }

  /**
   * Agrupa os pedidos pelo horário (0-23) do dia atual e retorna labels e valores.
   */
  private aggregateSalesByHour(pedidos: any): { labels: string[]; values: number[] } {
    // Inicializa 24 horas com zero
    const values = new Array<number>(24).fill(0);

    const hoje = new Date();
    const anoHoje = hoje.getFullYear();
    const mesHoje = hoje.getMonth();
    const diaHoje = hoje.getDate();

    pedidos.forEach((pedido: any) => {
      try {
        const dh = pedido.dataHora || pedido.dataAbertura || pedido.dataHoraPedido || pedido.data;
        if (!dh) return;
        const d = new Date(dh);
        if (d.getFullYear() !== anoHoje || d.getMonth() !== mesHoje || d.getDate() !== diaHoje) return;

        const hour = d.getHours();

        // extrair valor do pedido defensivamente
        let valor = 0;
        if (typeof pedido.valorTotal === 'number') valor = pedido.valorTotal;
        else if (typeof pedido.totalPedido === 'number') valor = pedido.totalPedido;
        else if (pedido.itens && Array.isArray(pedido.itens)) {
          valor = pedido.itens.reduce((acc: number, it: any) => {
            const q = Number(it.quantidade) || 0;
            const p = Number(it.precoUnitario ?? it.preco ?? 0) || 0;
            return acc + q * p;
          }, 0);
        }

        values[hour] += valor;
      } catch (e) {
        console.warn('Erro ao processar pedido para agregação por hora:', e, pedido);
      }
    });

    const labels = values.map((_, i) => `${String(i).padStart(2, '0')}:00`);
    return { labels, values };
  }

  private renderHourlyChart(labels: string[], values: number[]) {
    // Registra componentes do Chart.js uma vez
    try {
      Chart.register(...registerables);
    } catch (e) {
      // já registrado possivelmente
    }

    this.destroyHourlyChart();

    const canvasEl = this.hourlyCanvas?.nativeElement;
    if (!canvasEl) {
      // Tenta novamente pouco depois se o elemento ainda não existir no DOM
      setTimeout(() => this.renderHourlyChart(labels, values), 100);
      return;
    }

    const ctx = canvasEl.getContext('2d');
    if (!ctx) {
      console.warn('Canvas context não disponível para hourlyChart');
      return;
    }

    this.hourlyChart = new Chart(ctx, {
      type: 'line',
      data: {
        labels,
        datasets: [
          {
            label: 'Vendas (R$) por Hora',
            data: values,
            backgroundColor: 'rgba(54, 162, 235, 0.15)',
            borderColor: 'rgba(54, 162, 235, 1)',
            borderWidth: 2,
            tension: 0.3,
            fill: true,
            pointRadius: 3,
            pointHoverRadius: 5,
            pointBackgroundColor: 'rgba(54, 162, 235, 1)'
          }
        ]
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        scales: {
          y: {
            beginAtZero: true,
            ticks: {
              callback: function(value: any) {
                return 'R$ ' + Number(value).toFixed(2);
              }
            }
          }
        },
        plugins: {
          tooltip: {
            callbacks: {
              label: function(context: any) {
                const v = context.parsed.y ?? context.parsed;
                return 'R$ ' + Number(v).toFixed(2);
              }
            }
          }
        }
      }
    });
  }

  carregarDados(): void {
    console.log('📥 Iniciando carregamento de dados...');
    this.carregando = true;
    this.erro = null;

    // 1) Obter resumo do dia (dashboard)
    this.relatorioService.obterDashboard().subscribe({
      next: (dash) => {
        console.log('✅ Dashboard recebido:', dash);
        this.dashboardResumo = dash;
      },
      error: (err) => {
        console.warn('⚠️ Falha ao obter dashboard:', err?.status ?? err);
        // Não interrompe o carregamento dos demais dados — apenas loga/mostra aviso se for auth
        if (err?.status === 401 || err?.status === 403) {
          this.erro = 'Acesso negado ao dashboard: verifique permissões (GERENTE/ADMIN).';
          this.carregando = false;
        }
      }
    });

    // 2) Obter top produtos já calculados pelo backend
    this.relatorioService.obterTopProdutos().subscribe({
      next: (top) => {
        console.log('✅ Top produtos recebido (backend):', top);
        this.produtosVendidosBackend = top;
      },
      error: (err) => {
        console.warn('⚠️ Falha ao obter top produtos do backend:', err?.status ?? err);
      }
    });

    // 3) Mantemos também a chamada aos pedidos para cálculos mensais/relatórios existentes
    const subscription = this.relatorioService.obterPedidos().subscribe({
      next: (pedidos: any) => {
        console.log('✅ Response recebido do servidor');
        try {
          console.log('📦 Pedidos recebidos:', pedidos);
          
          if (!pedidos || pedidos.length === 0) {
            console.warn('⚠️ Nenhum pedido encontrado');
            this.totalPedidosRealizados = 0;
            this.totalProdutosVendidos = 0;
            this.carregando = false;
            return;
          }

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

          // Gerar gráfico de vendas por hora para o dia atual
          try {
            const { labels, values } = this.aggregateSalesByHour(pedidos);
            // Aguarda a atualização do DOM (caso o canvas esteja dentro de *ngIf)
            setTimeout(() => this.renderHourlyChart(labels, values), 50);
          } catch (e) {
            console.warn('Erro ao gerar gráfico de vendas por hora:', e);
          }
        } catch (e: any) {
          this.erro = `Erro ao processar dados: ${e.message || 'Desconhecido'}`;
          console.error('❌ Erro ao processar pedidos:', e);
        } finally {
          this.carregando = false;
        }
      },
      error: (error: any) => {
        console.error('❌ Erro HTTP ao carregar pedidos:', error);
        console.error('Status:', error.status);
        console.error('Mensagem:', error.message);
        
        // Verifica o tipo de erro
        if (error.status === 0) {
          this.erro = 'Erro de conexão: Verifique se o backend está rodando em http://localhost:8080';
        } else if (error.status === 404) {
          this.erro = 'Endpoint não encontrado: /api/pedidos não existe';
        } else if (error.status === 401 || error.status === 403) {
          this.erro = 'Acesso negado: Verifique suas permissões';
        } else if (error.name === 'TimeoutError') {
          this.erro = 'Timeout: O backend demorou muito para responder (>10s). Verifique a conexão.';
        } else {
          this.erro = formatError(error) || 'Erro desconhecido ao carregar pedidos';
        }
        
        this.carregando = false;
      },
      complete: () => {
        console.log('✅ Observable completado');
      }
    });

    // Timeout adicional de segurança (12 segundos)
    setTimeout(() => {
      if (this.carregando) {
        console.error('⏱️ Timeout: Dados não carregaram em 12 segundos');
        this.erro = 'Timeout: O servidor não respondeu em tempo. Verifique se o backend está rodando.';
        this.carregando = false;
        subscription?.unsubscribe();
      }
    }, 12000);

    // 4) Se o usuário for cliente, buscar seu histórico de comandas/pedidos
    try {
      if (this.auth.isCliente && this.auth.isCliente()) {
        this.relatorioService.obterHistoricoCliente().subscribe({
          next: (hist) => {
            console.log('✅ Histórico do cliente recebido:', hist);
            this.meuHistoricoComandas = hist || [];
            // Conta quantos pedidos do cliente ocorreram hoje
            const hoje = new Date();
            const inicio = new Date(hoje.getFullYear(), hoje.getMonth(), hoje.getDate(), 0, 0, 0);
            const fim = new Date(hoje.getFullYear(), hoje.getMonth(), hoje.getDate(), 23, 59, 59);
            let count = 0;
            this.meuHistoricoComandas.forEach((comanda) => {
              (comanda.pedidos || []).forEach((pedido) => {
                const d = new Date(pedido.dataHora);
                if (d >= inicio && d <= fim) count++;
              });
            });
            this.meusPedidosHojeCount = count;
          },
          error: (err) => {
            console.warn('⚠️ Falha ao obter histórico do cliente:', err?.status ?? err);
          }
        });
      }
    } catch (e) {
      console.warn('Erro ao checar tipo de usuário cliente:', e);
    }
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
