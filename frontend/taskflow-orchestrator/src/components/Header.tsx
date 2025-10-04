// components/layout/Header.tsx
import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom'; // Для навигации

const Header: React.FC = () => {
    const [isAuthenticated, setIsAuthenticated] = useState(false); // Состояние авторизации
    const navigate = useNavigate(); // Хук для навигации

    const menuItems = [
        { name: 'Dashboard', icon: '📊', href: '#' },
        { name: 'All Tasks', icon: '📝', href: '#' },
        { name: 'My Tasks', icon: '👤', href: '#' },
        { name: 'Available Tasks', icon: '🎯', href: '#' },
        { name: 'Analytics', icon: '📈', href: '#' },
        { name: 'Departments', icon: '🏢', href: '#' },
    ];

    // Функции для навигации к эндпоинтам
    const handleLogin = () => {
        navigate('/login'); // Переход к странице логина (предполагаемый эндпоинт /api/auth/login)
    };

    const handleRegister = () => {
        navigate('/register'); // Переход к странице регистрации (предполагаемый эндпоинт /api/auth/register)
    };

    return (
        <header className="fixed top-0 left-0 right-0 z-50 bg-white shadow-md">
            <nav className="px-6 py-4 flex justify-between items-center border-b border-gray-200">
                <ul className="flex space-x-4">
                    {menuItems.map((item) => (
                        <li key={item.name}>
                            <a
                                href={item.href}
                                className="flex items-center space-x-2 px-4 py-2 rounded-lg hover:bg-blue-50 hover:text-blue-600 transition-colors"
                            >
                                <span>{item.icon}</span>
                                <span>{item.name}</span>
                            </a>
                        </li>
                    ))}
                </ul>
                <div className="flex items-center space-x-4">
                    {isAuthenticated ? (
                        <>
                            <div className="text-right">
                                <p className="font-medium">John Doe</p>
                                <p className="text-sm text-gray-600">Development Department</p>
                            </div>
                            <div className="w-10 h-10 bg-blue-500 rounded-full flex items-center justify-center text-white font-semibold">
                                JD
                            </div>
                        </>
                    ) : (
                        <>
                            <button
                                className="px-4 py-2 bg-blue-500 text-white rounded-lg hover:bg-blue-600 transition-colors"
                                onClick={handleLogin}
                            >
                                Войти
                            </button>
                            <button
                                className="px-4 py-2 bg-gray-200 text-gray-800 rounded-lg hover:bg-gray-300 transition-colors"
                                onClick={handleRegister}
                            >
                                Зарегистрироваться
                            </button>
                        </>
                    )}
                </div>
            </nav>
        </header>
    );
};

export default Header;