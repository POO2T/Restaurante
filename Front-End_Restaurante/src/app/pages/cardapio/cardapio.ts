import {
  Component,
  OnInit,
  ChangeDetectionStrategy,
  inject,
  ChangeDetectorRef,
} from '@angular/core';
import { CommonModule } from '@angular/common';
import {
  ReactiveFormsModule,
  FormBuilder,
  FormGroup,
  Validators,
} from '@angular/forms';
import { Router } from '@angular/router';

import { formatError } from '../../utils/formatError';

import { Produto } from '../../models/produto.model';
import { Categoria } from '../../models/categoria.model';
import { ItemPedido } from '../../models/itemPedido.model';
import { Pedido, PedidoRequest } from '../../models/pedido.model';
import { Comanda, ComandaAberturaRequest } from '../../models/comanda.model';
import { Mesa } from '../../models/mesa.model';
import { Cliente } from '../../models/cliente.model';

import { FormaPagamento } from '../../enums/formaPagamento';
import { statusComanda } from '../../enums/statusComanda';

import { CategoriaService } from '../../services/categoria/categoria.service';
import { ProdutoService } from '../../services/produto/produto.service';
import { PedidoService } from '../../services/pedido/pedido.service';
import { MesasService } from '../../services/mesas/mesas.service';
import { AuthService } from '../../services/auth/auth.service';
import { ComandaService } from '../../services/comanda/comanda.service';
import { StorageService } from '../../services/storage.service';

import { switchMap, tap } from 'rxjs/operators';

