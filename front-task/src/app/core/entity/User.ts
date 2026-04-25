export interface Role{
    id: number;
    name: string;
}

export interface User{
    username: string;
    password: string;
    role?: Role;
  }