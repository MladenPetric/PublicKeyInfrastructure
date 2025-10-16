import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { LoginComponent } from './login/login.component';
import { RegisterComponent } from './register/register.component';
import { HomeAdminComponent } from './home/home-admin/home-admin.component';
import { HomeCaComponent } from './home/home-ca/home-ca.component';
import { HomeUserComponent } from './home/home-user/home-user.component';
import { CreateCertifcatesComponent } from './certificate/create-certifcates/create-certifcates.component';
import { AddCaComponent } from './add-ca/add-ca.component';
import { ViewMyCertificatesComponent } from './certificate/view-my-certificates/view-my-certificates.component';
import { CreateSchemaComponent } from './certificate/create-schema/create-schema.component';
import { IssueCertificateComponent } from './certificate/issue-certificate/issue-certificate.component';
import { RequestCertificateComponent } from './certificate/request-certificate/request-certificate.component';
import { ViewCertificatesComponent } from './certificate/view-certificates/view-certificates.component';

const routes: Routes = [
  { path: '', redirectTo: '/login', pathMatch: 'full' }, 
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },
  {
    path: 'admin',
    component: HomeAdminComponent,
    children: [
      { path: 'certificates', component: ViewCertificatesComponent },
      { path: 'add-certificates', component: CreateCertifcatesComponent },
      { path: 'add-ca', component: AddCaComponent }
    ]
  },
  { 
    path: 'ca', 
    component: HomeCaComponent,
    children: [
      { path: 'my-certificates', component: ViewMyCertificatesComponent},
      { path: 'issue-certificate', component: IssueCertificateComponent },
      { path: 'add-schema', component: CreateSchemaComponent }
    ] 
  },
  { path: 'user', 
    component: HomeUserComponent,
    children: [
      { path: 'my-certificates', component: ViewMyCertificatesComponent},
      { path: 'request', component: RequestCertificateComponent }
    ]  
  }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
