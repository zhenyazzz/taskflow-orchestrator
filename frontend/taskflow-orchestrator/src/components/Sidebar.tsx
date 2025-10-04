// components/layout/Sidebar.tsx
import React from 'react';

const Sidebar: React.FC = () => {
    const menuItems = [
        { name: 'Dashboard', icon: '📊', href: '#' },
        { name: 'All Tasks', icon: '📝', href: '#' },
        { name: 'My Tasks', icon: '👤', href: '#' },
        { name: 'Available Tasks', icon: '🎯', href: '#' },
        { name: 'Analytics', icon: '📈', href: '#' },
        { name: 'Departments', icon: '🏢', href: '#' },
    ];

    return (
        <nav className="w-64 bg-white shadow-lg h-full overflow-y-auto">
            <div className="p-4 border-b">
                <h1 className="text-xl font-bold text-gray-800">TaskFlow</h1>
                <p className="text-sm text-gray-600">Internal Task System</p>
            </div>
            <ul className="p-4 space-y-2">
                {menuItems.map((item) => (
                    <li key={item.name}>
                        <a
                            href={item.href}
                            className="flex items-center space-x-3 p-3 rounded-lg hover:bg-blue-50 hover:text-blue-600 transition-colors"
                        >
                            <span>{item.icon}</span>
                            <span>{item.name}</span>
                        </a>
                    </li>
                ))}
            </ul>
        </nav>
    );
};

export default Sidebar;