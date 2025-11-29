import { Component, OnInit, ChangeDetectionStrategy, inject, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { formatError } from '../../utils/formatError';

import { Produto } from '../../models/produto.model';
import { Categoria } from '../../models/categoria.model';
import { CategoriaService } from '../../services/categoria/categoria.service';
import { ProdutoService } from '../../services/produto/produto.service';


@Component({
  selector: 'app-cardapio',
  imports: [CommonModule, FormsModule],
  templateUrl: './cardapio.html',
  styleUrls: ['./cardapio.css'],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class Cardapio {
  private categoriaService = inject(CategoriaService);
  private produtoService = inject(ProdutoService);
  private cdr = inject(ChangeDetectorRef);

  produtos: Produto[] = [];
  categorias: Categoria[] = [];
  categoriaSelecionada: Categoria | 'Todos' = 'Todos';
  carrinho: { produto: Produto; quantidade: number }[] = [];


  ngOnInit(): void {
    this.loadData();
  }

  get produtosFiltrados() {
    if (this.categoriaSelecionada === 'Todos') {
      return this.produtos;
    }

    // Comparar por ID quando possível (produto.categoria pode ser objeto ou string)
    const selectedIsCategoria = this.categoriaSelecionada as Categoria;
    return this.produtos.filter((p) => {
      const prodCat = (p as any).categoria;
      // prodCat pode ser: { id, nome } ou apenas nome (string) ou id
      const prodCatId = prodCat && typeof prodCat === 'object' ? prodCat.id : prodCat;
      if (prodCatId != null && selectedIsCategoria && (selectedIsCategoria as any).id != null) {
        return prodCatId === (selectedIsCategoria as any).id;
      }
      // fallback para comparar por nome/valor
      const prodCatName = prodCat && typeof prodCat === 'object' ? prodCat.nome : prodCat;
      return prodCatName === (selectedIsCategoria as any).nome || prodCatName === this.categoriaSelecionada;
    });
  }

  filtrarPorCategoria(categoria: Categoria | 'Todos') {
    this.categoriaSelecionada = categoria;
  }

  adicionarAoCarrinho(produto: Produto) {
    const itemExistente = this.carrinho.find(
      (item) => item.produto.id === produto.id
    );

    if (itemExistente) {
      itemExistente.quantidade++;
    } else {
      this.carrinho.push({ produto, quantidade: 1 });
    }

    console.log('Produto adicionado ao carrinho:', produto.nome);
    console.log('Carrinho atual:', this.carrinho);
  }

  get totalCarrinho() {
    return this.carrinho.reduce(
      (total, item) => total + item.produto.preco * item.quantidade,
      0
    );
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
    alert(
      `Pedido finalizado com sucesso! Total: R$ ${this.totalCarrinho.toFixed(
        2
      )}`
    );
    console.log('Pedido finalizado:', {
      itens: this.carrinho,
      total: this.totalCarrinho,
      dataHora: new Date(),
    });

    // Limpa o carrinho
    this.carrinho = [];
  }

  loadData(): void {
    // Carregar categorias do serviço
    this.categoriaService.getCategorias().subscribe({
      next: (categorias) => {
        this.categorias = categorias;
        // this.categoriaSelecionada = this.categorias[0]; // Seleciona "Todos" por padrão

        this.produtoService.getProdutos().subscribe({
          next: (produtos) => {
            this.produtos = produtos;
            this.cdr.markForCheck();
          },
          error: (error) => {
            alert(formatError(error));
            console.error('Erro ao carregar produtos:', formatError(error));
          },
        });
      },
      error: (error) => {
        alert(formatError(error));
        console.error('Erro ao carregar categorias:', formatError(error));
      },
    });
  }
}