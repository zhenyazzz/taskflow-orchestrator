import React, { useRef } from 'react';
import Header from '../Header';
import Footer from '../Footer';

interface LayoutProps {
    children: React.ReactNode;
}

const Layout: React.FC<LayoutProps> = ({ children }) => {
    const mainRef = useRef<HTMLElement>(null);
    return (
        <div className="flex flex-col h-screen bg-gray-50 overflow-hidden">
            <Header />
            <main ref={mainRef} className="flex-1 p-6 overflow-y-auto pt-16">
                {children}
            </main>
            <Footer scrollContainerRef={mainRef} />
        </div>
    );
};

export default Layout;