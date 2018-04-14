import { Component, OnInit } from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {AuthService} from '../../core/services/auth.service';
import {Router} from '@angular/router';

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html'
})
export class RegisterComponent implements OnInit {

  form: FormGroup;

  constructor(private fb: FormBuilder,
              private authService: AuthService,
              private router: Router) {
    this.form = this.fb.group({

      username: ['', Validators.required],
      email: ['', Validators.required],
      password: ['', Validators.required],
      confirm: ['', Validators.required]

    });
  }

  ngOnInit() {
  }

  register() {
    const val = this.form.value;

    if (val.username && val.password) {
      this.authService.register(val.username, val.email, val.password);
    }

  }

}