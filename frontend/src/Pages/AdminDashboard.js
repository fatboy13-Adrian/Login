// src/components/AdminDashboard.js
import React, { useEffect, useState } from 'react';
import axios from 'axios';
import { useNavigate } from 'react-router-dom'; // React Router hook

const AdminDashboard = () => {
  const [users, setUsers] = useState([]);
  const [error, setError] = useState('');
  const navigate = useNavigate(); // for routing

  const fetchUsers = async () => {
    try {
      const response = await axios.get('http://localhost:8080/users', {
        withCredentials: true,
      });
      setUsers(response.data);
    } catch (err) {
      setError('Failed to fetch users. ' + (err.response?.data || err.message));
    }
  };

  const deleteUser = async (userId) => {
    try {
      await axios.delete(`http://localhost:8080/users/${userId}`, {
        withCredentials: true,
      });
      setUsers(users.filter((user) => user.userId !== userId));
    } catch (err) {
      alert('Failed to delete user: ' + (err.response?.data || err.message));
    }
  };

  const handleLogout = () => {
    // optional: clear tokens/session/etc. if needed
    navigate('/login'); // redirect to login page
  };

  useEffect(() => {
    fetchUsers();
  }, []);

  return (
    <div className="p-6">
      <h1 className="text-2xl font-bold mb-4">Admin Dashboard - User List</h1>
      {error && <div className="text-red-500 mb-2">{error}</div>}
      <table className="min-w-full border-collapse border border-gray-300">
        <thead>
          <tr className="bg-gray-200 text-left">
            <th className="border border-gray-300 px-4 py-2">ID</th>
            <th className="border border-gray-300 px-4 py-2">Username</th>
            <th className="border border-gray-300 px-4 py-2">Email</th>
            <th className="border border-gray-300 px-4 py-2">Address</th>
            <th className="border border-gray-300 px-4 py-2">Actions</th>
          </tr>
        </thead>
        <tbody>
          {users.map(({ userId, username, email, homeAddress }) => (
            <tr key={userId}>
              <td className="border px-4 py-2">{userId}</td>
              <td className="border px-4 py-2">{username}</td>
              <td className="border px-4 py-2">{email}</td>
              <td className="border px-4 py-2">{homeAddress}</td>
              <td className="border px-4 py-2">
                <button
                  onClick={() => deleteUser(userId)}
                  className="bg-red-500 text-white px-3 py-1 rounded hover:bg-red-600"
                >
                  Delete
                </button>
              </td>
            </tr>
          ))}
          {users.length === 0 && (
            <tr>
              <td colSpan="5" className="text-center py-4 text-gray-500">
                No users found.
              </td>
            </tr>
          )}
        </tbody>
      </table>

      {/* Logout Button */}
      <div className="mt-6 text-center">
        <button
          onClick={handleLogout}
          className="bg-blue-500 text-white px-4 py-2 rounded hover:bg-blue-600"
        >
          Logout
        </button>
      </div>
    </div>
  );
};

export default AdminDashboard;
