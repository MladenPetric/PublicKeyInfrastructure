import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { FormsModule } from '@angular/forms';
import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { NavBarComponent } from './nav-bar/nav-bar.component';
import { LoginComponent } from './login/login.component';
import { RegisterComponent } from './register/register.component';
import { HomeAdminComponent } from './home/home-admin/home-admin.component';
import { HomeCaComponent } from './home/home-ca/home-ca.component';
import { HomeUserComponent } from './home/home-user/home-user.component';
import { ViewAllCertifcatesComponent } from './certificate/view-certifcates/view-certifcates.component';
import { CreateCertifcatesComponent } from './certificate/create-certifcates/create-certifcates.component';
import { AddCaComponent } from './add-ca/add-ca.component';
import { CreateSchemaComponent } from './certificate/create-schema/create-schema.component';
import { IssueCertificateComponent } from './certificate/issue-certificate/issue-certificate.component';
import { ViewMyCertificatesComponent } from './certificate/view-my-certificates/view-my-certificates.component';
import { RequestCertificateComponent } from './certificate/request-certificate/request-certificate.component';

@NgModule({
  declarations: [
    AppComponent,
    NavBarComponent,
    LoginComponent,
    RegisterComponent,
    HomeAdminComponent,
    HomeCaComponent,
    HomeUserComponent,
    ViewAllCertifcatesComponent,
    CreateCertifcatesComponent,
    AddCaComponent,
    CreateSchemaComponent,
    IssueCertificateComponent,
    ViewMyCertificatesComponent,
    RequestCertificateComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    FormsModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
