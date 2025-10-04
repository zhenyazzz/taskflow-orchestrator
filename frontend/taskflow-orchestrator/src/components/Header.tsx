import React from 'react';

const Header: React.FC = () => {
    const menuItems = [
        { name: 'Dashboard', icon: '📊', href: '#' },
        { name: 'All Tasks', icon: '📝', href: '#' },
        { name: 'My Tasks', icon: '👤', href: '#' },
        { name: 'Available Tasks', icon: '🎯', href: '#' },
        { name: 'Analytics', icon: '📈', href: '#' },
        { name: 'Departments', icon: '🏢', href: '#' },
    ];

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
                {/* Profile icon and info */}
                <div className="flex items-center space-x-4">
                    <div className="text-right">
                        <p className="font-medium">John Doe</p>
                        <p className="text-sm text-gray-600">Development Department</p>
                    </div>
                    <div className="w-10 h-10 bg-blue-500 rounded-full flex items-center justify-center text-white font-semibold">
                        JD
                    </div>
                </div>
            </nav>
        </header>
    );
};

export default Header;
