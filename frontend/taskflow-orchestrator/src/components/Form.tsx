// src/components/Form.tsx
import React from 'react';

interface FormProps {
  title: string;
  children: React.ReactNode;
}

const Form: React.FC<FormProps> = ({ title, children }) => {
  return (
    <div className="bg-white p-8 rounded-lg shadow-md w-full max-w-md">
      <h2 className="text-2xl font-bold text-center mb-6">{title}</h2>
      {children}
    </div>
  );
};

export default Form;