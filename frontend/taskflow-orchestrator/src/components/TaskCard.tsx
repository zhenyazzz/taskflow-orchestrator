/*
// Пример красивого компонента
export function TaskCard({ title, status }: { title: string; status: string }) {
  return (
    <div className="group bg-white rounded-xl shadow-sm hover:shadow-lg transition-all duration-300 border border-gray-100 p-6">
      <h3 className="font-semibold text-gray-800 group-hover:text-blue-600 transition-colors">
        {title}
      </h3>
      <div className="flex items-center justify-between mt-4">
        <span className={`px-3 py-1 rounded-full text-sm font-medium ${
          status === 'completed' 
            ? 'bg-green-100 text-green-800' 
            : 'bg-yellow-100 text-yellow-800'
        }`}>
          {status}
        </span>
        <button className="text-gray-400 hover:text-blue-500 transition-colors">
          ↗
        </button>
      </div>
    </div>
  );
}*/
// components/TaskCard.tsx
import React from 'react';

interface TaskCardProps {
    title: string;
    status: 'completed' | 'in-progress';
}

export const TaskCard: React.FC<TaskCardProps> = ({ title, status }) => {
    return (
        <div className="group bg-white rounded-xl shadow-sm hover:shadow-lg transition-all duration-300 border border-gray-100 p-6">
            <h3 className="font-semibold text-gray-800 group-hover:text-blue-600 transition-colors">
                {title}
            </h3>
            <div className="flex items-center justify-between mt-4">
        <span className={`px-3 py-1 rounded-full text-sm font-medium ${
            status === 'completed'
                ? 'bg-green-100 text-green-800'
                : 'bg-yellow-100 text-yellow-800'
        }`}>
          {status}
        </span>
                <button className="text-gray-400 hover:text-blue-500 transition-colors">
                    ↗
                </button>
            </div>
        </div>
    );
};