import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';

import { Categoria } from '../../../models/categoria.model';
import { Produto } from '../../../models/produto.model';

import { CategoriaService } from '../../../services/categoria/categoria.service';
import { ProdutoService } from '../../../services/produto/produto.service';

@Component({
  selector: 'app-produtos',
  imports: [ReactiveFormsModule, CommonModule],
  templateUrl: './produtos.html',
  styleUrls: ['./produtos.css']
})
export class Produtos {
  categorias: Categoria[] = [];
  produtos: Produto[] = [];

  produtoForm: FormGroup;
  categoriaForm: FormGroup;
  
  private fb = inject(FormBuilder);
  private categoriaService = inject(CategoriaService); // Serviço para gerenciar categorias
  private produtoService = inject(ProdutoService); // Serviço para gerenciar produtos

  constructor() {
    this.categoriaForm = this.fb.group({
      nome: ['', Validators.required],
    });

    this.produtoForm = this.fb.group({
      nome: ['', Validators.required],
      preco: ['', [Validators.required, Validators.min(0)]],
      categoriaId: ['', Validators.required]
    });
  }

  addCategoria() {

  }
  editarCategoria(categoria: Categoria) {}
  deletarCategoria(categoria: Categoria) {}

  addProduto() {}
  editarProduto(produto: Produto) {}
  deletarProduto(produto: Produto) {}
}
