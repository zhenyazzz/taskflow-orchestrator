// components/layout/Sidebar.tsx
import React from 'react';
import { Link, useLocation } from 'react-router-dom';

const Sidebar: React.FC = () => {
    const location = useLocation();
    
    const menuItems = [
        { name: 'Мои задачи', icon: '👤', href: '/my-tasks' },
        { name: 'Общий пул задач', icon: '🎯', href: '/task-pool' },
        { name: 'Аналитика', icon: '📈', href: '/analytics' },
        { name: 'Администрирование', icon: '⚙️', href: '/admin' },
    ];

    const isActive = (href: string) => location.pathname === href;

    return (
        <nav className="w-64 bg-white shadow-lg h-full overflow-y-auto">
            <div className="p-4 border-b">
                <h1 className="text-xl font-bold text-gray-800">TaskFlow</h1>
                <p className="text-sm text-gray-600">Отдел разработки</p>
            </div>
            <ul className="p-4 space-y-2">
                {menuItems.map((item) => (
                    <li key={item.name}>
                        <Link
                            to={item.href}
                            className={`flex items-center space-x-3 p-3 rounded-lg transition-colors ${
                                isActive(item.href)
                                    ? 'bg-blue-500 text-white'
                                    : 'hover:bg-blue-50 hover:text-blue-600'
                            }`}
                        >
                            <span>{item.icon}</span>
                            <span>{item.name}</span>
                        </Link>
                    </li>
                ))}
            </ul>
        </nav>
    );
};

export default Sidebar;