import React, { useState } from 'react';
import axios from 'axios';
import Button from '../components/Button';
import InputField from '../components/InputField';
import ErrorMessage from '../components/ErrorMessage';

const API_URL = 'http://localhost:8080/users';

const AdminDashboard = () => {
  const [userId, setUserId] = useState('');
  const [username, setUsername] = useState('');
  const [email, setEmail] = useState('');
  const [homeAddress, setHomeAddress] = useState('');
  const [password, setPassword] = useState('');
  const [role, setRole] = useState('CUSTOMER');
  const [users, setUsers] = useState([]);
  const [error, setError] = useState('');

  const handleCreateUser = async () => {
    try {
      const response = await axios.post(`${API_URL}/create`, {
        username,
        email,
        homeAddress,
        password,
        role
      });
      alert('User created successfully!');
      console.log(response.data);
    } catch (err) {
      setError('Failed to create user');
    }
  };

  const handleGetUser = async () => {
    try {
      const response = await axios.get(`${API_URL}/${userId}`);
      setUsers([response.data]);
    } catch (err) {
      setError('Failed to fetch user');
    }
  };

  const handleGetAllUsers = async () => {
    try {
      const response = await axios.get(API_URL);
      setUsers(response.data);
    } catch (err) {
      setError('Failed to fetch users');
    }
  };

  const handleUpdateUser = async () => {
    try {
      const response = await axios.put(`${API_URL}/update/${userId}`, {
        username,
        email,
        homeAddress,
        password,
        role
      });
      alert('User updated successfully!');
      console.log(response.data);
    } catch (err) {
      setError('Failed to update user');
    }
  };

  const handleDeleteUser = async () => {
    try {
      await axios.delete(`${API_URL}/delete/${userId}`);
      alert('User deleted successfully!');
    } catch (err) {
      setError('Failed to delete user');
    }
  };

  return (
    <div style={{ padding: '2rem', fontFamily: 'Arial, sans-serif' }}>
      <h1>Admin Dashboard</h1>

      <div style={{ marginBottom: '1rem' }}>
        <InputField
          id="userId"
          label="User ID"
          type="text"
          value={userId}
          onChange={(e) => setUserId(e.target.value)}
        />
        <InputField
          id="username"
          label="Username"
          type="text"
          value={username}
          onChange={(e) => setUsername(e.target.value)}
        />
        <InputField
          id="email"
          label="Email"
          type="email"
          value={email}
          onChange={(e) => setEmail(e.target.value)}
        />
        <InputField
          id="homeAddress"
          label="Home Address"
          type="text"
          value={homeAddress}
          onChange={(e) => setHomeAddress(e.target.value)}
        />
        <InputField
          id="password"
          label="Password"
          type="password"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
        />
        <div>
          <label htmlFor="role">Role:</label>
          <select id="role" value={role} onChange={(e) => setRole(e.target.value)} required>
            <option value="CUSTOMER">CUSTOMER</option>
            <option value="ADMIN">ADMIN</option>
          </select>
        </div>
      </div>

      <div style={{ marginBottom: '1rem' }}>
        <Button onClick={handleCreateUser}>Create User</Button>
        <Button onClick={handleGetUser}>Get User</Button>
        <Button onClick={handleGetAllUsers}>Get All Users</Button>
        <Button onClick={handleUpdateUser}>Update User</Button>
        <Button onClick={handleDeleteUser}>Delete User</Button>
      </div>

      <ErrorMessage message={error} />

      <div>
        <h2>User Data</h2>
        {users.length === 0 ? (
          <p>No users found.</p>
        ) : (
          <ul>
            {users.map((user, index) => (
              <li key={index}>{user.username} ({user.email})</li>
            ))}
          </ul>
        )}
      </div>
    </div>
  );
};

export default AdminDashboard;