import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';

const routes: Routes = [
  {
    path: '', pathMatch: 'full', redirectTo: 'busca-reativa'
  },
  {
    path: 'busca-reativa',
    loadChildren: () => import('./reactive-search/reactive-search.module').then(m => m.ReactiveSearchModule)
  }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
