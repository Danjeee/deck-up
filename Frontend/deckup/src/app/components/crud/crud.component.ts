import { Component } from '@angular/core';
import { CrudService } from '../../services/crud.service';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';

@Component({
  selector: 'app-crud',
  imports: [CommonModule, FormsModule],
  templateUrl: './crud.component.html',
  styleUrl: './crud.component.css'
})
export class CrudComponent {
  excludedFields: string[] = ['copias', 'id', 'imagen'];
  items: any[] = [];
  filteredItems: any[] = [];
  selectedItem: any = null;
  editingItem: any = null;
  searchTerm: string = '';

  constructor(private crudService: CrudService, private router: Router) { }

  ngOnInit() {
    let mod = this.router.url.split("/")[this.router.url.split("/").length-1]
    this.crudService.setModule(mod)
    this.crudService.getItems().subscribe(data => {
      this.items = data;
      this.filteredItems = [...this.items];
    });
  }

  search() {
    this.filteredItems = this.items.filter(item =>
      JSON.stringify(item).toLowerCase().includes(this.searchTerm.toLowerCase())
    );
  }

  view(item: any) {
    this.selectedItem = item;
    this.editingItem = null;
  }

  edit(item: any) {
    this.editingItem = { ...item };
    this.selectedItem = null;
  }

  cancelEdit() {
    this.editingItem = null;
  }

  modalClosing = false;

  startModalClose(event?: Event): void {
    if (event) event.stopPropagation(); // prevenir cierre si se hace click dentro
    this.modalClosing = true;
    setTimeout(() => {
      this.modalClosing = false;
      this.cancelEdit();
    }, 300); // Esperamos que la animación termine
  }

  isBoolean(value: any): boolean {
    return typeof value === 'boolean';
  }

  // Función para verificar si el valor es un objeto
  isObject(value: any): boolean {
    return typeof value === 'object' && value !== null && !Array.isArray(value);
  }

  onSubmit() {
    const formData = new FormData();
    
    // Iteramos sobre las claves del objeto (los campos del formulario)
    Object.keys(this.editingItem).forEach(key => {
      const value = this.editingItem[key];

      if (this.isBoolean(value)) {
        // Si el valor es un booleano, lo enviamos como true/false
        formData.append(key, value ? 'true' : 'false');
      } else if (this.isObject(value)) {
        const val = (document.getElementById(key) as HTMLInputElement).value
        // Si es un objeto, lo convertimos a JSON
        if (val == "" || val == null){
          formData.append(key, '0')
        } else {
          //formData.append(key, JSON.stringify({ id: value[Object.keys(value)[0]] }));
          formData.append(key, val);
        }
      } else {
        // Si es cualquier otro tipo de valor, lo enviamos como texto
        if (value != null){
          formData.append(key, value);
        } else {
          formData.append(key, '-1');
        }
      }
    });

    // Aquí puedes agregar la lógica para enviar el FormData a un servidor
    this.showFormData(formData)
    this.startModalClose(); // Cerrar modal al guardar
    this.crudService.saveItem(formData).subscribe({
      next: (data) => {console.log(data)}
    })
  }

  showFormData(formData: FormData): void {
    for (let [key, value] of formData.entries()) {
      console.log(`Campo: ${key}, Valor: ${value}`);
    }
  }


  save() {
    const formData = new FormData();
    for (const key in this.editingItem) {
      formData.append(key, this.editingItem[key]);
    }

    this.crudService.saveItem(formData).subscribe((updated: any) => {
      const index = this.items.findIndex(i => i.id === updated.id);
      if (index > -1) {
        this.items[index] = updated;
      } else {
        this.items.push(updated); // si es nuevo
      }
      this.filteredItems = [...this.items];
      this.editingItem = null;
    });
  }

  delete(item: any) {
    if (confirm('¿Seguro que quieres eliminar este ítem?')) {
      this.crudService.deleteItem(item.id).subscribe(() => {
        this.items = this.items.filter(i => i.id !== item.id);
        this.filteredItems = [...this.items];
      });
    }
  }

  objectKeys(obj: any, all: boolean = false): string[] {
    if (all){
      return Object.keys(obj)
    } else {
      return Object.keys(obj).filter(key => !this.excludedFields.includes(key));
    }
  }
}
