export class User{
    constructor(i: number, u: string, p: string){
        this.id = i;
        this.username = u;
        this.password = p;
    }
    
    id: number;
    username: string;
    password: string;
  }