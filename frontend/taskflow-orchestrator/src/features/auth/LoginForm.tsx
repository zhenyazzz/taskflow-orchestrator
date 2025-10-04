// src/features/auth/LoginForm.tsx
import React from 'react';
import Input from '../../components/Input';
import Button from '../../components/Button';
import Link from '../../components/Link';
import Form from '../../components/Form';

const LoginForm: React.FC = () => {
  return (
    <Form title="Вход в систему">
      <Input label="Email" type="email" placeholder="Введите email" />
      <Input label="Пароль" type="password" placeholder="Введите пароль" />
      <div className="flex justify-between mb-4">
        <label className="flex items-center">
          <input type="checkbox" className="mr-2" /> Запомнить меня
        </label>
        <Link to="/forgot-password">Забыли пароль?</Link>
      </div>
      <Button>Войти</Button>
      <p className="text-center mt-4">
        Нет аккаунта? <Link to="/register">Зарегистрироваться</Link>
      </p>
    </Form>
  );
};

export default LoginForm;