// src/components/Auth/LoginForm.js
import React, { useState } from 'react';
import { loginUser } from '../services/authService';
import { useAuth } from '../../context/AuthContext';
import { useNavigate } from 'react-router-dom';
import InputField from '../InputField';
import ErrorMessage from '../ErrorMessage';
import Button from '../Button';

const LoginForm = () => {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const { login } = useAuth();
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');

    try {
      const userData = await loginUser(username, password);
      login(userData);

      const role = userData.roleMessage?.split(':')[1]?.trim().toUpperCase();

      if (role === 'ADMIN') {
        navigate('/admin');
      } else {
        navigate('/home', { state: { role } });
      }
    } catch (err) {
      setError('Invalid credentials, please try again.');
    }
  };

  const handleNavigateToRegister = () => {
    navigate('/create-user');
  };

  return (
    <div>
      <h2>Login</h2>
      <form onSubmit={handleSubmit}>
        <InputField
          id="username"
          label="Username"
          type="text"
          value={username}
          onChange={(e) => setUsername(e.target.value)}
          required
        />
        <InputField
          id="password"
          label="Password"
          type="password"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
          required
        />
        <ErrorMessage message={error} />
        <Button type="submit">Login</Button>
      </form>
      <div style={{ marginTop: '1rem' }}>
        <Button type="button" onClick={handleNavigateToRegister}>
          Register New User
        </Button>
      </div>
      <div style={{ marginTop: '1rem' }}>
        <Button type="button" onClick={handleNavigateToRegister}>
          Forgot My Password
        </Button>
      </div>
    </div>
  );
};

export default LoginForm;