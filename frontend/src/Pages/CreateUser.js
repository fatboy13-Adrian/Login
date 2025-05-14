import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom'; // ✅ Step 1

function CreateUser() {
  const navigate = useNavigate(); // ✅ Step 2

  const [formData, setFormData] = useState({
    username: '',
    email: '',
    homeAddress: '',
    password: '',
    role: '' 
  });

  const [message, setMessage] = useState('');

  const handleChange = (e) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value
    });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    const response = await fetch('http://localhost:8080/users', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(formData)
    });

    if (response.ok) {
      setMessage('User created successfully!');
      setFormData({ username: '', email: '', homeAddress: '', password: '', role: '' });

      // ✅ Step 3: Redirect after short delay or immediately
      setTimeout(() => navigate('/login'), 1000); // adjust path if needed
    } else {
      const errorData = await response.json();
      setMessage(`Failed to create user: ${errorData.message || 'Unknown error'}`);
    }
  };

  return (
    <div>
      <h2>Create User</h2>
      <form onSubmit={handleSubmit}>
        <div>
          <label htmlFor="username">Username: </label>
          <input
            id="username"
            name="username"
            placeholder="Enter your username"
            value={formData.username}
            onChange={handleChange}
            required
          />
        </div>
        <br />

        <div>
          <label htmlFor="email">Email: </label>
          <input
            id="email"
            name="email"
            type="email"
            placeholder="Enter your email"
            value={formData.email}
            onChange={handleChange}
            required
          />
        </div>
        <br />

        <div>
          <label htmlFor="homeAddress">Home Address: </label>
          <input
            id="homeAddress"
            name="homeAddress"
            placeholder="Enter your home address"
            value={formData.homeAddress}
            onChange={handleChange}
            required
          />
        </div>
        <br />

        <div>
          <label htmlFor="password">Password: </label>
          <input
            id="password"
            name="password"
            type="password"
            placeholder="Enter your password"
            value={formData.password}
            onChange={handleChange}
            required
          />
        </div>
        <br />

        <div>
          <label htmlFor="role">Role: </label>
          <select
            id="role"
            name="role"
            value={formData.role}
            onChange={handleChange}
            required
          >
            <option value="">Select Role</option>
            <option value="ADMIN">Admin</option>
            <option value="CUSTOMER">Customer</option>
            <option value="WAREHOUSE">Warehouse</option>
            <option value="SALES">Sales</option>
          </select>
        </div>
        <br />

        <button type="submit">Create User</button>
      </form>

      {message && <p>{message}</p>}
    </div>
  );
}

export default CreateUser;