@Component({
  selector: 'app-cardapio',
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './cardapio.html',
  styleUrls: ['./cardapio.css'],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class Cardapio {
  private categoriaService = inject(CategoriaService);
  private comandaService = inject(ComandaService);
  private produtoService = inject(ProdutoService);
  private pedidoService = inject(PedidoService);
  private mesasService = inject(MesasService);
  private authService = inject(AuthService);
  private storageService = inject(StorageService);
  private router = inject(Router);
  private fb = inject(FormBuilder);

  // Detector de mudanças para atualizar a UI após carregamento assíncrono
  private cdr = inject(ChangeDetectorRef);

  comandaForm: FormGroup;

  isLoading: boolean = false;
  mostrarComanda: boolean = false;
  isOverlayVisible: boolean = false;

  // MODELOS
  produtos: Produto[] = [];
  categorias: Categoria[] = [];
  carrinho: ItemPedido[] = [];
  mesas: Mesa[] = [];

  // ENUMS
  formasPagamento: FormaPagamento[] = Object.values(FormaPagamento);
  statusComanda = statusComanda;

  categoriaSelecionada: Categoria | 'Todos' = 'Todos';

  constructor() {
    this.comandaForm = this.fb.group({
      mesa: ['', Validators.required],
      pagamento: ['', Validators.required],
    });
  }

  ngOnInit(): void {
    this.loadData();
  }

  get produtosFiltrados() {
    // Se a categoria selecionada for o objeto 'Todos' (id === null) ou a string 'Todos', retorna todos
    const selected = this.categoriaSelecionada as any;
    if (
      this.categoriaSelecionada === 'Todos' ||
      (selected && selected.id == null && selected.nome === 'Todos')
    ) {
      return this.produtos;
    }

    // Comparar por ID quando possível (produto.categoria pode ser objeto ou string)
    const selectedIsCategoria = this.categoriaSelecionada as Categoria;
    return this.produtos.filter((p) => {
      const prodCat = (p as any).categoria;
      // prodCat pode ser: { id, nome } ou apenas nome (string) ou id
      const prodCatId =
        prodCat && typeof prodCat === 'object' ? prodCat.id : prodCat;
      if (
        prodCatId != null &&
        selectedIsCategoria &&
        (selectedIsCategoria as any).id != null
      ) {
        return prodCatId === (selectedIsCategoria as any).id;
      }
      // fallback para comparar por nome/valor
      const prodCatName =
        prodCat && typeof prodCat === 'object' ? prodCat.nome : prodCat;
      return (
        prodCatName === (selectedIsCategoria as any).nome ||
        prodCatName === this.categoriaSelecionada
      );
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
      itemExistente.precoUnitario = produto.preco * itemExistente.quantidade;
    } else {
      this.carrinho.push({
        produto,
        quantidade: 1,
        pedido: {} as Pedido,
        precoUnitario: produto.preco,
      } as ItemPedido);
    }

    console.log('Produto adicionado ao carrinho:', produto.nome);
    console.log('Carrinho atual:', this.carrinho);
    this.isLoading = false;
    this.cdr.markForCheck();
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
      alert(
        'O carrinho está vazio. Adicione itens antes de finalizar o pedido.'
      );
      return;
    }

    this.loadMesas();

    this.toggleComanda();
    this.toggleOverlay();
  }

  onSubmitComanda(): void {
    if (this.comandaForm.invalid) {
      alert('Formulário inválido. Preencha os campos obrigatórios.');
      return;
    }

    const v = this.comandaForm.value;

    // Normalizar valor da mesa: o controle pode fornecer o objeto Mesa ou apenas o id
    const mesaControl = v.mesa;
    const mesaId =
      mesaControl && typeof mesaControl === 'object'
        ? mesaControl.id
        : mesaControl;
    if (!mesaId) {
      alert('Selecione uma mesa válida antes de prosseguir.');
      return;
    }

    const comandaRequest: ComandaAberturaRequest = {
      mesaId: mesaId,
    };

    const pedidoRequest: PedidoRequest = {
      itens: this.carrinho.map((item) => ({
        produtoId: item.produto.id,
        quantidade: item.quantidade,
      })),
    };

    const cliente =
      this.authService.currentUser() && this.authService.isCliente()
        ? (this.authService.currentUser() as Cliente)
        : null;

    // Log útil para debug antes da requisição
    console.debug('Comanda request payload:', comandaRequest);
    console.debug('Pedido request payload:', pedidoRequest);

    // Escolhe o observable de criação de comanda conforme autenticação
    const comanda$ = cliente
      ? this.comandaService.postComandaAutenticada(comandaRequest)
      : this.comandaService.postComandaVisitante(comandaRequest);

    // Para ambos os casos (visitante ou autenticado) encadeamos criação da comanda -> criação do pedido
    if (!cliente) {
      if (!confirm('Você não está logado. Deseja continuar como convidado?'))
        return;
    }

    this.isLoading = true;

    // atualizar flags de loading apenas quando a solicitação completar
    comanda$
      .pipe(
        tap((comandaCriada) => {
          // Se for visitante, salva o id da comanda no localStorage para a página /pedido
          if (!cliente) {
            try {
              // Salva apenas o ID para uso rápido na página /pedido
              if (comandaCriada && comandaCriada.id != null) {
                this.storageService.setItem(
                  'last_comanda_id',
                  String(comandaCriada.id)
                );
              }
              // Salva também a comanda completa (serializada) para inspeção/diagnóstico
              try {
                this.storageService.setItem(
                  'last_comanda',
                  JSON.stringify(comandaCriada)
                );
              } catch (e) {
                // não crítico
              }

              console.debug(
                'Salvo last_comanda_id no storage:',
                comandaCriada?.id
              );
            } catch (e) {
              console.warn(
                'Não foi possível salvar last_comanda_id no storage',
                e
              );
            }
          }
        }),
        switchMap((comandaCriada) =>
          this.pedidoService.postPedido(comandaCriada, pedidoRequest)
        )
      )
      .subscribe({
        next: (pedidoCriado) => {
          alert('Pedido realizado com sucesso!');
          this.carrinho = [];
          this.closeOverlay();
          this.comandaForm.reset();

          this.router.navigate(['/pedido']);
          this.isLoading = false;
          this.cdr.markForCheck();
        },
        error: (err) => {
          alert(formatError(err, 'Erro ao processar pedido/comanda.'));
          console.error('Erro no fluxo comanda->pedido:', err);
          this.isLoading = false;
          this.cdr.markForCheck();
        },
      });
  }

  loadData(): void {
    // Carregar categorias do serviço
    this.isLoading = true;

    this.categoriaService.getCategorias().subscribe({
      next: (categorias) => {
        console.debug('Categorias recebidas:', categorias);
        // Normaliza categorias: garante que cada categoria seja um objeto {id,nome,produtos}
        const normalized = (categorias || []).map((c: any) => ({
          ...c,
          produtos: c.produtos ?? [],
        }));

        // Adiciona um objeto "Todos" ao final para seleção
        const todosObj = { id: null, nome: 'Todos', produtos: [] } as any;
        this.categorias = [...normalized, todosObj];
        this.categoriaSelecionada = todosObj; // Seleciona "Todos" por padrão

        this.cdr.markForCheck();

        // Agora carrega produtos e anexa às categorias
        this.produtoService.getProdutos().subscribe({
          next: (produtos) => {
            console.debug('Produtos recebidos:', produtos);
            this.produtos = produtos || [];

            // Anexa cada produto à categoria correspondente por id quando possível
            for (const produto of this.produtos) {
              const catId = produto?.categoria?.id ?? produto?.categoria;
              if (catId == null) continue;
              const catIndex = this.categorias.findIndex((c) => c.id === catId);
              if (catIndex !== -1) {
                const cat = this.categorias[catIndex];
                const novos = [...(cat.produtos ?? []), produto];
                this.categorias[catIndex] = {
                  ...cat,
                  produtos: novos,
                } as Categoria;
              }
            }

            // Preenche o objeto 'Todos' (último item) com todos os produtos
            const todosIndex = this.categorias.findIndex(
              (c) => c && (c as any).nome === 'Todos'
            );
            if (todosIndex !== -1) {
              const todosCat = this.categorias[todosIndex];
              this.categorias[todosIndex] = {
                ...todosCat,
                produtos: [...this.produtos],
              } as Categoria;
            }

            this.cdr.markForCheck();
            this.isLoading = false;
            this.cdr.markForCheck();
          },
          error: (error) => {
            alert(formatError(error));
            console.error('Erro ao carregar produtos:', formatError(error));
            this.isLoading = false;
            this.cdr.markForCheck();
          },
        });
      },
      error: (error) => {
        alert(formatError(error));
        console.error('Erro ao carregar categorias:', formatError(error));
        this.isLoading = false;
        this.cdr.markForCheck();
      },
    });

    // this.isLoading = false;
    // this.cdr.markForCheck();
  }

  loadMesas(): void {
    this.isLoading = true;

    this.mesasService.getMesas().subscribe({
      next: (mesas) => {
        this.mesas = mesas;
        this.isLoading = false;
        this.cdr.markForCheck();
      },
      error: (error) => {
        alert(formatError(error));
        console.error('Erro ao carregar mesas:', formatError(error));
        this.isLoading = false;
        this.cdr.markForCheck();
      },
    });
  }

  toggleComanda() {
    this.mostrarComanda = !this.mostrarComanda;
  }
  toggleOverlay() {
    this.isOverlayVisible = !this.isOverlayVisible;
  }
  closeOverlay() {
    this.isOverlayVisible = false;
    this.mostrarComanda = false;
  }
}
