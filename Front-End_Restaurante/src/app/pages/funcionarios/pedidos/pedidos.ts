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
    console.log('Carregando pedidos pendentes para funcionários...');
    this.isLoading = true;
    this.pedidoService
      .getPedidosPendentes()
      .pipe(
        finalize(() => {
          this.isLoading = false;
          this.cdr.detectChanges();
        })
      )
      .subscribe({
        next: (arr) => {
          // O backend retorna PedidoCozinhaDTO com { pedidoId, dataHora, nomeMesa, numeroMesa, itens }
          // Mapear DTOs para objetos tipados
          console.log(
            'GET /api/pedidos/pendentes -> quantidade:',
            (arr || []).length,
            arr
          );
          this.pedidos = (arr || []).map((p: any) => {
            const pedidoObj = {} as Pedido;

            const itens: ItemPedido[] = (p.itens || []).map(
              (it: any, idx: number) => {
                const produto: Produto = {
                  id: it.produtoId ?? (null as any),
                  nome: it.nomeProduto || it.nome || '',
                  descricao: it.descricao || '',
                  preco: it.preco ?? it.precoUnitario ?? it.valorUnitario ?? 0,
                  quantidadeEstoque: 0,
                  disponibilidade: null as any,
                  categoria: null as any,
                  imagemUrl: undefined,
                };

                const item: ItemPedido = {
                  id: idx + 1,
                  quantidade: it.quantidade ?? 1,
                  precoUnitario:
                    (it.precoUnitario ??
                      it.preco ??
                      it.valorUnitario ??
                      produto.preco) ||
                    0,
                  pedido: pedidoObj,
                  produto,
                } as ItemPedido;
                return item;
              }
            );

            pedidoObj.id = p.pedidoId;
            pedidoObj.dataHora = p.dataHora ? new Date(p.dataHora) : new Date();
            pedidoObj.status = (p.status ?? 'PENDENTE')
              .toString()
              .toUpperCase() as any;
            pedidoObj.comanda = {
              id: null as any,
              dataAbertura: null,
              dataFechamento: null,
              status: null as any,
              cliente: null,
              mesa: {
                id: null as any,
                numero: p.numeroMesa ?? null,
                nome: p.nomeMesa ?? null,
                status: null as any,
              } as any,
              pedidos: [],
              total: 0,
            } as Comanda;
            pedidoObj.itens = itens;
            // calcular total do pedido a partir dos itens
            pedidoObj.total = itens.reduce(
              (sum, it) => sum + (it.precoUnitario || 0) * (it.quantidade || 1),
              0
            );

            // vincula cada item ao pedido tipado
            for (const it of itens) {
              it.pedido = pedidoObj;
            }

            return pedidoObj;
          });

          // Agrupa por comanda (mesa) para exibir comandas com pedidos aninhados
          const map = new Map<string, Comanda>();
          for (const ped of this.pedidos) {
            const key = `${ped.comanda.mesa?.nome || 'Mesa'}::${
              ped.comanda.mesa?.numero ?? ''
            }`;
            if (!map.has(key)) {
              const cmd: Comanda = {
                id: null as any,
                dataAbertura: null,
                dataFechamento: null,
                status: null as any,
                cliente: null,
                mesa: ped.comanda.mesa,
                pedidos: [],
                total: 0,
              };
              map.set(key, cmd);
            }
            const cmd = map.get(key)!;
            cmd.pedidos.push(ped);
            // atualizar referência de comanda do pedido para o objeto agrupado
            ped.comanda = cmd;
          }

          this.comandas = Array.from(map.values());
        },
        error: (err) => {
          console.error('Erro ao carregar pedidos pendentes:', err);
          this.erro = 'Falha ao carregar pedidos pendentes';
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
