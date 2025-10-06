import { Component, OnInit } from '@angular/core';

interface Pedido {

  id: string;
  dataAbertura: string;
  status: string;

}

@Component({
  selector: 'app-pedidos',
  imports: [],
  templateUrl: './pedidos.html',
  styleUrl: './pedidos.css'
})

export class Pedidos implements OnInit {

  pedidos: Pedido[] = [];

  constructor() { }

  ngOnInit(): void {
    this.pedidos = [

      { id: '1', dataAbertura: '2024-06-01', status: 'Em andamento' },
      { id: '2', dataAbertura: '2024-06-02', status: 'Concluído' },
      { id: '3', dataAbertura: '2024-06-03', status: 'Cancelado' }

    ]
  }

}
