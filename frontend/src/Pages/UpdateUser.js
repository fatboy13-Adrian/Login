import React, { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import axios from 'axios';

const UpdateUser = () => {
  const { userId } = useParams();
  const navigate = useNavigate();
  const [userData, setUserData] = useState({});
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [role, setRole] = useState(null);

  // Helper to parse JWT and get role (if stored there)
  const parseJwt = (token) => {
    try {
      return JSON.parse(atob(token.split('.')[1]));
    } catch {
      return null;
    }
  };

  useEffect(() => {
    const fetchUser = async () => {
      try {
        const token = localStorage.getItem('token');
        if (!token) {
          setError('No token found. Please login.');
          setLoading(false);
          return;
        }

        // Parse token to get role
        const payload = parseJwt(token);
        if (payload?.role) setRole(payload.role);

        // Fetch user data from API
        const res = await axios.get(`http://localhost:8080/users/${userId}`, {
          headers: { Authorization: `Bearer ${token}` },
        });
        setUserData(res.data);
      } catch (err) {
        setError('Failed to load user data');
      } finally {
        setLoading(false);
      }
    };
    fetchUser();
  }, [userId]);

  const handleChange = (e) => {
    setUserData({ ...userData, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      const token = localStorage.getItem('token');
      await axios.put(`http://localhost:8080/users/${userId}`, userData, {
        headers: { Authorization: `Bearer ${token}` },
      });

      alert('Profile updated successfully!');

      // Redirect based on role
      if (role === 'admin') {
        navigate('/admin-dashboard');
      } else {
        navigate('/get-user'); // Assuming this route shows normal user profile
      }
    } catch (err) {
      alert('Update failed');
    }
  };

  if (loading) return <p>Loading...</p>;
  if (error) return <p className="text-red-600">{error}</p>;

  return (
    <form onSubmit={handleSubmit} className="p-6 max-w-md mx-auto">
      <h2 className="text-xl font-bold mb-4">Update Profile</h2>

      <label className="block mb-2">
        First Name:
        <input
          type="text"
          name="firstName"
          value={userData.firstName || ''}
          onChange={handleChange}
          className="border p-2 w-full"
        />
      </label>

      <label className="block mb-2">
        Last Name:
        <input
          type="text"
          name="lastName"
          value={userData.lastName || ''}
          onChange={handleChange}
          className="border p-2 w-full"
        />
      </label>

      <label className="block mb-2">
        Email:
        <input
          type="email"
          name="email"
          value={userData.email || ''}
          onChange={handleChange}
          className="border p-2 w-full"
        />
      </label>

      <label className="block mb-2">
        Phone Number:
        <input
          type="text"
          name="phoneNumber"
          value={userData.phoneNumber || ''}
          onChange={handleChange}
          className="border p-2 w-full"
        />
      </label>

      <label className="block mb-2">
        Address:
        <input
          type="text"
          name="homeAddress"
          value={userData.homeAddress || ''}
          onChange={handleChange}
          className="border p-2 w-full"
        />
      </label>

      {/* Optionally, disable or hide role editing for normal users */}
      <label className="block mb-4">
        Role:
        <input
          type="text"
          name="role"
          value={userData.role || ''}
          onChange={handleChange}
          className="border p-2 w-full"
          disabled={role !== 'admin'} // Only admins can change role
        />
      </label>

      <button
        type="submit"
        className="bg-blue-600 text-white px-4 py-2 rounded hover:bg-blue-700"
      >
        Update Profile
      </button>
    </form>
  );
};

export default UpdateUser;
