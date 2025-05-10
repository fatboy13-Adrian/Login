import React, { useState } from 'react';
import { loginUser } from '../services/authService';
import { useAuth } from '../../context/AuthContext';
import { useNavigate } from 'react-router-dom';

const LoginForm = () => {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const { login } = useAuth();
  const navigate = useNavigate();

  // Submit handler with async error handling
  const handleSubmit = async (e) => {
  e.preventDefault();
  setError('');

  try {
    const userData = await loginUser(username, password);
    login(userData);
    console.log(userData);

    // extract role from roleMessage string
    const role = userData.roleMessage.split(':')[1].trim(); // e.g., "ADMIN"

    navigate('/home', { state: { role } });
  } catch (err) {
    setError('Invalid credentials, please try again.');
  }
};

  return (
    <div>
      <h2>Login</h2>
      <form onSubmit={handleSubmit}>
        <div>
          <label htmlFor="username">Username:</label>
          <input
            type="text"
            id="username"
            value={username}
            onChange={(e) => setUsername(e.target.value)}
            required
          />
        </div>
        <div>
          <label htmlFor="password">Password:</label>
          <input
            type="password"
            id="password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            required
          />
        </div>
        {error && <div style={{ color: 'red' }}>{error}</div>}
        <button type="submit">Login</button>
      </form>
    </div>
  );
};

export default LoginForm;
