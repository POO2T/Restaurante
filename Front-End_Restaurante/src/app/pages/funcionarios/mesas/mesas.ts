import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';

import { Mesa } from '../../../models/mesa.model';
import { statusMesa } from '../../../enums/statusMesa';
import { MesasService } from '../../../services/mesas/mesas.service';
// import { AuthGuard } from '../../../guards/auth.guard';

@Component({
  selector: 'app-mesas',
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './mesas.html',
  styleUrls: ['./mesas.css'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class Mesas {

  mesaForm!: FormGroup;
  editMesaForm!: FormGroup;
  erro = '';
  
  mesas: Mesa[] = [];
  statusMesa = Object.values(statusMesa);

  mesaSelecionada: Mesa | null = null;
  mesaInfoVisible: boolean = false;

  private fb = inject(FormBuilder);
  private mesaService = inject(MesasService);
  // private authGuard = inject(AuthGuard);

  constructor() {

    this.mesaForm = this.fb.group({
      numero: [null, Validators.required],
      nome: [null, Validators.required],
      status: [null, Validators.required]
    });

    // Form usado para editar uma mesa selecionada (não conflita com o form de criação)
    this.editMesaForm = this.fb.group({
      numero: [{ value: null, disabled: false }, Validators.required],
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
      this.erro = 'Formulário inválido. Por favor, verifique os campos.';
      return;
    }

    const mesa: Mesa = this.mesaForm.value;
    
    mesa.nome = mesa.nome.toUpperCase();

    this.mesaService.postMesa(mesa).subscribe({
      next: (newMesa) => {
        this.mesas.push(newMesa);
        this.mesaForm.reset();
        // clear any previous 'nenhuma mesa' message and show success
        this.erro = 'Mesa cadastrada com sucesso!';
      },
      error: (err) => {
        this.erro = this.formatError(err, 'Erro ao cadastrar mesa.');
        console.error('Erro ao cadastrar mesa:', err);
      }
    });
  }

  private loadMesas() {
    this.mesaService.getMesas().subscribe({
      next: (mesas) => {
        this.mesas = mesas || [];
        if (this.mesas.length === 0) {
          this.erro = 'Nenhuma mesa encontrada.';
          console.warn(this.erro);
        } else {
          // clear any previous error when we have data
          this.erro = '';
        }
      },
      error: (err) => {
        this.erro = this.formatError(err, 'Erro ao carregar mesas.');
        console.error('Erro ao carregar mesas:', err);
      }
    });
  }

  selecionarMesa(mesa: Mesa) {
    this.mesaSelecionada = mesa;
    this.mesaInfoVisible = true;

    // Preencher o formulário de edição com os valores da mesa selecionada
    this.editMesaForm.patchValue({
      numero: mesa.numero,
      nome: mesa.nome,
      status: mesa.status
    });
    //alert(`Mesa selecionada: ${mesa.nome} (ID: ${mesa.id})`);
    console.log(`Mesa selecionada:`, mesa);
  }
  closeMesaInfo() {
    this.mesaInfoVisible = false;
    this.mesaSelecionada = null;
  }

  onUpdateSubmit() {
    if (!this.mesaSelecionada) {
      this.erro = 'Nenhuma mesa selecionada para atualizar.';
      return;
    }

    if (this.editMesaForm.invalid) {
      this.erro = 'Formulário inválido. Verifique os campos.';
      return;
    }

    const newNumero: number = this.editMesaForm.get('numero')!.value;
    const newNome: string = this.editMesaForm.get('nome')!.value?.toUpperCase();
    const newStatus: string = this.editMesaForm.get('status')!.value;

    // Validação: impedir escolher um número já existente em outra mesa
    const conflict: boolean = this.mesas.some(m => m.numero === newNumero && m.id !== this.mesaSelecionada!.id);
    if (conflict) {
      this.erro = 'Já existe uma mesa com esse número. Escolha outro número.';
      return;
    }

    const alterarBasico: boolean = (newNumero !== this.mesaSelecionada.numero) || (newNome !== this.mesaSelecionada.nome);
    const alterarStatus: boolean = (newStatus !== this.mesaSelecionada.status);

    // Helper to update local array with returned DTO
    const applyUpdated = (updatedMesa: Mesa) => {
      const idx = this.mesas.findIndex((m) => m.id === updatedMesa.id);
      if (idx > -1) this.mesas[idx] = updatedMesa;
      this.mesaSelecionada = updatedMesa;
    };

    // If only status changed, call status endpoint; if only basic info changed call putMesa;
    // if both changed, call putMesa first (name/numero) then status endpoint.

    const updateStatus = (id: number) => {
      return this.mesaService.putMesaStatus(id, newStatus).subscribe({
        next: (updatedMesa) => {
          applyUpdated(updatedMesa);
          this.erro = 'Mesa atualizada com sucesso!';
          console.log('Status atualizado:', updatedMesa);
        },
        error: (err) => {
          this.erro = this.formatError(err, 'Erro ao atualizar status da mesa.');
          console.error('Erro ao atualizar status da mesa:', err);
        }
      });
    };

    if (alterarBasico) {
      const basicPayload: Partial<Mesa> = { numero: newNumero, nome: newNome };
      this.mesaService.putMesa(this.mesaSelecionada.id, basicPayload).subscribe({
        next: (updatedMesa) => {
          applyUpdated(updatedMesa);
          // if status also changed, call status endpoint
          if (alterarStatus) {
            updateStatus(this.mesaSelecionada!.id);
          } else {
            this.erro = 'Mesa atualizada com sucesso!';
            console.log('Mesa atualizada:', updatedMesa);
          }
        },
        error: (err) => {
          this.erro = this.formatError(err, 'Erro ao atualizar mesa.');
          console.error('Erro ao atualizar mesa:', err);
        }
      });
    } else if (alterarStatus) {
      // Only status changed
      updateStatus(this.mesaSelecionada.id);
    } else {
      this.erro = 'Nenhuma alteração detectada.';
    }
  }

  onDeleteMesa(mesa: Mesa | null) {
    if (!mesa) {
      this.erro = 'Nenhuma mesa selecionada para deletar.';
      return;
    }

    if (!confirm(`Confirma a exclusão da mesa "${mesa.nome}" (ID: ${mesa.id})? Esta ação não pode ser desfeita.`)) {
      return;
    }

    this.mesaService.deleteMesa(mesa.id).subscribe({
      next: () => {
        this.mesas = this.mesas.filter(m => m.id !== mesa.id);
        this.erro = 'Mesa deletada com sucesso!';
        console.log('Mesa deletada:', mesa);
      
        this.closeMesaInfo();
      },
      error: (err: any) => {
        this.erro = this.formatError(err, 'Erro ao deletar mesa.');
        console.error('Erro ao deletar mesa:', err);
      }
    });
  }

}
