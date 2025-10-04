// types/Task.ts
// @ts-ignore
export enum TaskStatus {
  AVAILABLE = 'AVAILABLE',
  IN_PROGRESS = 'IN_PROGRESS',
  COMPLETED = 'COMPLETED',
  // Добавь другие статусы, если они есть
}

// @ts-ignore
export enum TaskPriority {
  LOW = 'LOW',
  MEDIUM = 'MEDIUM',
  HIGH = 'HIGH',
  // Добавь другие приоритеты, если они есть
}

// @ts-ignore
export enum Department {
  DEVELOPMENT = 'DEVELOPMENT',
  SALES = 'SALES',
  HR = 'HR',
  // Добавь другие отделы, если они есть
}

export interface Comment {
  id?: string; // Опционально, если бэкенд генерирует
  text: string;
  createdBy?: string;
  createdAt?: Date;
  // Добавь другие поля комментария, если они есть
}

export interface Task {
  id: string;
  title: string;
  description?: string;
  status: TaskStatus;
  priority?: TaskPriority;
  assigneeIds: Set<string>; // Используем Set для соответствия бэкенду
  creatorId: string;
  department: Department;
  createdAt: Date;
  updatedAt?: Date;
  dueDate?: Date;
  tags?: string[];
  comments?: Comment[];
}