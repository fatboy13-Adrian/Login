import React, { useEffect, useState, useCallback } from "react";
import axios from "axios";
import { useNavigate } from "react-router-dom";

const Dashboard = () => {
  const [users, setUsers] = useState([]);
  const [currentUser, setCurrentUser] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [deleteSuccess, setDeleteSuccess] = useState("");
  const navigate = useNavigate();

  // Fetch all users for admin
  const fetchAllUsers = async (token) => {
    try {
      const response = await axios.get("http://localhost:8080/users", {
        headers: { Authorization: `Bearer ${token}` },
      });
      setUsers(response.data);
      setError("");
    } catch (err) {
      setError("Failed to fetch users list.");
    }
  };

  // Fetch current logged in user and load users accordingly
  const fetchCurrentUser = useCallback(async (token, isAdmin) => {
    try {
      const response = await axios.get("http://localhost:8080/users/me", {
        headers: { Authorization: `Bearer ${token}` },
      });
      const user = response.data;
      setCurrentUser(user);

      if (isAdmin) {
        await fetchAllUsers(token);
      } else {
        setUsers([user]);
      }

      setError("");
    } catch (err) {
      setError("Failed to fetch user profile.");
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    const token = localStorage.getItem("token");
    const role = localStorage.getItem("role");

    if (!token) {
      setError("No token found. Please log in.");
      setLoading(false);
      return;
    }

    fetchCurrentUser(token, role === "ADMIN");
  }, [fetchCurrentUser]);

  // Delete user handler - only ADMIN allowed, can't delete self
  const handleDelete = async (userId) => {
    if (currentUser?.role !== "ADMIN") {
      alert("Unauthorized action.");
      return;
    }

    if (userId === currentUser.userId) {
      alert("Admins cannot delete their own accounts.");
      return;
    }

    const token = localStorage.getItem("token");

    try {
      await axios.delete(`http://localhost:8080/users/${userId}`, {
        headers: { Authorization: `Bearer ${token}` },
      });

      setDeleteSuccess("User deleted successfully. Refreshing list...");
      await fetchAllUsers(token);
      setTimeout(() => setDeleteSuccess(""), 3000);
    } catch (err) {
      alert("Failed to delete user.");
    }
  };

  const handleLogout = () => {
    localStorage.clear();
    navigate("/login");
  };

  const handleUpdate = () => {
    if (!currentUser) {
      alert("User data not loaded yet.");
      return;
    }
    navigate(`/update-user/${currentUser.userId}`);
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
        <>
          {deleteSuccess && (
            <div className="mb-4 text-green-600 font-semibold">{deleteSuccess}</div>
          )}
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
                {currentUser?.role === "ADMIN" && <th className="border px-4 py-2">Actions</th>}
              </tr>
            </thead>
            <tbody>
              {users.map((user) => (
                <tr key={user.userId}>
                  <td className="border px-4 py-2">{user.userId}</td>
                  <td className="border px-4 py-2">{user.firstName || "-"}</td>
                  <td className="border px-4 py-2">{user.lastName || "-"}</td>
                  <td className="border px-4 py-2">{user.username}</td>
                  <td className="border px-4 py-2">{user.email}</td>
                  <td className="border px-4 py-2">{user.phoneNumber || "-"}</td>
                  <td className="border px-4 py-2">{user.homeAddress || "-"}</td>
                  <td className="border px-4 py-2">{user.role || "-"}</td>
                  {currentUser?.role === "ADMIN" && (
                    <td className="border px-4 py-2">
                      {user.userId !== currentUser.userId && (
                        <button
                          onClick={() => handleDelete(user.userId)}
                          className="bg-red-500 text-white px-3 py-1 rounded hover:bg-red-600"
                        >
                          Delete
                        </button>
                      )}
                    </td>
                  )}
                </tr>
              ))}
            </tbody>
          </table>
        </>
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
