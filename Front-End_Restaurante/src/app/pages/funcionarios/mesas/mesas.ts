import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';

import { Mesa } from '../../../models/mesa.model';
import { statusMesa } from '../../../models/mesa.model';
import { MesasService } from '../../../services/mesas/mesas.service';
import { AuthGuard } from '../../../guards/auth.guard';

@Component({
  selector: 'app-mesas',
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './mesas.html',
  styleUrls: ['./mesas.css'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class Mesas {

  mesaForm: FormGroup;
  erro = '';
  
  mesas: Mesa[] = [];
  statusMesa = Object.values(statusMesa);

  private fb = inject(FormBuilder);
  private mesaService = inject(MesasService);
  //private authGuard = inject(AuthGuard);

  constructor() {

    // PROTEÇÃO DE ROTA PARA FUNCIONÁRIOS
    // if (!this.authGuard.canActivateFuncionario()) {
    //   console.warn('Acesso negado. Redirecionando para a página de login do funcionário.');
    // }

    this.mesaForm = this.fb.group({
      numero: [null, Validators.required],
      nome: [null, Validators.required],
      status: [null, Validators.required]
    });
    this.loadMesas();
  }

  private formatError(err: unknown, fallback = 'Ocorreu um erro'): string {
    if (!err) return fallback;
    if (typeof err === 'string') return err;
    if (err instanceof Error) return err.message || fallback;
    const anyErr = err as { error?: unknown; message?: string };
    if (anyErr.error)
      return typeof anyErr.error === 'string'
        ? anyErr.error
        : JSON.stringify(anyErr.error);
    if (anyErr.message) return anyErr.message;
    try {
      return JSON.stringify(err);
    } catch {
      return fallback;
    }
  }

  onSubmit() {
    if (this.mesaForm.invalid) {
      this.erro = 'Formulário inválido. Preencha os campos obrigatórios.';
      return;
    }

    const mesa: Mesa = this.mesaForm.value;
    
    mesa.nome = mesa.nome.toUpperCase();

    this.mesaService.postMesa(mesa).subscribe((newMesa) => {
      this.mesas.push(newMesa);
      this.mesaForm.reset();
    });
  }

  private loadMesas() {
    this.mesaService.getMesas().subscribe((mesas) => {
      this.mesas = mesas;
    });
    if (this.mesas.length === 0) {
      this.erro = 'Nenhuma mesa encontrada.';
      console.warn(this.erro);
    }
  }

}
