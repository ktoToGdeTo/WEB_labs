import { ChangeDetectorRef, Component, inject, OnInit } from '@angular/core';
import { ViewTask } from "../view-task/view-task";
import { Task } from '../../core/entity/Task';
import { TaskService } from '../../core/services/taskService';
import { Router } from '@angular/router';
import { AuthService } from '../../core/services/authService';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-task-list-component',
  imports: [ViewTask, CommonModule],
  templateUrl: './task-list-component.html',
  styleUrl: './task-list-component.css',
})
export class TaskListComponent implements OnInit {
  tasks: Task[] = [];

  authService = inject(AuthService);
  taskService = inject(TaskService);
  router = inject(Router);
  private cd = inject(ChangeDetectorRef);

  userId$: number = 0;


  ngOnInit(): void {
    this.userId$ = this.authService.getUserId$();
    if (sessionStorage.getItem('roles') == 'ROLE_ADMIN') {
      this.getAllTasks();
    }
    else if (sessionStorage.getItem('roles') == 'ROLE_USER') {
      this.getTasksById(this.userId$);
    }
  }

  getAllTasks() {
    this.taskService.getAllTasks().subscribe({
      next: (data) => {
        this.tasks = data;
        console.log(this.tasks)
        this.cd.markForCheck();
      },
      error: (err) => {
        console.error('Ошибка при загрузке тасок.', err);
      }
    });
  }

  getTasksById(userId: number) {
    this.taskService.getTasks(userId).subscribe({
      next: (data) => {
        this.tasks = data;
        console.log(this.tasks)
        this.cd.markForCheck();
      },
      error: (err) => {
        console.error('Ошибка при загрузке тасок.', err);
      }
    });
  }

  deleteTask(id: number) {
    this.taskService.deleteTask(id).subscribe({
      next: (msg: any) => {
        this.tasks = this.tasks.filter(task => task.id !== id);
        console.error('Delete task');
      },
      error: (err) => {
        console.error('Error deleting task', err);
      }
    });
  }

  updateTask(id: number) {
    console.log(`Переход по ID: ${id}`);
    this.router.navigate(['/tasks/', id]);
  }

}
