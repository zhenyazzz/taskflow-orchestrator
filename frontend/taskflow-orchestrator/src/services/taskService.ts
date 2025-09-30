import { Task, CreateTaskRequest, UpdateTaskRequest } from '../types/Task';

const API_BASE_URL = 'http://localhost:8080/api'; // Замени на реальный URL твоего бэкенда

export class TaskService {
  static async getAllTasks(): Promise<Task[]> {
    const response = await fetch(`${API_BASE_URL}/tasks`);
    if (!response.ok) {
      throw new Error('Failed to fetch tasks');
    }
    return response.json();
  }

  static async getTaskById(id: string): Promise<Task> {
    const response = await fetch(`${API_BASE_URL}/tasks/${id}`);
    if (!response.ok) {
      throw new Error('Failed to fetch task');
    }
    return response.json();
  }

  static async createTask(task: CreateTaskRequest): Promise<Task> {
    const response = await fetch(`${API_BASE_URL}/tasks`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(task),
    });
    if (!response.ok) {
      throw new Error('Failed to create task');
    }
    return response.json();
  }

  static async updateTask(id: string, task: UpdateTaskRequest): Promise<Task> {
    const response = await fetch(`${API_BASE_URL}/tasks/${id}`, {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(task),
    });
    if (!response.ok) {
      throw new Error('Failed to update task');
    }
    return response.json();
  }

  static async deleteTask(id: string): Promise<void> {
    const response = await fetch(`${API_BASE_URL}/tasks/${id}`, {
      method: 'DELETE',
    });
    if (!response.ok) {
      throw new Error('Failed to delete task');
    }
  }
}
