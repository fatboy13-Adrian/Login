import React, { useEffect, useState } from 'react';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';

const GetUser = () => {
  const navigate = useNavigate();
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  // Get token from localStorage
  const authToken = localStorage.getItem('token');

  useEffect(() => {
    if (!authToken) {
      setError('No token provided');
      setLoading(false);
      return;
    }

    axios
      .get('http://localhost:8080/users/me', {
        headers: { Authorization: `Bearer ${authToken}` },
      })
      .then((res) => {
        setUser(res.data);
        setError(null);
      })
      .catch(() => {
        setError('Failed to fetch user data');
        setUser(null);
      })
      .finally(() => {
        setLoading(false);
      });
  }, [authToken]);

  const goHome = () => navigate('/home');

  const handleLogout = () => {
    // Clear stored session data on logout
    localStorage.removeItem('token');
    localStorage.removeItem('role');
    localStorage.removeItem('userId');
    navigate('/login');
  };

  if (loading) return <p>Loading user data...</p>;

  if (error)
    return (
      <div>
        <p>Error: {error}</p>
        <button onClick={goHome}>Go to Home</button>
        <button onClick={handleLogout}>Logout</button>
      </div>
    );

  if (!user)
    return (
      <div>
        <p>User data not available.</p>
        <button onClick={goHome}>Go to Home</button>
        <button onClick={handleLogout}>Logout</button>
      </div>
    );

  return (
    <div>
      <h2>User Profile</h2>
      <p>Username: {user.username ?? 'N/A'}</p>
      <p>Role: {user.role ?? 'N/A'}</p>
      <p>Email: {user.email ?? 'N/A'}</p>
      <p>Home Address: {user.homeAddress ?? 'N/A'}</p>
      <button onClick={goHome}>Go to Home</button>
      <button onClick={handleLogout} style={{ marginLeft: '10px' }}>
        Logout
      </button>
    </div>
  );
};

export default GetUser;
