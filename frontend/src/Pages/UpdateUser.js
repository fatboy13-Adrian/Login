import React, { useEffect, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import axios from 'axios';

const UpdateUser = () => {
  const { userId } = useParams();
  const navigate = useNavigate();

  const [formData, setFormData] = useState({
    firstName: '',
    lastName: '',
    username: '',
    email: '',
    phoneNumber: '',
    homeAddress: ''
  });
  const [message, setMessage] = useState('');
  const [error, setError] = useState('');

  // Fetch current user info
  useEffect(() => {
    const fetchUser = async () => {
      try {
        const token = localStorage.getItem('token');
        const res = await axios.get(`http://localhost:8080/users/${userId}`, {
          headers: { Authorization: `Bearer ${token}` }
        });
        setFormData(res.data);
      } catch (err) {
        setError('Failed to load user data.');
      }
    };
    fetchUser();
  }, [userId]);

  const handleChange = (e) => {
    setFormData(prev => ({ ...prev, [e.target.name]: e.target.value }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setMessage('');
    setError('');
    try {
      const token = localStorage.getItem('token');
      const res = await axios.patch(
        `http://localhost:8080/users/${userId}`,
        formData,
        {
          headers: { Authorization: `Bearer ${token}` }
        }
      );

      // Expecting backend to return a new token and updated userId
      const newToken = res.data.token;
      const newUserId = res.data.userId || userId;

      if (newToken) {
        localStorage.setItem('token', newToken);
        localStorage.setItem('userId', newUserId);
      }

      setMessage('Profile updated successfully! Redirecting to Dashboard...');
      setTimeout(() => {
        navigate('/dashboard', {
          state: {
            newToken,
            userId: newUserId
          }
        });
      }, 5000);
    } catch (err) {
      console.error(err);
      setError('Update failed. Please try again.');
    }
  };

  return (
    <div className="p-6">
      <h2 className="text-xl font-bold mb-4">Update My Profile</h2>
      {message && <p className="text-green-600 mb-2">{message}</p>}
      {error && <p className="text-red-600 mb-2">{error}</p>}
      <form onSubmit={handleSubmit} className="grid gap-4">
        <input name="firstName" value={formData.firstName || ''} onChange={handleChange} placeholder="First Name" className="border px-3 py-2" />
        <input name="lastName" value={formData.lastName || ''} onChange={handleChange} placeholder="Last Name" className="border px-3 py-2" />
        <input name="username" value={formData.username || ''} onChange={handleChange} placeholder="Username" className="border px-3 py-2" />
        <input name="email" value={formData.email || ''} onChange={handleChange} placeholder="Email" className="border px-3 py-2" />
        <input name="phoneNumber" value={formData.phoneNumber || ''} onChange={handleChange} placeholder="Phone Number" className="border px-3 py-2" />
        <input name="homeAddress" value={formData.homeAddress || ''} onChange={handleChange} placeholder="Home Address" className="border px-3 py-2" />
        <button type="submit" className="bg-blue-600 text-white py-2 px-4 rounded hover:bg-blue-700">Update</button>
      </form>
    </div>
  );
};

export default UpdateUser;
