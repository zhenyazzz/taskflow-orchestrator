import React from 'react';
import './styles/App.css';
import Header from './components/Header';
import Sidebar from './components/Sidebar';
import Dashboard from './pages/Dashboard';

function App() {
  return (
    <div className='min-h-screen bg-gray-100'>
      <Header />
      <div className='flex'>
        <Sidebar />
        <main className='flex-1'>
          <Dashboard />
        </main>
      </div>
    </div>
  );
}

export default App;
