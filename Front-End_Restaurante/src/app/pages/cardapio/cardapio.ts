import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';

interface Produto {
  id: number;
  nome: string;
  descricao: string;
  preco: number;
  categoria: string;
  imagemUrl: string;
  disponivel: boolean;
}

@Component({
  selector: 'app-cardapio',
  imports: [CommonModule, FormsModule],
  templateUrl: './cardapio.html',
  styleUrl: './cardapio.css'
})
export class Cardapio implements OnInit {

  produtos: Produto[] = [];
  categorias: string[] = ['Todos', 'Massas', 'Peixes', 'Carnes', 'Sobremesas', 'Bebidas'];
  categoriaSelecionada: string = 'Todos';
  carrinho: { produto: Produto, quantidade: number }[] = [];

  constructor(private router: Router) { }

  ngOnInit(): void {
    this.produtos = [
      {
        id: 1,
        nome: 'Lasanha à Bolonhesa',
        descricao: 'Uma obra-prima da culinária italiana, feita com camadas de massa fresca, molho bolonhesa caseiro e queijos selecionados.',
        preco: 45.90,
        categoria: 'Massas',
        imagemUrl: '/assets/imgs/lasanha.png',
        disponivel: true
      },
      {
        id: 2,
        nome: 'Salmão Grelhado ao Limone',
        descricao: 'Um filé de salmão fresco, perfeitamente grelhado com crosta crocante por fora e suculento por dentro, acompanhado de molho de limão siciliano.',
        preco: 68.50,
        categoria: 'Peixes',
        imagemUrl: '/assets/imgs/salmao.png',
        disponivel: true
      },
      {
        id: 3,
        nome: 'Risotto de Camarão',
        descricao: 'Arroz arbóreo cremoso com camarões frescos, finalizado com parmesão e ervas finas.',
        preco: 52.90,
        categoria: 'Massas',
        imagemUrl: '/assets/imgs/lasanha.png',
        disponivel: true
      },
      {
        id: 4,
        nome: 'Filé Mignon ao Molho Madeira',
        descricao: 'Filé mignon grelhado ao ponto, servido com molho madeira e acompanhamentos da casa.',
        preco: 75.90,
        categoria: 'Carnes',
        imagemUrl: '/assets/imgs/salmao.png',
        disponivel: false
      },
      {
        id: 5,
        nome: 'Tiramisu Clássico',
        descricao: 'A sobremesa italiana mais famosa do mundo, com mascarpone, café expresso e cacau.',
        preco: 18.90,
        categoria: 'Sobremesas',
        imagemUrl: '/assets/imgs/lasanha.png',
        disponivel: true
      },
      {
        id: 6,
        nome: 'Vinho Tinto Reserva',
        descricao: 'Vinho tinto encorpado, ideal para acompanhar carnes e massas.',
        preco: 89.90,
        categoria: 'Bebidas',
        imagemUrl: '/assets/imgs/salmao.png',
        disponivel: true
      }
    ];
  }

  get produtosFiltrados() {
    if (this.categoriaSelecionada === 'Todos') {
      return this.produtos;
    }
    return this.produtos.filter(p => p.categoria === this.categoriaSelecionada);
  }

  filtrarPorCategoria(categoria: string) {
    this.categoriaSelecionada = categoria;
  }

  adicionarAoCarrinho(produto: Produto) {
    const itemExistente = this.carrinho.find(item => item.produto.id === produto.id);
    
    if (itemExistente) {
      itemExistente.quantidade++;
    } else {
      this.carrinho.push({ produto, quantidade: 1 });
    }
    
    console.log('Produto adicionado ao carrinho:', produto.nome);
    console.log('Carrinho atual:', this.carrinho);
  }

  get totalCarrinho() {
    return this.carrinho.reduce((total, item) => total + (item.produto.preco * item.quantidade), 0);
  }

  get quantidadeItensCarrinho() {
    return this.carrinho.reduce((total, item) => total + item.quantidade, 0);
  }

  finalizarPedido() {
    if (this.carrinho.length === 0) {
      alert('Adicione itens ao carrinho antes de finalizar o pedido!');
      return;
    }

    // Simula finalização do pedido
    alert(`Pedido finalizado com sucesso! Total: R$ ${this.totalCarrinho.toFixed(2)}`);
    console.log('Pedido finalizado:', {
      itens: this.carrinho,
      total: this.totalCarrinho,
      dataHora: new Date()
    });
    
    // Limpa o carrinho
    this.carrinho = [];
  }

  voltarInicio() {
    this.router.navigate(['/']);
  }
}