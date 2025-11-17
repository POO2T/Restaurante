import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, OnInit, ChangeDetectorRef, inject } from '@angular/core';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Funcionario } from '../../../models/funcionario.model';
import { TipoFuncionario } from '../../../enums/tipoFuncionario';
import { RegisterFuncionarioRequest } from '../../../models/auth.model';
import { FuncionarioService } from '../../../services/funcionario/funcionario.service';

@Component({
  selector: 'app-usuarios',
  imports: [ReactiveFormsModule, CommonModule],
  templateUrl: './usuarios.html',
  styleUrls: ['./usuarios.css'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class Usuarios {

  private fb = inject(FormBuilder);
  private funcionarioService = inject(FuncionarioService);

  // Checagem de mudanças manual
  private cdr = inject(ChangeDetectorRef);

  addFuncionario: boolean = false;
  editandoFuncionario: boolean = false;

  // Indicador de carregamento
  loading: boolean = false;

  funcionarioForm!: FormGroup;
  editFuncionarioForm!: FormGroup;

  dataAvailable: boolean = false;
  erro: string = '';

  funcionarios: Funcionario[] = [];
  tipoFuncionario = Object.values(TipoFuncionario);


  constructor() {
    this.funcionarioForm = this.fb.group({
      nome: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      senha: ['', [Validators.required, Validators.minLength(6)]],
      // confirmarSenha: ['', [Validators.required, Validators.minLength(6)]],
      cargo: ['', Validators.required],
      salario: [0, [Validators.required, Validators.min(0)]],
    });

    this.editFuncionarioForm = this.fb.group({
      id: [{ value: null, disabled: true }],
      nome: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      senha: ['', [Validators.required, Validators.minLength(6)]],
      confirmarSenha: ['', [Validators.required, Validators.minLength(6)]],
      cargo: ['', Validators.required],
      salario: [0, [Validators.required, Validators.min(0)]],
    });
  }

  ngOnInit() {
    this.loadFuncionarios();
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

  private loadFuncionarios() {
    this.loading = true;
    this.funcionarioService.getFuncionarios().subscribe({
      next: (funcionarios) => {
        this.funcionarios = funcionarios;
        this.dataAvailable = true;
        this.loading = false;
        this.cdr.markForCheck();
      },
      error: (err) => {
        this.erro = this.formatError(err, 'Erro ao carregar funcionários.');
        this.dataAvailable = false;
        this.loading = false;
        this.cdr.markForCheck();
      }
    });
  }

  onSubmit() {
    this.loading = true;

    if (this.funcionarioForm.invalid) {
      this.erro = 'Formulário inválido. Preencha todos os campos corretamente.';
      return;
    }

    const novoFuncionario = this.funcionarioForm.value as RegisterFuncionarioRequest;
    
    this.funcionarioService.registerFuncionario(novoFuncionario).subscribe({
      next: (funcionario) => {
        // CAST USADO APENAS PARA EXEMPLO, AJUSTAR CONFORME NECESSÁRIO
        this.funcionarios.push(funcionario as Funcionario);
        this.funcionarioForm.reset();
        this.erro = '';
      },
      error: (err) => {
        this.erro = this.formatError(err, 'Erro ao criar funcionário.');
      }
    })

    this.addFuncionario = false;

  }

  onEditSubmit() {
    if (this.editFuncionarioForm.invalid) {
      this.erro = 'Formulário inválido. Preencha todos os campos corretamente.';
      return;
    }

    const funcionarioAtualizado = this.editFuncionarioForm.value as RegisterFuncionarioRequest & { id: number };

    this.funcionarioService.updateFuncionario(funcionarioAtualizado.id, funcionarioAtualizado).subscribe({
      next: (funcionario) => {
        const index = this.funcionarios.findIndex(f => f.id === funcionario.id);
        if (index !== -1) {
          this.funcionarios[index] = funcionario as Funcionario;
        }
        this.editFuncionarioForm.reset();
        this.erro = '';
      },
      error: (err) => {
        this.erro = this.formatError(err, 'Erro ao atualizar funcionário.');
      }
    });

    this.editandoFuncionario = false;
  }

  toggleFuncionario() {
    this.addFuncionario = !this.addFuncionario;
  }
  closeFuncionario() {
    this.addFuncionario = false;
    this.editandoFuncionario = false;
    this.funcionarioForm.reset();
  }

  editarFuncionario(funcionario: Funcionario) {
    this.editandoFuncionario = true;
    this.editFuncionarioForm.patchValue(funcionario);
  }

  deletarFuncionario(funcionario: Funcionario) {
    
    if (!confirm(`Confirma a exclusão do funcionário ${funcionario.nome}?`)) {
      return;
    }

    this.funcionarioService.deleteFuncionario(funcionario.id).subscribe({
      next: () => {
        this.funcionarios = this.funcionarios.filter(f => f.id !== funcionario.id);
        this.erro = 'Usuário deletado com sucesso.';
        console.warn(this.erro);
      },
      error: (err) => {
        this.erro = this.formatError(err, 'Erro ao deletar funcionário.');
      }
    });
  }



}
