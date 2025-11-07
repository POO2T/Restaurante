import { Component, OnInit, inject, ChangeDetectionStrategy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';

interface ItemPedido {
  id: number;
  nome: string;
  quantidade: number;
  preco: number;
}

interface Pedido {
  id: string;
  cliente: string;
  dataHora: Date;
  status: 'Pendente' | 'Preparando' | 'Pronto' | 'Entregue' | 'Cancelado';
  itens: ItemPedido[];
  total: number;
  observacoes?: string;
}

@Component({
  selector: 'app-pedidos',
  imports: [CommonModule, FormsModule],
  templateUrl: './pedidos.html',
  styleUrls: ['./pedidos.css'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class Pedidos implements OnInit {

  pedidos: Pedido[] = [];
  statusFiltro: string = 'Todos';
  statusOptions = ['Todos', 'Pendente', 'Preparando', 'Pronto', 'Entregue', 'Cancelado'];

  private router = inject(Router);

  ngOnInit(): void {
    this.pedidos = [
      {
        id: 'PED001',
        cliente: 'João Silva',
        dataHora: new Date('2024-10-06T12:30:00'),
        status: 'Pendente',
        itens: [
          { id: 1, nome: 'Lasanha à Bolonhesa', quantidade: 1, preco: 45.90 },
          { id: 5, nome: 'Tiramisu Clássico', quantidade: 2, preco: 18.90 }
        ],
        total: 83.70,
        observacoes: 'Sem cebola na lasanha'
      },
      {
        id: 'PED002',
        cliente: 'Maria Santos',
        dataHora: new Date('2024-10-06T13:15:00'),
        status: 'Preparando',
        itens: [
          { id: 2, nome: 'Salmão Grelhado ao Limone', quantidade: 1, preco: 68.50 },
          { id: 6, nome: 'Vinho Tinto Reserva', quantidade: 1, preco: 89.90 }
        ],
        total: 158.40
      },
      {
        id: 'PED003',
        cliente: 'Carlos Oliveira',
        dataHora: new Date('2024-10-06T11:45:00'),
        status: 'Pronto',
        itens: [
          { id: 3, nome: 'Risotto de Camarão', quantidade: 2, preco: 52.90 }
        ],
        total: 105.80,
        observacoes: 'Para viagem'
      },
      {
        id: 'PED004',
        cliente: 'Ana Costa',
        dataHora: new Date('2024-10-06T10:20:00'),
        status: 'Entregue',
        itens: [
          { id: 1, nome: 'Lasanha à Bolonhesa', quantidade: 1, preco: 45.90 },
          { id: 2, nome: 'Salmão Grelhado ao Limone', quantidade: 1, preco: 68.50 },
          { id: 5, nome: 'Tiramisu Clássico', quantidade: 1, preco: 18.90 }
        ],
        total: 133.30
      }
    ];
  }

  get pedidosFiltrados() {
    if (this.statusFiltro === 'Todos') {
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
        return dataPedido.getTime() === hoje.getTime() && p.status === 'Entregue';
      })
      .reduce((total, pedido) => total + pedido.total, 0);
  }
}
