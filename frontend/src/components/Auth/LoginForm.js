// src/components/Auth/LoginForm.js

import React, { useState } from 'react';
import { loginUser } from '../services/authService';
import { useAuth } from '../../context/AuthContext';
import { useNavigate } from 'react-router-dom';
import InputField from '../InputField';  // Import the InputField component
import ErrorMessage from '../ErrorMessage';  // Import the ErrorMessage component
import Button from '../Button';  // Import the Button component

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

      // Extract role from roleMessage string
      const role = userData.roleMessage.split(':')[1].trim();

      navigate('/home', { state: { role } });
    } catch (err) {
      setError('Invalid credentials, please try again.');
    }
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
        <Button type="submit">Login</Button>  {/* Use the Button component here */}
      </form>
    </div>
  );
};

export default LoginForm;