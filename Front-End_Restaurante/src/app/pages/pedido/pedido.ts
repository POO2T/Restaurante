import { ChangeDetectorRef, Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';

import { Comanda } from '../../models/comanda.model';

import { AuthService } from '../../services/auth/auth.service';
import { ComandaService } from '../../services/comanda/comanda.service';
import { StorageService } from '../../services/storage.service';

@Component({
  selector: 'app-pedido',
  imports: [CommonModule],
  templateUrl: './pedido.html',
  styleUrls: ['./pedido.css'],
})
export class Pedido {
  private comandaService = inject(ComandaService);
  private storageService = inject(StorageService);
  public authService = inject(AuthService);
  private cdr = inject(ChangeDetectorRef);
  private router = inject(Router);

  isLoading = false;
  historico: Comanda[] = [];
  comandaSelecionada: Comanda | null = null;

  constructor() {}

  ngOnInit(): void {
    if (this.authService.isCliente()) {
      this.loadHistoricoCliente();
    } else {
      console.debug('Usuário visitante, carregando comanda visitante');
      this.loadComandaVisitante();
    }
  }

  private loadHistoricoCliente(): void {
    console.debug('Carregando histórico de comandas do cliente');

    this.isLoading = true;
    this.comandaService.getHistoricoCliente().subscribe({
      next: (hist) => {
        this.historico = (hist || []).sort((a, b) => {
          const ad = a.dataAbertura ? new Date(a.dataAbertura).getTime() : 0;
          const bd = b.dataAbertura ? new Date(b.dataAbertura).getTime() : 0;
          return bd - ad;
        });
        // Não selecionar automaticamente a primeira comanda; seleção ocorre
        // apenas quando o usuário clicar em "Ver detalhes".
        this.isLoading = false;
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Erro ao carregar histórico do cliente:', err);
        this.isLoading = false;
        this.cdr.detectChanges();
      },
    });
  }

  private loadComandaVisitante(): void {
    console.debug('Carregando comanda visitante do storage');

    // Primeiro tenta usar o ID salvo (mais confiável) e obter detalhes do backend
    try {
      const lastIdStr = this.storageService.getItem('last_comanda_id');
      if (lastIdStr) {
        const idNum = Number(lastIdStr);
        if (!Number.isNaN(idNum)) {
          this.isLoading = true;
          console.debug(
            'Tentando buscar detalhes da comanda visitante via API (id=',
            idNum,
            ')'
          );
          this.comandaService.getDetalhesComanda(idNum).subscribe({
            next: (det) => {
              // Preenche o histórico do visitante, mas não seleciona automaticamente.
              this.historico = [det];
              this.isLoading = false;
              this.cdr.detectChanges();
            },
            error: (err) => {
              console.warn(
                'Falha ao buscar detalhes da comanda via API, fazendo fallback para storage JSON',
                err
              );
              this.isLoading = false;
              this.cdr.detectChanges();
              // fallback para o JSON salvo (menos completo)
              this.loadComandaVisitanteFromJsonFallback();
            },
          });
          return; // já disparou a requisição
        } else {
          console.warn('last_comanda_id inválido no storage:', lastIdStr);
        }
      } else {
        console.debug('Nenhum last_comanda_id encontrado no storage');
      }
    } catch (e) {
      console.warn('Erro lendo last_comanda_id do storage', e);
    }

    // Se não houver ID ou a busca falhar, tenta recuperar o JSON serializado salvo como fallback
    this.loadComandaVisitanteFromJsonFallback();
  }

  private loadComandaVisitanteFromJsonFallback(): void {
    try {
      const lastComandaStr = this.storageService.getItem('last_comanda');
      if (!lastComandaStr) {
        console.debug(
          'Nenhuma comanda visitante encontrada no storage (fallback)'
        );
        this.comandaSelecionada = null;
        return;
      }
      console.debug(
        'Comanda visitante encontrada no storage (fallback):',
        lastComandaStr
      );
      const comandaObj = JSON.parse(lastComandaStr) as Comanda;
      // O JSON salvo pode não conter pedidos/detalhes — exibimos o que houver
      this.historico = [comandaObj];
      // Não selecionar automaticamente; o usuário deve tocar em "Ver detalhes".
      this.isLoading = false;
      this.cdr.detectChanges();
    } catch (e) {
      console.warn(
        'Erro ao acessar storage para comanda visitante (fallback)',
        e
      );
      this.comandaSelecionada = null;
      this.isLoading = false;
      this.cdr.detectChanges();
    }
  }

  verDetalhes(comanda: Comanda): void {
    // Clientes autenticados podem ver qualquer comanda do histórico
    if (this.authService.isCliente()) {
      this.isLoading = true;
      this.comandaService.getDetalhesComanda(comanda.id).subscribe({
        next: (det) => {
          this.comandaSelecionada = det;
          this.isLoading = false;
          this.cdr.detectChanges();
        },
        error: (err) => {
          console.error('Erro ao obter detalhes da comanda:', err);
          this.isLoading = false;
          this.cdr.detectChanges();
        },
      });
      return;
    }

    // Visitantes só podem ver a própria comanda (last_comanda_id)
    const lastIdStr = this.storageService.getItem('last_comanda_id');
    if (!lastIdStr) {
      console.warn('Visitante sem comanda registrada no storage');
      return;
    }
    const idNum = Number(lastIdStr);
    if (Number.isNaN(idNum) || idNum !== comanda.id) {
      console.warn('Tentativa de ver comanda não pertencente ao visitante');
      return;
    }

    this.isLoading = true;
    this.comandaService.getDetalhesComanda(idNum).subscribe({
      next: (det) => {
        this.comandaSelecionada = det;
        this.isLoading = false;
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.warn('Erro ao obter detalhes da comanda visitante via API:', err);
        // Tenta fallback local: se o JSON salvo existir, exibe-o como detalhes.
        try {
          const lastComandaStr = this.storageService.getItem('last_comanda');
          if (lastComandaStr) {
            const comandaObj = JSON.parse(lastComandaStr) as Comanda;
            // Apenas usa o fallback se o ID bater
            if (comandaObj && comandaObj.id === idNum) {
              this.comandaSelecionada = comandaObj;
            }
          }
        } catch (ex) {
          console.warn('Falha no fallback local da comanda visitante:', ex);
        }
        this.isLoading = false;
        this.cdr.detectChanges();
      },
    });
  }

  irParaCardapio(): void {
    this.router.navigate(['/cardapio']);
  }
}
