import { Component, OnInit, inject, ChangeDetectionStrategy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { ChangeDetectorRef } from '@angular/core';

import { formatError } from '../../../utils/formatError';

import { Pedido } from '../../../models/pedido.model';
import { ItemPedido } from '../../../models/itemPedido.model';
import { statusPedido } from '../../../enums/statusPedido';

//import { PedidoService } from '../../../services/pedido/pedido.service';


@Component({
  selector: 'app-pedidos',
  imports: [CommonModule, FormsModule],
  templateUrl: './pedidos.html',
  styleUrls: ['./pedidos.css'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class Pedidos implements OnInit {

  pedidos: Pedido[] = [];
  statusFiltro: string = 'TODOS';
  statusOptions = ['TODOS', ...Object.values(statusPedido)];
  erro: string | null = null;

  private router = inject(Router);

  ngOnInit(): void {
    // Simulação de dados de pedidos
    this.pedidos = [
      {
        id: 1,
        dataHora: new Date(),
        status: statusPedido.PENDENTE,
        comanda: {} as any,
        itens: [
          { id: 1, quantidade: 2, precoUnitario: 15.0 },
          { id: 2, quantidade: 1, precoUnitario: 5.0 } 
        ] as ItemPedido[],
        total: 35.0
      },
      {
        id: 2,
        dataHora: new Date(),
        status: statusPedido.PREPARANDO,
        comanda: {} as any,
        itens: [
          { id: 3, quantidade: 1, precoUnitario: 20.0 }
        ] as ItemPedido[],
        total: 20.0
      }
    ];
  }

  get pedidosFiltrados() {
    if (this.statusFiltro === 'TODOS') {
      return this.pedidos.sort((a, b) => b.dataHora.getTime() - a.dataHora.getTime());
    }
    return this.pedidos
      .filter(p => p.status === this.statusFiltro)
      .sort((a, b) => b.dataHora.getTime() - a.dataHora.getTime());
  }

  atualizarStatus(pedido: Pedido, novoStatus: string) {
    const allowed = ['Pendente', 'Preparando', 'Pronto', 'Entregue', 'Cancelado'] as const;
    if (novoStatus && (allowed as readonly string[]).includes(novoStatus)) {
      pedido.status = novoStatus as Pedido['status'];
      console.log(`Pedido ${pedido.id} atualizado para: ${novoStatus}`);
    }
  }

  onStatusChange(pedido: Pedido, event: Event) {
    const target = event.target as HTMLSelectElement;
    if (target?.value) {
      this.atualizarStatus(pedido, target.value);
    }
  }

  getStatusClass(status: string): string {
    const classes: { [key: string]: string } = {
      'Pendente': 'status-pendente',
      'Preparando': 'status-preparando',
      'Pronto': 'status-pronto',
      'Entregue': 'status-entregue',
      'Cancelado': 'status-cancelado'
    };
    return classes[status] || '';
  }

  getTotalPedidosHoje(): number {
    const hoje = new Date();
    hoje.setHours(0, 0, 0, 0);
    return this.pedidos.filter(p => {
      const dataPedido = new Date(p.dataHora);
      dataPedido.setHours(0, 0, 0, 0);
      return dataPedido.getTime() === hoje.getTime();
    }).length;
  }

  getReceitaHoje(): number {
    const hoje = new Date();
    hoje.setHours(0, 0, 0, 0);
    return this.pedidos
      .filter(p => {
        const dataPedido = new Date(p.dataHora);
        dataPedido.setHours(0, 0, 0, 0);
        return dataPedido.getTime() === hoje.getTime() && p.status === statusPedido.ENTREGUE;
      })
      .reduce((total, pedido) => total + pedido.total, 0);
  }
}
