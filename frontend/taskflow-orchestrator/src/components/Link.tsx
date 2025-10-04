// src/components/Link.tsx
import React from 'react';

interface LinkProps {
  to: string;
  children: React.ReactNode;
}

const Link: React.FC<LinkProps> = ({ to, children }) => {
  return (
    <a href={to} className="text-blue-500 hover:underline">
      {children}
    </a>
  );
};

export default Link;