import React from 'react';
import { useNavigate } from 'react-router-dom';

const Home = () => {
  const navigate = useNavigate();

  // Get role from localStorage, fallback to 'User' if missing
  const role = localStorage.getItem('role') || 'User';

  const handleLogout = () => {
    // Clear stored session data
    localStorage.removeItem('token');
    localStorage.removeItem('role');
    localStorage.removeItem('userId');
    navigate('/login');
  };

  return (
    <div className="p-6">
      <h1>Welcome to {role} home page</h1>
      <button
        onClick={() => navigate('/get-user')}
        className="bg-green-500 text-white px-4 py-2 rounded hover:bg-green-600"
      >
        View Profile
      </button>
      <button
        onClick={handleLogout}
        className="mt-4 bg-blue-500 text-white px-4 py-2 rounded hover:bg-blue-600"
      >
        Logout
      </button>
    </div>
  );
};

export default Home;
