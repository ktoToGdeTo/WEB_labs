import { ChangeDetectorRef, Component, EventEmitter, inject, Input, OnInit, Output } from '@angular/core';
import { TaskService } from '../../core/services/taskService';
import { Task } from '../../core/entity/Task';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-view-task',
  imports: [CommonModule],
  templateUrl: './view-task.html',
  styleUrl: './view-task.css',
})
export class ViewTask{

  private cd = inject(ChangeDetectorRef);
  @Input({ required: true }) task!: Task;
  @Output() deleteTask = new EventEmitter<number>();
  @Output() changeTask = new EventEmitter<number>();


  onDelete(): void {
    this.deleteTask.emit(this.task.id);
    this.cd.markForCheck();
  }

  onChange(): void {
    this.changeTask.emit(this.task.id);
  }

  hasRole(roleName: string): boolean {
    return roleName == sessionStorage.getItem("roles");
  }
  

}
