import './styles/App.css';
import Header from './components/Header';
import Sidebar from './components/Sidebar';

function App() {
  return (
    <div className='min-h-screen bg-gray-100'>
      <Header onToggleSidebar={() => {}} />
      <div className='flex'>
        <Sidebar />
        <main className='flex-1'>
        </main>
      </div>
    </div>
  );
}

export default App;
