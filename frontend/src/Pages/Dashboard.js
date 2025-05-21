import React, { useEffect, useState, useCallback } from "react";  //Import React hooks
import axios from "axios";                                        //Import axios for HTTP requests
import { useNavigate } from "react-router-dom";                   //Import navigation hook
import "../styles/styles.css";                                    //Import styles

//Component for Update/Delete buttons per user
const UserActions = ({ user, currentUser, onUpdate, onDelete, disabled }) => 
{
  //Allow update if admin or the user themselves
  const canUpdate = currentUser.role === "ADMIN" || user.userId === currentUser.userId;
  
  //Allow delete only if admin and not deleting themselves
  const canDelete = currentUser.role === "ADMIN" && user.userId !== currentUser.userId;

  return (
    <div className="action-buttons">
      {/* Update button */}
      <button
        className="btn"
        onClick={() => onUpdate(user)}
        disabled={disabled || !canUpdate}
      >
        Update
      </button>

      {/* Conditionally show Delete button */}
      {canDelete && (
        <button
          className="btn delete-btn"
          onClick={() => {
            //Confirm before deleting user
            if (window.confirm(`Are you sure you want to delete user ${user.username}?`)) {
              onDelete(user.userId);
            }
          }}
          disabled={disabled}
          style={{ marginLeft: "8px" }}
        >
          Delete
        </button>
      )}
    </div>
  );
};

//Table component displaying user list with actions
const UserTable = ({ users, currentUser, onUpdate, onDelete, actionsDisabled }) => (
  <table className="user-table">
    <thead>
      <tr>
        <th>ID</th><th>First Name</th><th>Last Name</th><th>Username</th>
        <th>Email</th><th>Phone</th><th>Address</th><th>Role</th><th>Actions</th>
      </tr>
    </thead>
    <tbody>
      {/* Map over users to display rows */}
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
          <td>
            {/* User action buttons */}
            <UserActions
              user={user}
              currentUser={currentUser}
              onUpdate={onUpdate}
              onDelete={onDelete}
              disabled={actionsDisabled}
            />
          </td>
        </tr>
      ))}
    </tbody>
  </table>
);

//Main dashboard component
const Dashboard = () => 
{
  //State hooks
  const [users, setUsers] = useState([]);                         //All users list
  const [currentUser, setCurrentUser] = useState(null);           //Logged-in user data
  const [loading, setLoading] = useState(true);                   //Loading indicator
  const [error, setError] = useState("");                         //Error message
  const [deleteSuccess, setDeleteSuccess] = useState("");         //Delete success message
  const [actionsDisabled, setActionsDisabled] = useState(false);  //Disable buttons while actions pending
  const navigate = useNavigate();                                 //React Router navigation

  //Fetch all users from backend (admin only)
  const fetchAllUsers = useCallback(async (token, isMounted) => 
  {
    try 
    {
      const res = await axios.get("http://localhost:8080/users", {
        headers: { Authorization: `Bearer ${token}` },
      });
      
      if(isMounted) 
      {
        setUsers(res.data); //Update users list
        setError("");       //Clear errors
      }
    } 
    
    catch(err) 
    {
      if(isMounted) 
        setError(err.response?.data?.message || "Failed to fetch users list."); //Set error on failure
    }
  }, []);

  //Fetch current user profile and then fetch all users if admin
  const fetchCurrentUser = useCallback(
    async (token, isAdmin, isMounted) => 
    {
      try 
      {
        const res = await axios.get("http://localhost:8080/users/me", {
          headers: { Authorization: `Bearer ${token}` },
        });
        
        if(!isMounted) return;

        const user = res.data;
        setCurrentUser(user); //Set current user state

        //Store user info in localStorage
        localStorage.setItem("userId", user.userId);
        localStorage.setItem("role", user.role);

        if(isAdmin)
          await fetchAllUsers(token, isMounted);  //Fetch all users if admin
        
        else 
          setUsers([user]); //Otherwise just current user
        
        setError("");       //Clear error
      } 
      
      catch (err) 
      {
        if(!isMounted) return;

        //Redirect to login if unauthorized
        if(err.response?.status === 401) 
        {
          localStorage.clear();
          navigate("/login");
        } 
        
        else 
          setError(err.response?.data?.message || "Failed to fetch user profile."); //Show error
      } 
      
      finally 
      {
        if(isMounted) setLoading(false);  //Stop loading
      }
    },
    [fetchAllUsers, navigate]
  );

  //Initial data fetch on mount
  useEffect(() => 
  {
    let isMounted = true;

    const token = localStorage.getItem("token");  //Get token from storage
    const role = localStorage.getItem("role");    //Get role from storage

    if(!token) 
    {
      setError("No token found. Please log in.");
      setLoading(false);
      return;
    }

    fetchCurrentUser(token, role === "ADMIN", isMounted); //Fetch user data

    return () => 
    {
      isMounted = false;  //Cleanup to avoid state update on unmounted
    };
  }, [fetchCurrentUser]);

  //Handler to delete a user by id
  const handleDelete = async (userId) => 
  {
    const token = localStorage.getItem("token");

    if(!token) 
    {
      alert("Session expired. Please log in again.");
      navigate("/login");
      return;
    }

    setActionsDisabled(true); //Disable actions during delete

    try 
    {
      await axios.delete(`http://localhost:8080/users/${userId}`, {
        headers: { Authorization: `Bearer ${token}` },
      });

      setDeleteSuccess("User deleted successfully. Refreshing list...");  //Show success
      await fetchAllUsers(token, true);                                   //Refresh user list
      setTimeout(() => setDeleteSuccess(""), 3000);                       //Clear success after delay
    } 
    
    catch(err) 
    {
      alert(err.response?.data?.message || "Failed to delete user."); //Show error alert
    } 
    
    finally 
    {
      setActionsDisabled(false);  //Re-enable actions
    }
  };

  //Handler to navigate to user update page
  const handleUpdate = (user) => 
  {
    const token = localStorage.getItem("token");
    if(!token) 
    {
      alert("Session expired. Please log in again.");
      navigate("/login");
      return;
    }

    //Store update user info in localStorage
    localStorage.setItem("updateUserId", user.userId);
    localStorage.setItem("updateUserRole", user.role);
    localStorage.setItem("token", token); //Token already saved

    navigate(`/update-user/${user.userId}`); //Redirect to update page
  };

  //Logout handler
  const handleLogout = () => 
  {
    localStorage.clear(); //Clear storage
    navigate("/login");   //Redirect to login page
  };

  return (
    <div className="dashboard-center">
      <h1>Dashboard</h1>

      {/* Show loading, error or main content */}
      {loading ? (
        <p>Loading...</p>
      ) : error ? (
        <div className="error-message" role="alert">
          {error}
        </div>
      ) : (
        <>
          {/* Show delete success message */}
          {deleteSuccess && (
            <div className="success-message" role="status">
              {deleteSuccess}
            </div>
          )}

          {/* Display user table */}
          <UserTable
            users={users}
            currentUser={currentUser}
            onUpdate={handleUpdate}
            onDelete={handleDelete}
            actionsDisabled={actionsDisabled}
          />

          {/* Logout button */}
          <div className="buttons-center" style={{ marginTop: "20px" }}>
            <button className="btn" onClick={handleLogout}>
              Logout
            </button>
          </div>
        </>
      )}
    </div>
  );
};

export default Dashboard; //Export dashboard component