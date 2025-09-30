// types/task.ts
export interface Task {
  id: string;
  title: string;
  description: string;
  status: 'TODO' | 'IN_PROGRESS' | 'DONE';
  priority: 'LOW' | 'MEDIUM' | 'HIGH';
  department: string;
  assignedTo: string[];
  createdAt: string;
  dueDate?: string;
  attachments: string[];
  comments: Comment[];
}

export interface User {
  id: string;
  name: string;
  email: string;
  department: string;
  role: 'ADMIN' | 'USER';
  weeklyQuota: number;
  completedThisWeek: number;
}