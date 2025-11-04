import { ChangeDetectionStrategy, Component } from '@angular/core';
import { Mesa } from '../../../models/mesa.model';

@Component({
  selector: 'app-mesas',
  imports: [],
  templateUrl: './mesas.html',
  styleUrls: ['./mesas.css'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class Mesas {

  mesas: Mesa[] = [
    { id: 1, numero: 1, nome: 'Mesa 1', status: 'DISPONIVEL' },
    { id: 2, numero: 2, nome: 'Mesa 2', status: 'OCUPADA' },
    { id: 3, numero: 3, nome: 'Mesa 3', status: 'RESERVADA' }
  ];

  constructor() {}

}
