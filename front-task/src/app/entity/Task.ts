import { TaskStatus } from "./TaskStatus";

export class Task{
    constructor(i: number, t: string, s: TaskStatus, cB: string, cA: Date){
        this.id = i;
        this.title = t;
        this.status = s;
        this.createdBy = cB;
        this.createdAt = cA;
    }
    
    id: number;
    title: string;
    status: TaskStatus;
    createdBy: string;
    createdAt: Date; 
  }