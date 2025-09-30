import React, { useState, useEffect } from 'react';
import type { Task } from '../types/Task';
import { TaskCard } from '../components/TaskCard';
import { TaskService } from '../services/taskService';

const TaskListPage: React.FC = () => {
  const [tasks, setTasks] = useState<Task[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    loadTasks();
  }, []);

  const loadTasks = async () => {
    try {
      setLoading(true);
      const fetchedTasks = await TaskService.getAllTasks();
      setTasks(fetchedTasks);
    } catch (err) {
      setError('Failed to load tasks');
      console.error('Error loading tasks:', err);
    } finally {
      setLoading(false);
    }
  };

  const handleDeleteTask = async (taskId: string) => {
    try {
      await TaskService.deleteTask(taskId);
      setTasks(tasks.filter(task => task.id !== taskId));
    } catch (err) {
      setError('Failed to delete task');
      console.error('Error deleting task:', err);
    }
  };

  if (loading) {
    return (
      <div className="flex justify-center items-center h-64">
        <div className="text-lg">Loading tasks...</div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="flex justify-center items-center h-64">
        <div className="text-red-600">{error}</div>
      </div>
    );
  }

  return (
    <div className="container mx-auto px-4 py-8">
      <div className="flex justify-between items-center mb-6">
        <h1 className="text-3xl font-bold text-gray-900">TaskFlow Orchestrator</h1>
        <button className="bg-blue-600 text-white px-4 py-2 rounded-lg hover:bg-blue-700">
          Add New Task
        </button>
      </div>

      {tasks.length === 0 ? (
        <div className="text-center py-12">
          <div className="text-gray-500 text-lg">No tasks found</div>
          <div className="text-gray-400">Create your first task to get started</div>
        </div>
      ) : (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
          {tasks.map((task) => (
            <TaskCard
              key={task.id}
              task={task}
              onDelete={handleDeleteTask}
            />
          ))}
        </div>
      )}
    </div>
  );
};

export default TaskListPage;
