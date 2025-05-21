import React, { useEffect, useState, useCallback } from "react";
import axios from "axios";
import { useNavigate } from "react-router-dom";
import "../styles/styles.css";                //Import external CSS styles

const Dashboard = () => {
  const [users, setUsers] = useState([]);
  const [currentUser, setCurrentUser] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [deleteSuccess, setDeleteSuccess] = useState("");
  const navigate = useNavigate();

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
    <div className="dashboard-center">
      <h1>Dashboard</h1>

      {loading ? (
        <p>Loading...</p>
      ) : error ? (
        <div className="error-message">{error}</div>
      ) : (
        <>
          {deleteSuccess && <div className="success-message">{deleteSuccess}</div>}
          <table className="user-table">
            <thead>
              <tr>
                <th>ID</th>
                <th>First Name</th>
                <th>Last Name</th>
                <th>Username</th>
                <th>Email</th>
                <th>Phone</th>
                <th>Address</th>
                <th>Role</th>
                {currentUser?.role === "ADMIN" && <th>Actions</th>}
              </tr>
            </thead>
            <tbody>
              {users.map((user) => (
                <tr key={user.userId}>
                  <td>{user.userId}</td>
                  <td>{user.firstName || "-"}</td>
                  <td>{user.lastName || "-"}</td>
                  <td>{user.username}</td>
                  <td>{user.email}</td>
                  <td>{user.phoneNumber || "-"}</td>
                  <td>{user.homeAddress || "-"}</td>
                  <td>{user.role || "-"}</td>
                  {currentUser?.role === "ADMIN" && (
                    <td>
                      {user.userId !== currentUser.userId && (
                        <button
                          onClick={() => handleDelete(user.userId)}
                          className="btn"
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

          <div className="buttons-center">
            <button onClick={handleUpdate} className="btn">
              Update My Profile
            </button>
            <button onClick={handleLogout} className="btn">
              Logout
            </button>
          </div>
        </>
      )}
    </div>

);

};

export default Dashboard;
