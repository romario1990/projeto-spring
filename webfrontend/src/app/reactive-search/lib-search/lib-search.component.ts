import { Component, OnInit } from '@angular/core';
import { Observable } from 'rxjs';
import { HttpClient, HttpParams, HttpHeaders } from '@angular/common/http';
import { tap, map, filter, distinctUntilChanged, debounceTime, switchMap } from 'rxjs/operators';
import { FormBuilder, FormGroup, FormControl, Validators, FormArray} from '@angular/forms';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-lib-search',
  templateUrl: './lib-search.component.html',
  styleUrls: ['./lib-search.component.css']
})
export class LibSearchComponent implements OnInit {
  queryField = new FormControl();
  readonly SEARCH_URL = 'http://localhost:8080/user/search';
  readonly HEADERS = new HttpHeaders({
    'Content-Type': 'application/json',
    'Access-Control-Allow-Origin': '*',
    'Access-Control-Allow-Headers': 'Content-Type',
    'Access-Control-Allow-Methods': 'GET,POST,OPTIONS,DELETE,PUT',
  });
  results$: Observable<any> | undefined;
  total: number | undefined;
  readonly FIELDS = 'name,username,email';
  form: FormGroup;
  filtersList: any = [
    { id: 1, name: 'name', label: 'Nome' },
    { id: 2, name: 'username', label: 'Nome de usuário' },
    { id: 3, name: 'email', label: 'E-mail' }
  ];

  constructor(private http: HttpClient, private formBuilder: FormBuilder) {
    this.form = this.formBuilder.group({
      filters: this.formBuilder.array([1,2,3], [Validators.required])
    })
  }

  onCheckboxChange(e: any) {
    const filters: FormArray = this.form.get('filters') as FormArray;

    if (e.target.checked) {
      filters.push(new FormControl(e.target.value));
    } else {
       const index = filters.controls.findIndex(x => x.value === e.target.value);
       filters.removeAt(index);
    }
  }
  ngOnInit() {
    this.results$ = this.queryField.valueChanges
    .pipe(
      map(value => value.trim()),
      filter(value => value.length > 1),
      debounceTime(200),
      distinctUntilChanged(),
      switchMap(value => this.http.get(this.SEARCH_URL, {
        params: {
          search: value,
          filter: this.FIELDS
        },
        headers: this.HEADERS,
      })),
      tap((res: any) => this.total = res.total),
      map((res: any) => res.results)
    );
  }

  onSearch() {
    const fields: any = [];
    this.form.value.filters.forEach((element: string | number) => {
      fields.push(this.filtersList.find((f: { id: number; })=> f.id === +element)?.name)
    });
    if(fields.length === 0){
      Swal.fire('Selecione ao menos um filtro!');
      return;
    }
    let value = this.queryField.value;
    if (value && (value = value.trim()) !== '') {

      let params = new HttpParams();
      params = params.set('search', value);
      params = params.set('filter', fields.join(','));

      this.results$ = this.http
        .get(this.SEARCH_URL, { params, headers: this.HEADERS, })
        .pipe(
          tap((res: any) => (this.total = res.total)),
          map((res: any) => res.results)
        );
    }
  }
}
