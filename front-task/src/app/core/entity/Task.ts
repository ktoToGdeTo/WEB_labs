export interface Task{    
    id?: number;
    title: string;
    status: string;
    createdBy?: number;
    createdAt?: Date; 
  }