// components/layout/Header.tsx
import React from 'react';

const Header: React.FC = () => {
  return (
    <header className="bg-white shadow-sm border-b">
      <div className="flex justify-between items-center px-6 py-4">
        <div>
          <h2 className="text-2xl font-semibold text-gray-800">Dashboard</h2>
          <p className="text-gray-600">Welcome back! Here's what's happening today.</p>
        </div>
        <div className="flex items-center space-x-4">
          <div className="text-right">
            <p className="font-medium">John Doe</p>
            <p className="text-sm text-gray-600">Development Department</p>
          </div>
          <div className="w-10 h-10 bg-blue-500 rounded-full flex items-center justify-center text-white font-semibold">
            JD
          </div>
        </div>
      </div>
    </header>
  );
};

export default Header;