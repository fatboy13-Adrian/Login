import React from 'react';
import { useLocation, useNavigate } from 'react-router-dom';

const Home = () => {
  const location = useLocation();
  const navigate = useNavigate();
  const { role } = location.state || { role: 'user' };

  const handleLogout = () => {
    // Add any session cleanup here if needed
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
