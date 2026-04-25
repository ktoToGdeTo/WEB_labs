import { Routes } from '@angular/router';
import { LoginComponent } from './pages/login-component/login-component';
import { TaskListComponent } from './pages/task-list-component/task-list-component';
import { EditTaskComponent } from './pages/edit-task-component/edit-task-component';

export const routes: Routes = [
  { path: "", redirectTo:"tasks", pathMatch:"full" },
  { path: "login", component: LoginComponent },
  { 
    path: "tasks", 
    children: [
      { path: "", component: TaskListComponent },
      { path: ":id", component: EditTaskComponent },
      { path: "new", component: EditTaskComponent }
    ]
  },
  
  // 🌐 Ловим все несуществующие пути
  { path: "**", redirectTo: "login" }
];
