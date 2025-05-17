// UpdateUser.js
import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';

const UpdateUser = () => {
  const [userData, setUserData] = useState({
    username: '',
    email: '',
    homeAddress: '',
    password: '',
  });
  const [loading, setLoading] = useState(true);
  const [message, setMessage] = useState('');
  const [error, setError] = useState('');

  const token = localStorage.getItem('token');
  const userId = localStorage.getItem('userId');
  const role = localStorage.getItem('role'); // Assuming role stored here

  const navigate = useNavigate();

  useEffect(() => {
    const fetchUserData = async () => {
      try {
        const res = await fetch(`/users/${userId}`, {
          headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json',
          },
        });

        if (!res.ok) throw new Error('Failed to fetch user');

        const data = await res.json();
        setUserData({
          username: data.username || '',
          email: data.email || '',
          homeAddress: data.homeAddress || '',
          password: '',
        });
      } catch (err) {
        setError(err.message);
      } finally {
        setLoading(false);
      }
    };

    fetchUserData();
  }, [token, userId]);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setUserData((prev) => ({ ...prev, [name]: value }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setMessage('');
    setError('');

    try {
      const res = await fetch(`/users/${userId}`, {
        method: 'PUT',
        headers: {
          'Authorization': `Bearer ${token}`,
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(userData),
      });

      const result = await res.json();

      if (!res.ok) throw new Error(result.message || 'Update failed');

      setMessage(result.message || 'Profile updated successfully!');

      if (result.token) {
        localStorage.setItem('token', result.token);
      }
    } catch (err) {
      setError(err.message);
    }
  };

  // Navigate to Home.js and pass token, role, userId
  const handleGoHome = () => {
    const currentToken = localStorage.getItem('token');
    const currentRole = role;
    const currentUserId = localStorage.getItem('userId');

    navigate('/home', {
      state: {
        token: currentToken,
        role: currentRole,
        userId: currentUserId,
      },
    });
  };

  if (loading) return <p>Loading user data...</p>;

  return (
    <div style={{ maxWidth: '500px', margin: 'auto' }}>
      <h2>Edit Profile</h2>
      {message && <p style={{ color: 'green' }}>{message}</p>}
      {error && <p style={{ color: 'red' }}>{error}</p>}
      <form onSubmit={handleSubmit}>
        <label>
          Username:
          <input
            name="username"
            value={userData.username}
            onChange={handleChange}
            required
          />
        </label>
        <br />

        <label>
          Email:
          <input
            name="email"
            value={userData.email}
            onChange={handleChange}
            required
          />
        </label>
        <br />

        <label>
          Home Address:
          <input
            name="homeAddress"
            value={userData.homeAddress}
            onChange={handleChange}
          />
        </label>
        <br />

        <label>
          New Password:
          <input
            name="password"
            type="password"
            value={userData.password}
            onChange={handleChange}
          />
        </label>
        <br />

        <button type="submit">Update Profile</button>
      </form>
      <br />
      <button onClick={handleGoHome}>Go to Home</button>
    </div>
  );
};

export default UpdateUser;
