import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { AuthService } from '../../../services/auth/auth.service';
import { Funcionario, TipoFuncionario } from '../../../models/funcionario.model';
import { RegisterFuncionarioRequest } from '../../../models/auth.model';

@Component({
  selector: 'app-usuarios',
  imports: [ReactiveFormsModule, CommonModule],
  templateUrl: './usuarios.html',
  styleUrls: ['./usuarios.css'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class Usuarios {

  private fb = inject(FormBuilder);
  private authService = inject(AuthService);

  funcionarioForm: FormGroup;
  erro: string = '';

  //usuarios: Usuario[] = [];
  funcionarios: Funcionario[] = [];
  tipoFuncionario = Object.values(TipoFuncionario);


  constructor() {
    this.funcionarioForm = this.fb.group({
      nome: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      senha: ['', [Validators.required, Validators.minLength(6)]],
      tipoFuncionario: ['', Validators.required],
      salario: [0, [Validators.required, Validators.min(0)]],
    });
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
    
    if (this.funcionarioForm.invalid) {
      this.erro = 'Formulário inválido. Preencha todos os campos corretamente.';
      return;
    }

    const novoFuncionario = this.funcionarioForm.value as RegisterFuncionarioRequest;
    

    this.authService.registerFuncionario(novoFuncionario).subscribe({
      next: (funcionario) => {
        // CAST USADO APENAS PARA EXEMPLO, AJUSTAR CONFORME NECESSÁRIO
        this.funcionarios.push(funcionario as Funcionario);
        this.funcionarioForm.reset();
        this.erro = '';
      },
      error: (err) => {
        this.erro = this.formatError(err, 'Erro ao criar funcionário.');
      }
    });

  }

  adicionarFuncionario() {}

  editarFuncionario(funcionario: Funcionario) {

  }
  deletarFuncionario(funcionario: Funcionario) {

  }

}
