/*

// components/layout/Footer.tsx
import React, { useState, useEffect } from 'react';

const Footer: React.FC = () => {
    const [isVisible, setIsVisible] = useState(false);

    useEffect(() => {
        const handleScroll = () => {
            const scrollPosition = window.innerHeight + window.scrollY;
            const pageHeight = document.documentElement.scrollHeight;

            // Show footer only when near the bottom (within 100px of the end)
            if (scrollPosition >= pageHeight - 100) {
                setIsVisible(true);
            } else {
                setIsVisible(false);
            }
        };

        window.addEventListener('scroll', handleScroll);
        return () => window.removeEventListener('scroll', handleScroll);
    }, []);

    return (
        <footer
            className={`fixed bottom-0 left-0 right-0 z-50 shadow-md bg-white border-t border-gray-200 py-4 transition-all duration-300 ease-in-out ${
                isVisible ? 'translate-y-0' : 'translate-y-full'
            }`}
        >
            <div className="px-6 flex justify-between items-center text-sm text-gray-600">
                <div>
                    <p>&copy; 2025 TaskFlow. All rights reserved.</p>
                    <p>Internal Task System</p>
                </div>
                <div className="flex space-x-4">
                    <a href="#" className="hover:text-blue-600 transition-colors">Privacy Policy</a>
                    <a href="#" className="hover:text-blue-600 transition-colors">Terms of Service</a>
                    <a href="#" className="hover:text-blue-600 transition-colors">Contact Us</a>
                </div>
            </div>
        </footer>
    );
};

export default Footer;*/
// components/layout/Footer.tsx
import React, { useState, useEffect } from 'react';

const Footer: React.FC = () => {
    const [isVisible, setIsVisible] = useState(false);

    useEffect(() => {
        const handleScroll = () => {
            const scrollPosition = window.scrollY + window.innerHeight; // Current scroll position plus viewport height
            const pageHeight = document.documentElement.scrollHeight; // Total height of the document

            // Check if user is within 100px of the bottom
            if (scrollPosition >= pageHeight - 100) {
                setIsVisible(true);
            } else {
                setIsVisible(false);
            }
        };

        // Add scroll event listener
        window.addEventListener('scroll', handleScroll, { passive: true });

        // Initial check in case the page is already at the bottom
        handleScroll();

        // Cleanup event listener
        return () => window.removeEventListener('scroll', handleScroll);
    }, []);

    return (
        <footer
            className={`fixed bottom-0 left-0 right-0 z-50 shadow-md bg-white border-t border-gray-200 py-4 transition-all duration-300 ease-in-out ${
                isVisible ? 'translate-y-0' : 'translate-y-full'
            }`}
        >
            <div className="px-6 flex justify-between items-center text-sm text-gray-600">
                <div>
                    <p>&copy; 2025 TaskFlow. All rights reserved.</p>
                    <p>Internal Task System</p>
                </div>
                <div className="flex space-x-4">
                    <a href="#" className="hover:text-blue-600 transition-colors">Privacy Policy</a>
                    <a href="#" className="hover:text-blue-600 transition-colors">Terms of Service</a>
                    <a href="#" className="hover:text-blue-600 transition-colors">Contact Us</a>
                </div>
            </div>
        </footer>
    );
};

export default Footer;