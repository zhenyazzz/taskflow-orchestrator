// pages/Dashboard.tsx
import React from 'react';
import { TaskCard } from '../components/TaskCard';

// Mock данные для дизайна
const mockTasks = [
  {
    id: '1',
    title: 'Fix authentication bug',
    description: 'Users are experiencing issues with JWT token expiration',
    status: 'TODO' as const,
    priority: 'HIGH' as const,
    department: 'Development',
    assignedTo: ['JD', 'AS', 'MR'],
    createdAt: '2024-01-20',
    attachments: [],
    comments: [],
  },
  {
    id: '2',
    title: 'Update documentation',
    description: 'Add new API endpoints to the developer documentation',
    status: 'IN_PROGRESS' as const,
    priority: 'MEDIUM' as const,
    department: 'Documentation',
    assignedTo: ['TP'],
    createdAt: '2024-01-19',
    attachments: [],
    comments: [],
  },
];

const Dashboard: React.FC = () => {
  return (
    <div className="space-y-6">
      {/* Статистика */}
      <div className="grid grid-cols-1 md:grid-cols-4 gap-6">
        <div className="bg-white p-6 rounded-lg shadow-sm border">
          <div className="flex items-center">
            <div className="p-2 bg-blue-100 rounded-lg">
              <span className="text-2xl">📊</span>
            </div>
            <div className="ml-4">
              <p className="text-sm text-gray-600">Weekly Quota</p>
              <p className="text-2xl font-bold">3/5 tasks</p>
            </div>
          </div>
        </div>
        
        <div className="bg-white p-6 rounded-lg shadow-sm border">
          <div className="flex items-center">
            <div className="p-2 bg-green-100 rounded-lg">
              <span className="text-2xl">✅</span>
            </div>
            <div className="ml-4">
              <p className="text-sm text-gray-600">Completed</p>
              <p className="text-2xl font-bold">12 tasks</p>
            </div>
          </div>
        </div>
        
        <div className="bg-white p-6 rounded-lg shadow-sm border">
          <div className="flex items-center">
            <div className="p-2 bg-yellow-100 rounded-lg">
              <span className="text-2xl">⏰</span>
            </div>
            <div className="ml-4">
              <p className="text-sm text-gray-600">In Progress</p>
              <p className="text-2xl font-bold">5 tasks</p>
            </div>
          </div>
        </div>
        
        <div className="bg-white p-6 rounded-lg shadow-sm border">
          <div className="flex items-center">
            <div className="p-2 bg-purple-100 rounded-lg">
              <span className="text-2xl">🎯</span>
            </div>
            <div className="ml-4">
              <p className="text-sm text-gray-600">Available</p>
              <p className="text-2xl font-bold">8 tasks</p>
            </div>
          </div>
        </div>
      </div>

      {/* Список задач */}
      <div className="bg-white rounded-lg shadow-sm border">
        <div className="p-6 border-b">
          <h3 className="text-lg font-semibold">Recent Tasks</h3>
        </div>
        <div className="p-6">
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
            {mockTasks.map((task) => (
              <TaskCard key={task.id} task={task} />
            ))}
          </div>
        </div>
      </div>
    </div>
  );
};

export default Dashboard;