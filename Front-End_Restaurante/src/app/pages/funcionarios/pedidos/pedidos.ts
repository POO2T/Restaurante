import {
  Component,
  OnInit,
  inject,
  ChangeDetectionStrategy,
} from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { ChangeDetectorRef } from '@angular/core';
import { PedidoService } from '../../../services/pedido/pedido.service';
import { finalize } from 'rxjs/operators';

import { formatError } from '../../../utils/formatError';

import { Pedido } from '../../../models/pedido.model';
import { ItemPedido } from '../../../models/itemPedido.model';
import { Comanda } from '../../../models/comanda.model';
import { Produto } from '../../../models/produto.model';
import { statusComanda } from '../../../enums/statusComanda';
import { statusPedido } from '../../../enums/statusPedido';

//import { PedidoService } from '../../../services/pedido/pedido.service';

@Component({
  selector: 'app-pedidos',
  imports: [CommonModule, FormsModule],
  templateUrl: './pedidos.html',
  styleUrls: ['./pedidos.css'],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class Pedidos implements OnInit {
  pedidos: Pedido[] = [];
  comandas: Comanda[] = [];
  statusFiltro: string = 'TODOS';
  statusOptions = ['TODOS', ...Object.values(statusPedido)];
  erro: string | null = null;

  private router = inject(Router);
  private pedidoService = inject(PedidoService);
  private cdr = inject(ChangeDetectorRef);

  isLoading: boolean = false;

  ngOnInit(): void {
    this.loadPedidosPendentes();
  }

  filtrarComandasPorStatus(valor: string) {
    this.statusFiltro = valor || 'TODOS';
    this.cdr.detectChanges();
  }

  // Retorna comandas com pedidos filtrados pelo status selecionado
  get comandasFiltradas(): Comanda[] {
    if (!this.comandas || this.comandas.length === 0) return [];
    if (!this.statusFiltro || this.statusFiltro === 'TODOS')
      return this.comandas;
    const filtro = this.statusFiltro.toString().toUpperCase();
    return this.comandas
      .map((c) => ({
        ...c,
        pedidos: (c.pedidos || []).filter(
          (p) => (p.status || '').toString().toUpperCase() === filtro
        ),
      }))
      .filter((c) => (c.pedidos || []).length > 0);
  }

  private loadPedidosPendentes(): void {
    this.erro = null;
    console.log(
      'Carregando comandas abertas (detalhadas) para funcionários...'
    );
    this.isLoading = true;
    this.pedidoService
      .getComandasAbertas()
      .pipe(
        finalize(() => {
          this.isLoading = false;
          this.cdr.detectChanges();
        })
      )
      .subscribe({
        next: (arr) => {
          // Backend retorna ComandaDetalhadaDTO com pedidos, pagamentos e totais
          console.log(
            'GET /api/comandas/abertas -> quantidade:',
            (arr || []).length,
            arr
          );
          this.comandas = (arr || []).map((c: any) => {
            const comanda: Comanda = {
              id: c.id ?? (null as any),
              dataAbertura: c.dataAbertura ? new Date(c.dataAbertura) : null,
              dataFechamento: c.dataFechamento
                ? new Date(c.dataFechamento)
                : null,
              status: (c.status ?? null) as any,
              cliente: null,
              mesa: {
                id: null as any,
                numero: c.numeroMesa ?? null,
                nome: c.nomeMesa ?? null,
                status: null as any,
              } as any,
              pedidos: [],
              total: (c.totalCalculado ?? c.total ?? 0) as number,
            } as Comanda;

            comanda.pedidos = (c.pedidos || []).map((p: any) => {
              const pedido: Pedido = {
                id: p.id ?? (null as any),
                dataHora: p.dataHora ? new Date(p.dataHora) : new Date(),
                status: (p.status ?? 'PENDENTE')
                  .toString()
                  .toUpperCase() as any,
                comanda,
                itens: (p.itens || []).map((it: any, idx: number) => {
                  const produto: Produto = {
                    id: it.id ?? (null as any),
                    nome: it.nomeProduto || it.nome || '',
                    descricao: it.descricao || '',
                    preco: it.precoUnitario ?? it.preco ?? 0,
                    quantidadeEstoque: 0,
                    disponibilidade: null as any,
                    categoria: null as any,
                    imagemUrl: undefined,
                  };
                  const item: ItemPedido = {
                    id: it.id ?? idx + 1,
                    quantidade: it.quantidade ?? 1,
                    precoUnitario:
                      it.precoUnitario ?? it.preco ?? produto.preco ?? 0,
                    pedido: null as any,
                    produto,
                  } as ItemPedido;
                  return item;
                }),
                total: p.totalPedido ?? p.total ?? 0,
              } as Pedido;
              // vincular pedido na referência dos itens
              for (const it of pedido.itens) {
                it.pedido = pedido;
              }
              return pedido;
            });

            return comanda;
          });
        },
        error: (err) => {
          console.error('Erro ao carregar comandas abertas:', err);
          this.erro = 'Falha ao carregar comandas abertas';
        },
      });
  }

  get pedidosFiltrados() {
    if (this.statusFiltro === 'TODOS') {
      return this.pedidos.sort(
        (a, b) => b.dataHora.getTime() - a.dataHora.getTime()
      );
    }
    return this.pedidos
      .filter((p) => p.status === this.statusFiltro)
      .sort((a, b) => b.dataHora.getTime() - a.dataHora.getTime());
  }

  atualizarStatus(pedido: Pedido, novoStatus: string) {
    const allowed = [
      'PENDENTE',
      'PREPARANDO',
      'PRONTO',
      'ENTREGUE',
      'CANCELADO',
    ] as const;
    if (
      novoStatus &&
      (allowed as readonly string[]).includes(novoStatus.toUpperCase())
    ) {
      pedido.status = novoStatus.toUpperCase() as Pedido['status'];
      console.log(`Pedido ${pedido.id} atualizado para: ${novoStatus}`);
      this.cdr.detectChanges();
    }
  }

  onStatusChange(pedido: Pedido, event: Event) {
    const target = event.target as HTMLSelectElement;
    if (target?.value) {
      this.atualizarStatus(pedido, target.value);
    }
  }

  // Atualiza status da comanda (aplica localmente)
  atualizarStatusComanda(comanda: Comanda, novoStatus: string) {
    const allowed = Object.values(statusComanda).map((s) => String(s));
    if (novoStatus && allowed.includes(novoStatus)) {
      comanda.status = novoStatus as any;
      console.log(
        `Comanda da mesa ${comanda.mesa?.numero} atualizada para: ${novoStatus}`
      );
      this.cdr.detectChanges();
    }
  }

  onComandaStatusChange(comanda: Comanda, event: Event) {
    const target = event.target as HTMLSelectElement;
    if (target?.value) {
      this.atualizarStatusComanda(comanda, target.value);
    }
  }

  getStatusClass(status: string): string {
    const classes: { [key: string]: string } = {
      PENDENTE: 'status-pendente',
      PREPARANDO: 'status-preparando',
      PRONTO: 'status-pronto',
      ENTREGUE: 'status-entregue',
      CANCELADO: 'status-cancelado',
    };
    const key = (status ?? '').toString().toUpperCase();
    return classes[key] || '';
  }

  getTotalPedidosHoje(): number {
    const hoje = new Date();
    hoje.setHours(0, 0, 0, 0);
    return this.pedidos.filter((p) => {
      const dataPedido = new Date(p.dataHora);
      dataPedido.setHours(0, 0, 0, 0);
      return dataPedido.getTime() === hoje.getTime();
    }).length;
  }

  getReceitaHoje(): number {
    const hoje = new Date();
    hoje.setHours(0, 0, 0, 0);
    return this.pedidos
      .filter((p) => {
        const dataPedido = new Date(p.dataHora);
        dataPedido.setHours(0, 0, 0, 0);
        return (
          dataPedido.getTime() === hoje.getTime() &&
          p.status === statusPedido.ENTREGUE
        );
      })
      .reduce((total, pedido) => total + pedido.total, 0);
  }
}
