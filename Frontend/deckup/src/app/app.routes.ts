import { Routes } from '@angular/router';
import { LoginComponent } from './components/login/login.component';
import { RegisterComponent } from './components/register/register.component';
import { HomeComponent } from './components/home/home.component';
import { VerifyComponent } from './components/verify/verify.component';
import { TiendaComponent } from './components/tienda/tienda.component';
import { PaymentsComponent } from './components/payments/payments.component';
import { ColeccionComponent } from './components/coleccion/coleccion.component';

export const routes: Routes = [
    {path: 'login', component: LoginComponent},
    {path: 'register', component: RegisterComponent},
    {path: 'home', component: HomeComponent},
    {path: 'verify', component: VerifyComponent},
    {path: 'tienda', component: TiendaComponent},
    {path: 'tienda/verify', component: PaymentsComponent},
    {path: 'tienda/cancel', component: PaymentsComponent},
    {path: 'coleccion', component: ColeccionComponent},
];
