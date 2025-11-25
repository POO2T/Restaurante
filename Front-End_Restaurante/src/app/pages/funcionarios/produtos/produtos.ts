import { Component, inject, ChangeDetectionStrategy, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';

import { Categoria } from '../../../models/categoria.model';
import { Produto, ProdutoRequest } from '../../../models/produto.model';
import { statusProduto } from '../../../enums/statusProduto';
import { CategoriaService } from '../../../services/categoria/categoria.service';
import { ProdutoService } from '../../../services/produto/produto.service';
import { formatError  } from '../../../utils/formatError';

// import { AuthService } from '../../../services/auth/auth.service';

@Component({
  selector: 'app-produtos',
  imports: [ReactiveFormsModule, CommonModule],
  changeDetection: ChangeDetectionStrategy.OnPush,
  templateUrl: './produtos.html',
  styleUrls: ['./produtos.css'],
})
export class Produtos {
  categorias: Categoria[] = [];
  produtos: Produto[] = [];
  statusProduto = Object.values(statusProduto);
  produtoSelecionado: Produto | null = null;

  erro: string = '';
  loading: boolean = false;

  isOverlayVisible: boolean = false;

  adicionandoProduto: boolean = false;
  editandoProduto: boolean = false;
  editandoCategoria: boolean = false;

  categoriaForm: FormGroup;
  categoriaEditForm: FormGroup;
  produtoForm: FormGroup;
  produtoEditForm: FormGroup;

  // INJEÇÕES
  private cdr: ChangeDetectorRef = inject(ChangeDetectorRef);
  private fb: FormBuilder = inject(FormBuilder);
  private categoriaService: CategoriaService = inject(CategoriaService); // Serviço para gerenciar categorias
  private produtoService: ProdutoService = inject(ProdutoService); // Serviço para gerenciar produtos
  // private authService: AuthService = inject(AuthService);

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
      disponibilidade: ['', Validators.required],
      quantidadeEstoque: ['', [Validators.required, Validators.min(0)]],
      descricao: [''],
    });

    this.produtoEditForm = this.fb.group({
      nome: ['', [Validators.required, Validators.minLength(3)]],
      preco: ['', [Validators.required, Validators.min(0)]],
      quantidadeEstoque: ['', [Validators.required, Validators.min(0)]],
      disponibilidade: ['', Validators.required],
      descricao: [''],
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
    this.isOverlayVisible = true;
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

  addProduto(categoria: Categoria) {
    this.adicionandoProduto = true;
    // patch the numeric categoria id (backend expects categoriaId)
    this.produtoForm.patchValue({ categoria: categoria.id });

    this.isOverlayVisible = true;
  }
  onAddProdutoSubmit(): void {
    if (this.produtoForm.invalid) {
      return;
    }
    
    this.loading = true;

    // Construir payload conforme ProdutoRequestDTO do backend (usa categoriaId)
    const produtoRequestDTO: ProdutoRequest = {
      nome: this.produtoForm.value.nome,
      preco: this.produtoForm.value.preco,
      quantidadeEstoque: this.produtoForm.value.quantidadeEstoque,
      disponibilidade: this.produtoForm.value.disponibilidade,
      descricao: this.produtoForm.value.descricao,
      categoriaId: this.produtoForm.value.categoria,
    };

    console.debug('Creating produto payload:', produtoRequestDTO);

    this.produtoService.postProduto(produtoRequestDTO).subscribe({
      next: (produto) => {
        // atualizar de forma imutável para respeitar OnPush
        this.produtos = [...this.produtos, produto];
        this.produtoForm.reset();

        this.categorias = this.categorias.map((categoria) => {
          if (categoria.id === produto.categoria.id) {
            const novos = [...(categoria.produtos ?? []), produto];
            return { ...categoria, produtos: novos } as Categoria;
          }
          return categoria;
        });

        this.loading = false;
        this.cdr.markForCheck();
      },
      error: (error) => {
        this.erro = formatError(error, 'Erro ao adicionar produto');
        console.error('Erro ao adicionar produto:', this.erro);
        alert(this.erro);
        
        this.cdr.markForCheck();
        this.loading = false;
      },
    });

    this.adicionandoProduto = false;
    this.closeOverlay();
  }


  editarProduto(produto: Produto) {
    // Preencher o formulário com os valores do produto antes de abrir o modal

    this.produtoSelecionado = produto;

    this.produtoEditForm.setValue({
      nome: produto.nome,
      preco: produto.preco,
      quantidadeEstoque: produto.quantidadeEstoque,
      disponibilidade: produto.disponibilidade,
      descricao: produto.descricao,
    });
    this.toggleEditProduto();
    this.toggleOverlay();
  }
  onEditProdutoSubmit() {
    if (this.produtoEditForm.invalid) {
      return;
    }
    if (!this.produtoSelecionado) {
      this.erro = 'Nenhum produto selecionado para edição.';
      console.error(this.erro);
      alert(this.erro);
      return;
    }

    this.loading = true;

    const produtoAtualizado: Partial<ProdutoRequest> = {
      nome: this.produtoEditForm.value.nome,
      preco: this.produtoEditForm.value.preco,
      quantidadeEstoque: this.produtoEditForm.value.quantidadeEstoque,
      disponibilidade: this.produtoEditForm.value.disponibilidade,
      descricao: this.produtoEditForm.value.descricao,
      categoriaId: this.produtoSelecionado!.categoria.id,
    };

    this.produtoService.putProduto(this.produtoSelecionado!.id, produtoAtualizado).subscribe({
      next: (produto) => {
        // atualizar lista de forma imutável para disparar OnPush
        this.produtos = this.produtos.map((p) => (p.id === produto.id ? produto : p));
        this.loading = false;
        this.cdr.markForCheck();
      },
      error: (error) => {
        this.erro = formatError(error, 'Erro ao atualizar produto');
        console.error('Erro ao atualizar produto:', this.erro);
        alert(this.erro);

        this.loading = false;
        this.cdr.markForCheck();
      },
    });

    this.produtoSelecionado = null;
    this.closeEditProduto();
    this.closeOverlay();
  }

  deletarProduto(produto: Produto) {
    if (confirm(`Confirma a exclusão do produto "${produto.nome}"? Esta ação não pode ser desfeita.`)) {
      
      const produtoSelecionado: Partial<Produto> = produto;
      this.loading = true;
      
      this.produtoService.deleteProduto(produtoSelecionado).subscribe({
        next: () => {
          this.produtos = this.produtos.filter((p) => p.id !== produto.id);
          this.cdr.markForCheck();
        },
        error: (error) => {
          this.erro = formatError(error, 'Erro ao deletar produto');
          console.error('Erro ao deletar produto:', this.erro);
          alert(this.erro);
        },
      });

      this.loading = false;
      this.cdr.markForCheck();
      this.closeOverlay();
    }
  }

  toggleEditCategoria() {
    this.editandoCategoria = !this.editandoCategoria;
  }
  closeEditCategoria() {
    this.editandoCategoria = false;
  }
  toggleEditProduto() {
    this.editandoProduto = !this.editandoProduto;
  }
  closeEditProduto() {
    this.editandoProduto = false;
  }

  toggleOverlay() {
    this.isOverlayVisible = !this.isOverlayVisible;
  }
  closeOverlay() {
    this.isOverlayVisible = false;
    this.editandoCategoria = false;
    this.adicionandoProduto = false;
    this.editandoProduto = false;
  }

  loadData(): void {
    this.loading = true;
    // First load categories, then load products so we can attach products to the categories
    this.categoriaService.getCategorias().subscribe({
      next: (categorias) => {
        // Backend returns CategoriaDTO (id, nome) without produtos list.
        // Initialize an empty produtos array so the UI can render and we can push newly created produtos.
        this.categorias = categorias.map((c: Categoria) => ({ ...c, produtos: c.produtos ?? [] }));

        // Now load products and attach them to the corresponding category objects
        this.produtoService.getProdutos().subscribe({
          next: (produtos) => {
            this.produtos = produtos;

            for (const produto of produtos) {
              const catId = produto?.categoria?.id;
              if (catId == null) continue;
              const cat = this.categorias.find((c) => c.id === catId);
              if (cat) {
                cat.produtos = cat.produtos ?? [];
                cat.produtos.push(produto);
              }
            }

            this.loading = false;
            this.cdr.markForCheck();
          },
          error: (error) => {
            this.erro = formatError(error, 'Erro ao carregar produtos');
            console.error('Erro ao carregar produtos:', this.erro);
            this.loading = false;
            this.cdr.markForCheck();
          },
        });
      },
      error: (error) => {
        this.erro = formatError(error, 'Erro ao carregar categorias');
        console.error('Erro ao carregar categorias:', this.erro);
        this.loading = false;
        this.cdr.markForCheck();
      },
    });
  }
}