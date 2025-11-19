import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class ProdutoService {
  
  private readonly endpoint = '/produtos';

  constructor() { }

}
