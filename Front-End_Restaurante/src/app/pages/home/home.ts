import { Component, ChangeDetectionStrategy } from '@angular/core';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-home',
  imports: [RouterModule],
  templateUrl: './home.html',
  styleUrls: ['./home.css'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class Home {
  // protected imagens = [
  //   'assets/imgs/home1.jpg',
  //   'assets/imgs/home2.jpg',
  //   'assets/imgs/home3.jpg'
  // ];
}
