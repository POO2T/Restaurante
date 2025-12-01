import {
  Component,
  OnInit,
  OnDestroy,
  ViewChild,
  ElementRef,
  inject,
} from '@angular/core';
import { CommonModule } from '@angular/common';
import { Chart, registerables } from 'chart.js';
// CERTO ✅

// Importação unificada do Service e dos DTOs
import {
  RelatorioService,
  DashboardResumoDTO,
  ProdutoVendidoDTO,
  ComandaDetalhadaDTO,
  PedidoResponseDTO,
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
  styleUrls: ['./relatorio.css'],
})
export class Relatorio {
  // Dados financeiros mensal
  dadosFinanceiros: DadosFinanceiros[] = [];
  pedidosHoje: PedidoResponseDTO[] = [];
  dadosFinanceirosMensais: DadosFinanceiros[] = [];

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
  meuHistoricoComandas: ComandaDetalhadaDTO[] | null = null;
  meusPedidosHojeCount: number = 0;
  // Chart.js instance
  private hourlyChart: Chart | null = null;
  @ViewChild('hourlyCanvas', { static: false })
  hourlyCanvas?: ElementRef<HTMLCanvasElement>;

  private auth = inject(AuthService);

  constructor(private relatorioService: RelatorioService) {}

  ngOnInit(): void {
    console.log('🚀 Relatório component iniciado');
    // Verifica token antes de tentar carregar dados
    try {
      const token = this.auth.getToken();
      console.log('🔐 Token presente?', !!token);
      if (!token) {
        this.erro =
          'Você não está autenticado. Faça login para acessar os relatórios.';
        this.carregando = false;
        return;
      }
    } catch (e) {
      console.warn('Não foi possível ler token de autenticação:', e);
    }

    this.carregarDados();
  }

  // Destrói o chart se existirb
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
  private aggregateSalesByHour(pedidos: any): {
    labels: string[];
    values: number[];
  } {
    // Inicializa 24 horas com zero
    const values = new Array<number>(24).fill(0);

    const hoje = new Date();
    const anoHoje = hoje.getFullYear();
    const mesHoje = hoje.getMonth();
    const diaHoje = hoje.getDate();

    pedidos.forEach((pedido: any) => {
      try {
        const dh =
          pedido.dataHora ||
          pedido.dataAbertura ||
          pedido.dataHoraPedido ||
          pedido.data;
        if (!dh) return;
        const d = new Date(dh);
        if (
          d.getFullYear() !== anoHoje ||
          d.getMonth() !== mesHoje ||
          d.getDate() !== diaHoje
        )
          return;

        const hour = d.getHours();

        // extrair valor do pedido defensivamente
        let valor = 0;
        if (typeof pedido.valorTotal === 'number') valor = pedido.valorTotal;
        else if (typeof pedido.totalPedido === 'number')
          valor = pedido.totalPedido;
        else if (pedido.itens && Array.isArray(pedido.itens)) {
          valor = pedido.itens.reduce((acc: number, it: any) => {
            const q = Number(it.quantidade) || 0;
            const p = Number(it.precoUnitario ?? it.preco ?? 0) || 0;
            return acc + q * p;
          }, 0);
        }

        values[hour] += valor;
      } catch (e) {
        console.warn(
          'Erro ao processar pedido para agregação por hora:',
          e,
          pedido
        );
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
            pointBackgroundColor: 'rgba(54, 162, 235, 1)',
          },
        ],
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        scales: {
          y: {
            beginAtZero: true,
            ticks: {
              callback: function (value: any) {
                return 'R$ ' + Number(value).toFixed(2);
              },
            },
          },
        },
        plugins: {
          tooltip: {
            callbacks: {
              label: function (context: any) {
                const v = context.parsed.y ?? context.parsed;
                return 'R$ ' + Number(v).toFixed(2);
              },
            },
          },
        },
      },
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
        // Preenche resumoGeral com o que o backend fornece (valor diário)
        this.resumoGeral = {
          totalReceita: dash.faturamentoHoje || 0,
          totalDespesa: 0,
          totalLucro: dash.faturamentoHoje || 0,
          margemLucro: 0,
          ticketMedio: dash.totalPedidosHoje
            ? (dash.faturamentoHoje || 0) / dash.totalPedidosHoje
            : 0,
        };
        this.totalPedidosRealizados = dash.totalPedidosHoje || 0;
        this.carregando = false;
      },
      error: (err) => {
        console.warn('⚠️ Falha ao obter dashboard:', err?.status ?? err);
        if (err?.status === 401 || err?.status === 403) {
          this.erro =
            'Acesso negado ao dashboard: verifique permissões (GERENTE/ADMIN).';
        } else {
          this.erro = 'Erro ao carregar dashboard.';
        }
        this.carregando = false;
      },
    });

    // 2) Obter top produtos já calculados pelo backend
    this.relatorioService.obterTopProdutos().subscribe({
      next: (top) => {
        console.log('✅ Top produtos recebido (backend):', top);
        this.produtosVendidosBackend = top;
      },
      error: (err) => {
        console.warn(
          '⚠️ Falha ao obter top produtos do backend:',
          err?.status ?? err
        );
      },
    });

    // Nota: o endpoint '/comandas/detalhes' não existe no backend atual.
    // Para relatórios financeiros mensais, o backend deve expor um endpoint específico.
    // Mantemos métodos de agregação local caso um endpoint futuro retorne pedidos/comandas.

    // Timeout adicional de segurança (12 segundos)
    setTimeout(() => {
      if (this.carregando) {
        console.error('⏱️ Timeout: Dados não carregaram em 12 segundos');
        this.erro =
          'Timeout: O servidor não respondeu em tempo. Verifique se o backend está rodando.';
        this.carregando = false;
      }
    }, 12000);
  }

  // Calcular percentual de crescimento
  obterCrescimento(
    mesAnterior: DadosFinanceiros,
    mesAtual: DadosFinanceiros
  ): number {
    if (mesAnterior.receita === 0) return 0;
    return (
      ((mesAtual.receita - mesAnterior.receita) / mesAnterior.receita) * 100
    );
  }

  // Recarregar dados
  recarregar(): void {
    this.carregarDados();
  }
}
