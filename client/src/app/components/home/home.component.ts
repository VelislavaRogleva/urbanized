import { Component, OnInit } from '@angular/core';
import {AuthService} from '../../core/services/auth.service';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html'
})
export class HomeComponent implements OnInit {

  constructor(public authService: AuthService) { }

  ngOnInit() {
  }

}
