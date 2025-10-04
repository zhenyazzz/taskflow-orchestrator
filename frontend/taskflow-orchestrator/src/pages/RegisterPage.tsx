// src/pages/RegisterPage.tsx
import React from 'react';
import RegisterForm from '../features/auth/RegisterForm';

const RegisterPage: React.FC = () => {
  return (
    <div className="flex justify-center items-center h-screen bg-gray-100">
      <RegisterForm />
    </div>
  );
};

export default RegisterPage;