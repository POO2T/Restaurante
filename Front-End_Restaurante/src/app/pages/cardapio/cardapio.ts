import { Component, OnInit } from '@angular/core';

// Futuramente, você pode criar uma interface para definir a estrutura de um Produto
interface Produto {
  nome: string;
  descricao: string;
  imagemUrl: string;
}

@Component({
  selector: 'app-cardapio',
  templateUrl: './cardapio.html',
  styleUrl: './cardapio.css'
})
export class Cardapio implements OnInit {

  // Lista de produtos (dados de exemplo)
  produtos: Produto[] = [];

  constructor() { }

  ngOnInit(): void {
    // Este método é chamado quando o componente é iniciado.
    // Aqui, preenchemos a lista de produtos.
    this.produtos = [
      {
        nome: 'Lasanha à Bolonhesa',
        descricao: 'Uma obra-prima da culinária italiana, feita com camadas de massa fresca...',
        imagemUrl: 'assets/imagens/lasanha.jpg' // Coloque suas imagens na pasta /src/assets/imagens
      },
      {
        nome: 'Salmão Grelhado ao Limone',
        descricao: 'Um filé de salmão fresco, perfeitamente grelhado para garantir uma crosta crocante...',
        imagemUrl: 'assets/imagens/salmao.jpg'
      },
    ];
  }
}