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
export class Cardapio implements OnInit {
  private categoriaService = inject(CategoriaService);
  private produtoService = inject(ProdutoService);
  private cdr = inject(ChangeDetectorRef);

  produtos: Produto[] = [];
  categorias: Categoria[] = [];
  categoriaSelecionada: Categoria | 'Todos' = 'Todos';
  produtosFiltrados: Produto[] = [];
  carrinho: { produto: Produto; quantidade: number }[] = [];


  ngOnInit(): void {
    this.loadData();
  }

  atualizarProdutosFiltrados(): void {
    if (this.categoriaSelecionada === 'Todos') {
      this.produtosFiltrados = this.produtos;
      console.debug('Filtro aplicado: Todos', { totalProdutos: this.produtosFiltrados.length });
    } else {
      const selected: any = this.categoriaSelecionada;
      const selectedId = selected?.id;
      const selectedNome = selected?.nome || selected;

      console.debug('Filtrando por categoria:', { selectedId, selectedNome });

      this.produtosFiltrados = this.produtos.filter((p) => {
        const prodCat: any = (p as any).categoria;
        
        const prodCatId = prodCat?.id ?? (typeof prodCat === 'number' ? prodCat : null);
        const prodCatNome = prodCat?.nome ?? (typeof prodCat === 'string' ? prodCat : null);

        // Log para cada produto e sua categoria
        console.debug(`Produto "${p.nome}":`, {
          prodCatId,
          prodCatNome,
          selectedId,
          selectedNome,
          match: false,
        });

        if (selectedId != null && prodCatId != null) {
          const match = prodCatId === selectedId;
          if (match) console.debug(`  ✓ Match por ID: ${prodCatId} === ${selectedId}`);
          return match;
        }

        if (selectedNome && (prodCatNome || selectedNome)) {
          const match = (prodCatNome || '').toLowerCase().trim() === selectedNome.toLowerCase().trim();
          if (match) console.debug(`  ✓ Match por Nome: "${prodCatNome}" === "${selectedNome}"`);
          return match;
        }

        return false;
      });

      console.debug('Resultado do filtro:', { 
        totalFiltrado: this.produtosFiltrados.length,
        produtosFiltrados: this.produtosFiltrados.map(p => ({ id: p.id, nome: p.nome }))
      });
    }
    this.cdr.markForCheck();
  }

  filtrarPorCategoria(categoria: Categoria | 'Todos') {
    this.categoriaSelecionada = categoria;
    this.atualizarProdutosFiltrados();
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

  // Retorna a URL da imagem considerando possíveis formatos retornados pela API
  imageFor(produto: Produto): string {
    const p: any = produto as any;
    // possíveis propriedades usadas pela API
    const candidates = [p.imagemUrl, p.imagem, p.imagem_url, p.imagemURL, p.imagem_base64, p.imageUrl, p.image];
    for (const c of candidates) {
      if (!c) continue;
      // se for base64
      if (typeof c === 'string' && c.length > 100 && /^[A-Za-z0-9+/=\s]+$/.test(c.trim())) {
        const result = `data:image/png;base64,${c.trim()}`;
        console.debug(`imageFor("${p.nome}"): base64 encontrado`);
        return result;
      }
      console.debug(`imageFor("${p.nome}"): candidato URL: ${c}`);
      return c;
    }
    // fallback para slug
    const name = (p.nome || produto.nome || '').toString();
    const slug = name
      .normalize('NFD')
      .replace(/\p{Diacritic}/gu, '')
      .toLowerCase()
      .replace(/[^a-z0-9]+/g, '-')
      .replace(/(^-|-$)/g, '');
    if (slug) {
      const result = `/assets/imgs/${slug}.png`;
      console.debug(`imageFor("${p.nome}"): usando slug fallback: ${result}`);
      return result;
    }
    console.debug(`imageFor("${p.nome}"): usando placeholder SVG`);
    return 'data:image/svg+xml;utf8,<svg xmlns="http://www.w3.org/2000/svg" width="200" height="120"><rect width="100%" height="100%" fill="%23f3f3f3"/><text x="50%" y="50%" dominant-baseline="middle" text-anchor="middle" fill="%23999" font-family="Arial,Helvetica" font-size="14">Sem imagem</text></svg>';
  }

  // Normaliza diferentes formatos de disponibilidade retornados pela API
  isDisponivel(produto: Produto): boolean {
    const p: any = produto as any;
    if (typeof p.disponivel === 'boolean') return p.disponivel;
    if (typeof p.disponibilidade === 'boolean') return p.disponibilidade;
    if (typeof p.disponibilidade === 'string') return p.disponibilidade.toUpperCase() === 'DISPONIVEL' || p.disponibilidade.toUpperCase() === 'DISPONÍVEL';
    if (typeof p.disponivel === 'string') return p.disponivel.toUpperCase() === 'DISPONIVEL' || p.disponivel.toUpperCase() === 'DISPONÍVEL';
    return !!p.disponibilidade || !!p.disponivel;
  }

  onImageError(event: Event) {
    const img = event.target as HTMLImageElement;
    if (!img) return;
    img.onerror = null; // evitar loop

    // tentar extensões alternativas se houver slug no data attribute
    const slug = img.dataset['slug'];
    if (slug) {
      const attemptsKey = `img-attempts-${slug}`;
      const attempts = (this._imageAttempts.get(slug) || 0);
      const exts = ['png', 'jpg', 'jpeg', 'webp', 'svg'];
      if (attempts < exts.length - 1) {
        const next = attempts + 1;
        this._imageAttempts.set(slug, next);
        img.src = `./svg/produtos/${slug}.${exts[next]}`;
        return;
      }
    }

    img.src = 'data:image/svg+xml;utf8,<svg xmlns="http://www.w3.org/2000/svg" width="200" height="120"><rect width="100%" height="100%" fill="%23f3f3f3"/><text x="50%" y="50%" dominant-baseline="middle" text-anchor="middle" fill="%23999" font-family="Arial,Helvetica" font-size="14">Sem imagem</text></svg>';
  }

  private _imageAttempts = new Map<string, number>();

  slugFor(produto: Produto): string {
    const p: any = produto as any;
    const name = (p.nome || produto.nome || '').toString();
    const slug = name
      .normalize('NFD')
      .replace(/\p{Diacritic}/gu, '')
      .toLowerCase()
      .replace(/[^a-z0-9]+/g, '-')
      .replace(/(^-|-$)/g, '');
    return slug;
  }

  trackByProdutoId(index: number, produto: Produto): any {
    // use id when available, otherwise use a stable slug derived from the name, fallback to index
    const id = (produto as any).id;
    if (id !== undefined && id !== null && id !== 0) return id;
    const slug = this.slugFor(produto);
    if (slug) return `slug:${slug}`;
    return index;
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
            this.atualizarProdutosFiltrados();
            // debug: log lista de produtos com slug e imagem candidata
            try {
              const debugList = produtos.map((p: any) => ({
                id: p.id,
                nome: p.nome,
                slug: this.slugFor(p),
                imageCandidate: this.imageFor(p),
              }));
              console.debug('Cardapio - produtos debug:', debugList);
            } catch (e) {
              console.debug('Cardapio - erro ao gerar debug produtos', e);
            }
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