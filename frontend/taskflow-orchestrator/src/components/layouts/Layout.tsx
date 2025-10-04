/*
// components/layout/Layout.tsx
import React, { useState } from 'react';
import Header from '../Header';
import Sidebar from '../Sidebar';

interface LayoutProps {
    children: React.ReactNode;
}

const Layout: React.FC<LayoutProps> = ({ children }) => {
    const [isSidebarOpen, setIsSidebarOpen] = useState(false);

    return (
        <div className="flex h-screen bg-gray-50 overflow-hidden">
            {/!* Sidebar - slides in from left *!/}
            <div
                className={`fixed inset-y-0 left-0 z-30 w-64 bg-white shadow-lg transform transition-transform duration-300 ease-in-out ${
                    isSidebarOpen ? 'translate-x-0' : '-translate-x-full'
                } md:relative md:translate-x-0 md:shadow-none`}
            >
                <Sidebar />
            </div>

            {/!* Overlay for mobile when sidebar is open *!/}
            {isSidebarOpen && (
                <div
                    className="fixed inset-0 z-20 bg-black bg-opacity-50 md:hidden"
                    onClick={() => setIsSidebarOpen(false)}
                />
            )}

            <div className="flex flex-col flex-1 overflow-y-auto">
                <Header onToggleSidebar={() => setIsSidebarOpen(!isSidebarOpen)} />
                <main className="flex-1 p-6">
                    {children}
                </main>
            </div>
        </div>
    );
};

export default Layout;*/


// components/layout/Layout.tsx
import React, { useState } from 'react';
import Header from '../Header';
import Sidebar from '../Sidebar';

interface LayoutProps {
    children: React.ReactNode;
}

const Layout: React.FC<LayoutProps> = ({ children }) => {
    const [isSidebarOpen, setIsSidebarOpen] = useState(true);

    return (
        <div className="flex h-screen bg-gray-50 overflow-hidden">
            {/* Sidebar - slides in from left */}
            <div
                className={`fixed inset-y-0 left-0 z-30 w-64 bg-white shadow-lg transform transition-transform duration-300 ease-in-out ${
                    isSidebarOpen ? 'translate-x-0' : '-translate-x-full'
                }`}
            >
                <Sidebar />
            </div>

            {/* Main content - pushed when sidebar open */}
            <div
                className={`flex flex-col flex-1 transition-all duration-300 ease-in-out ${
                    isSidebarOpen ? 'ml-64' : 'ml-0'
                }`}
            >
                <Header onToggleSidebar={() => setIsSidebarOpen(!isSidebarOpen)} />
                <main className="flex-1 p-6 overflow-y-auto">
                    {children}
                </main>
            </div>
        </div>
    );
};

export default Layout;