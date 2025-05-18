import React, { useEffect, useState, useCallback } from 'react';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';

const Dashboard = () => {
  const [users, setUsers] = useState([]);
  const [currentUser, setCurrentUser] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const navigate = useNavigate();

  const parseJwt = (token) => {
    try {
      return JSON.parse(atob(token.split('.')[1]));
    } catch {
      return null;
    }
  };

  const fetchCurrentUser = useCallback(async (token) => {
    try {
      const response = await axios.get('http://localhost:8080/users/me', {
        headers: { Authorization: `Bearer ${token}` },
      });
      const user = response.data;
      setCurrentUser(user);

      localStorage.setItem('userId', user.userId);
      localStorage.setItem('role', user.role);

      return user;
    } catch (err) {
      throw new Error('Failed to fetch user profile.');
    }
  }, []);

  const fetchAllUsers = useCallback(async (token) => {
    try {
      const response = await axios.get('http://localhost:8080/users', {
        headers: { Authorization: `Bearer ${token}` },
      });
      setUsers(response.data);
    } catch (err) {
      throw new Error('Failed to fetch users list.');
    }
  }, []);

  useEffect(() => {
    const initializeDashboard = async () => {
      const token = localStorage.getItem('token');
      if (!token) {
        setError('No token found. Please log in.');
        setLoading(false);
        return;
      }

      const decoded = parseJwt(token);
      if (decoded?.userId) {
        localStorage.setItem('userId', decoded.userId);
      }

      try {
        const user = await fetchCurrentUser(token);
        if (user.role?.toLowerCase() === 'admin') {
          await fetchAllUsers(token);
        } else {
          setUsers([user]);
        }
      } catch (err) {
        setError(err.message);
      } finally {
        setLoading(false);
      }
    };

    initializeDashboard();
  }, [fetchCurrentUser, fetchAllUsers]);

  const handleDelete = async (userId) => {
    const token = localStorage.getItem('token');
    try {
      await axios.delete(`http://localhost:8080/users/${userId}`, {
        headers: { Authorization: `Bearer ${token}` },
      });
      setUsers((prev) => prev.filter((u) => u.userId !== userId));
    } catch (err) {
      alert('Failed to delete user.');
    }
  };

  const handleLogout = () => {
    localStorage.clear();
    navigate('/login');
  };

  const handleUpdate = () => {
    const userId = localStorage.getItem('userId');
    if (userId) navigate(`/update-user/${userId}`);
    else alert('User ID missing.');
  };

  return (
    <div className="p-6">
      <h1 className="text-2xl font-bold mb-4">Dashboard</h1>

      <button
        onClick={handleUpdate}
        className="mb-4 bg-green-600 text-white px-4 py-2 rounded hover:bg-green-700"
      >
        Update My Profile
      </button>

      {loading ? (
        <p>Loading...</p>
      ) : error ? (
        <div className="text-red-500">{error}</div>
      ) : (
        <table className="min-w-full border-collapse border border-gray-300">
          <thead>
            <tr className="bg-gray-200 text-left">
              <th className="border px-4 py-2">ID</th>
              <th className="border px-4 py-2">First Name</th>
              <th className="border px-4 py-2">Last Name</th>
              <th className="border px-4 py-2">Username</th>
              <th className="border px-4 py-2">Email</th>
              <th className="border px-4 py-2">Phone</th>
              <th className="border px-4 py-2">Address</th>
              <th className="border px-4 py-2">Role</th>
              {currentUser?.role === 'admin' && (
                <th className="border px-4 py-2">Actions</th>
              )}
            </tr>
          </thead>
          <tbody>
            {users.map((user) => (
              <tr key={user.userId}>
                <td className="border px-4 py-2">{user.userId}</td>
                <td className="border px-4 py-2">{user.firstName || '-'}</td>
                <td className="border px-4 py-2">{user.lastName || '-'}</td>
                <td className="border px-4 py-2">{user.username}</td>
                <td className="border px-4 py-2">{user.email}</td>
                <td className="border px-4 py-2">{user.phoneNumber || '-'}</td>
                <td className="border px-4 py-2">{user.homeAddress || '-'}</td>
                <td className="border px-4 py-2">{user.role || '-'}</td>
                {currentUser?.role === 'admin' && (
                  <td className="border px-4 py-2 space-x-2">
                    <button
                      onClick={() => handleDelete(user.userId)}
                      className="bg-red-500 text-white px-3 py-1 rounded hover:bg-red-600"
                    >
                      Delete
                    </button>
                  </td>
                )}
              </tr>
            ))}
          </tbody>
        </table>
      )}

      <div className="mt-4">
        <button
          onClick={handleLogout}
          className="bg-blue-600 text-white px-4 py-2 rounded hover:bg-blue-700"
        >
          Logout
        </button>
      </div>
    </div>
  );
};

export default Dashboard;
