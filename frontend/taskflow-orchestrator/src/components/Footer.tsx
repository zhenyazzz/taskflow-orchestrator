import React, { useState, useEffect, type RefObject } from 'react';

interface FooterProps {
    scrollContainerRef: RefObject<HTMLElement>;
}

const Footer: React.FC<FooterProps> = ({ scrollContainerRef }) => {
    const [isVisible, setIsVisible] = useState(false);

    useEffect(() => {
        const handleScroll = () => {
            const scrollContainer = scrollContainerRef.current;
            if (!scrollContainer) return;

            const scrollPosition = scrollContainer.scrollTop + scrollContainer.clientHeight; // Current scroll position plus viewport height
            const contentHeight = scrollContainer.scrollHeight; // Total height of the scrollable content

            // Determine if the page is actually scrollable within the container
            const isPageScrollable = contentHeight > scrollContainer.clientHeight;

            if (isPageScrollable) {
                // If scrollable, show only when at the very bottom (with tolerance)
                if (scrollPosition >= contentHeight - 1) {
                    setIsVisible(true);
                } else {
                    setIsVisible(false);
                }
            } else {
                // If not scrollable, footer should be hidden
                setIsVisible(false);
            }
        };

        const scrollContainer = scrollContainerRef.current;
        if (scrollContainer) {
            scrollContainer.addEventListener('scroll', handleScroll, { passive: true });
            handleScroll(); // Initial check in case the page is already at the bottom or not scrollable
        }

        // Cleanup event listener
        return () => {
            if (scrollContainer) {
                scrollContainer.removeEventListener('scroll', handleScroll);
            }
        };
    }, [scrollContainerRef]);

    return (
        <footer
            className={`fixed bottom-0 left-0 right-0 z-50 shadow-md bg-white border-t border-gray-200 py-4 transition-all duration-300 ease-in-out ${
                isVisible ? 'translate-y-0' : 'translate-y-[100%]'
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