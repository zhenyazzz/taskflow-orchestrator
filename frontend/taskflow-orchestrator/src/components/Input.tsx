// src/components/Input.tsx
import React from 'react';

interface InputProps {
  label: string;
  type: string;
  placeholder: string;
}

const Input: React.FC<InputProps> = ({ label, type, placeholder }) => {
  return (
    <div className="mb-4">
      <label className="block mb-2 text-sm font-medium">{label}</label>
      <input
        type={type}
        placeholder={placeholder}
        className="w-full px-4 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
      />
    </div>
  );
};

export default Input;