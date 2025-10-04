// src/features/auth/RegisterForm.tsx
import React from 'react';
import Input from '../../components/Input';
import Button from '../../components/Button';
import Link from '../../components/Link';
import Form from '../../components/Form';

const RegisterForm: React.FC = () => {
  return (
    <Form title="Регистрация">
      <Input label="Имя пользователя" type="text" placeholder="Введите имя пользователя" />
      <Input label="Email" type="email" placeholder="Введите email" />
      <Input label="Пароль" type="password" placeholder="Введите пароль" />
      <Input label="Подтвердите пароль" type="password" placeholder="Подтвердите пароль" />
      {/* Опционально: дополнительные поля, например, отдел */}
      {/* <Input label="Отдел" type="text" placeholder="Введите отдел" /> */}
      <Button>Зарегистрироваться</Button>
      <p className="text-center mt-4">
        Уже есть аккаунт? <Link to="/login">Войти</Link>
      </p>
    </Form>
  );
};

export default RegisterForm;