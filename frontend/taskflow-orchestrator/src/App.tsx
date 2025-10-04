// src/App.tsx (пример с роутингом, предполагая использование react-router-dom)
import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import LoginPage from './pages/LoginPage';
import RegisterPage from './pages/RegisterPage';
import Layout from './components/layouts/Layout';
import WelcomePage from './pages/WelcomePage';

const App: React.FC = () => {
  return (
    <Router>
      <Routes>
      <Route path="/" element={<Layout><WelcomePage /></Layout>} />
        <Route path="/login" element={<LoginPage />} />
        <Route path="/register" element={<RegisterPage />} />
      </Routes>
    </Router>
  );
};

export default App;