// src/App.tsx (пример с роутингом, предполагая использование react-router-dom)
import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import LoginPage from './pages/LoginPage';
import RegisterPage from './pages/RegisterPage';
import Layout from './components/layouts/Layout';
// Импортируй другие страницы по мере необходимости

const App: React.FC = () => {
  return (
    <Router>
      <Routes>
        <Route path="/login" element={<LoginPage />} />
        <Route path="/register" element={<RegisterPage />} />
        <Route path="/" element={<Layout>

        </Layout>} />
      </Routes>
    </Router>
  );
};

export default App;