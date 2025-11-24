import { Component, inject, ChangeDetectionStrategy, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';

import { Categoria } from '../../../models/categoria.model';
import { Produto } from '../../../models/produto.model';
import { CategoriaService } from '../../../services/categoria/categoria.service';
import { ProdutoService } from '../../../services/produto/produto.service';
import { AuthService } from '../../../services/auth/auth.service';
import { formatError  } from '../../../utils/formatError';

@Component({
  selector: 'app-produtos',
  imports: [ReactiveFormsModule, CommonModule],
  templateUrl: './produtos.html',
  styleUrls: ['./produtos.css'],
})
export class Produtos {
  categorias: Categoria[] = [];
  produtos: Produto[] = [];

  erro: string = '';
  loading: boolean = false;
  editandoCategoria: boolean = false;

  produtoForm: FormGroup;
  categoriaForm: FormGroup;
  categoriaEditForm: FormGroup;

  // INJEÇÕES
  private cdr: ChangeDetectorRef = inject(ChangeDetectorRef);
  private fb: FormBuilder = inject(FormBuilder);
  private categoriaService: CategoriaService = inject(CategoriaService); // Serviço para gerenciar categorias
  private produtoService: ProdutoService = inject(ProdutoService); // Serviço para gerenciar produtos
  private authService: AuthService = inject(AuthService);

  constructor() {
    this.categoriaForm = this.fb.group({
      nome: ['', Validators.required],
    });

    this.categoriaEditForm = this.fb.group({
      id: ['', /* Adicionado controle para ID */],
      nome: ['', [Validators.required, Validators.minLength(3)]],
    });

    this.produtoForm = this.fb.group({
      nome: ['', Validators.required],
      preco: ['', [Validators.required, Validators.min(0)]],
      categoria: ['', Validators.required],
    });
  }

  ngOnInit() {
    this.loadData();
  }

  onAddCategoria() {
    if (this.categoriaForm.invalid) {
      return;
    }

    const novaCategoria: Partial<Categoria> = {
      nome: this.categoriaForm.value.nome,
    };

    this.categoriaService.postCategoria(novaCategoria).subscribe({
      next: (categoria) => {
        this.categorias.push(categoria);
        this.categoriaForm.reset();
      },
      error: (error) => {
        this.erro = formatError(error, 'Erro ao adicionar categoria');
        console.error('Erro ao adicionar categoria:', this.erro);
        alert(this.erro);
      },
    });
  }

  editarCategoria(categoria: Categoria) {
    // Preencher o formulário com os valores da categoria antes de abrir o modal
    this.categoriaEditForm.setValue({
      id: categoria.id,
      nome: categoria.nome,
    });

    // Abrir o modal de edição
    this.toggleEditCategoria();
  }
  onEditCategoriaSubmit() {
    if (this.categoriaEditForm.invalid) {
      return;
    }

    this.loading = true;

    const categoriaAtualizada: Partial<Categoria> = {
      id: this.categoriaEditForm.value.id,
      nome: this.categoriaEditForm.value.nome,
    };

    this.categoriaService.putCategoria(categoriaAtualizada).subscribe({
      next: (categoria) => {
        const index = this.categorias.findIndex((c) => c.id === categoria.id);
        if (index !== -1) {
          this.categorias[index] = categoria;
        }

        this.loading = false;
        this.categoriaEditForm.reset();
        this.cdr.markForCheck();
      },
      error: (error) => {
        this.erro = formatError(error, 'Erro ao atualizar categoria');
        console.error('Erro ao atualizar categoria:', this.erro);
        alert(this.erro);
      },
    });

    this.closeEditCategoria();
  }

  deletarCategoria(categoria: Categoria) {
    if (confirm(`Confirma a exclusão da categoria "${categoria.nome}"? Esta ação não pode ser desfeita.`)) {
      
      const categoriaSelecionada: Partial<Categoria> = categoria;
      this.loading = true;

      this.categoriaService.deleteCategoria(categoriaSelecionada).subscribe({
        next: () => {
          this.categorias = this.categorias.filter((c) => c.id !== categoria.id);
          this.loading = false;
          this.cdr.markForCheck();
        },
        error: (error) => {
          this.erro = formatError(error, 'Erro ao deletar categoria');
          console.error('Erro ao deletar categoria:', this.erro);
          alert(this.erro);
          
          this.loading = false;
          this.cdr.markForCheck();
        },
      });
    }
  }

  addProduto(categoria: Categoria) {}
  editarProduto(produto: Produto) {}
  deletarProduto(produto: Produto) {}

  toggleEditCategoria() {
    this.editandoCategoria = !this.editandoCategoria;
  }
  closeEditCategoria() {
    this.editandoCategoria = false;
  }

  loadData() {
    this.loading = true;
    this.categoriaService.getCategorias().subscribe({
      next: (categorias) => {
        this.categorias = categorias;
        this.loading = false;
        this.cdr.markForCheck();
      },
      error: (error) => {
        this.erro = formatError(error, 'Erro ao carregar categorias');
        console.error('Erro ao carregar categorias:', this.erro);
        alert(this.erro);
        this.loading = false;
        this.cdr.markForCheck();
      },
    });
  }
}