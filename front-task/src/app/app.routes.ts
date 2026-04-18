import { Routes } from '@angular/router';
import { LoginComponent } from './login-component/login-component';
import { TaskListComponent } from './task-list-component/task-list-component';
import { EditTaskComponent } from './edit-task-component/edit-task-component';

export const routes: Routes = [
    { path: 'login', component: LoginComponent },
  { path: 'tasks/:id', component: EditTaskComponent },
  { path: 'tasks', component: TaskListComponent }
];
