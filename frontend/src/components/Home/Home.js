import React from 'react';
import { useNavigate } from 'react-router-dom';

const Home = () => {
  const navigate = useNavigate();

  const token = localStorage.getItem('token');
  const role = localStorage.getItem('role') || 'User';
  const userId = localStorage.getItem('userId');

  const handleLogout = () => {
    localStorage.removeItem('token');
    localStorage.removeItem('role');
    localStorage.removeItem('userId');
    navigate('/login');
  };

  const handleUpdateProfile = () => {
    // Navigate to /update-user passing token, role, userId in state
    navigate('/update-user', { state: { token, role, userId } });
  };

  return (
    <div className="p-6">
      <h1>Welcome to {role} home page</h1>

      <button
        onClick={() => navigate('/get-user')}
        className="bg-green-500 text-white px-4 py-2 rounded hover:bg-green-600 mr-4"
      >
        View Profile
      </button>

      <button
        onClick={handleUpdateProfile}
        className="bg-yellow-500 text-white px-4 py-2 rounded hover:bg-yellow-600 mr-4"
      >
        Update My Profile
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
