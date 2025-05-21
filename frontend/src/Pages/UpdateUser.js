import React, { useEffect, useState } from "react";         //React hooks
import { useNavigate, useParams } from "react-router-dom";  //Routing utilities
import axios from "axios";                                  //HTTP client
import "../styles/styles.css";                              //Stylesheet

const UpdateUser = () => 
{
  const { userId } = useParams(); //Extract userId from URL
  const navigate = useNavigate(); //For navigation

  //State to hold form input values
  const [formData, setFormData] = useState({
    firstName: "",
    lastName: "",
    username: "",
    email: "",
    phoneNumber: "",
    homeAddress: "",
    password: "",
    role: "",
  });

  const [currentUser, setCurrentUser] = useState(null); //Authenticated user info
  const [loading, setLoading] = useState(true);         //Loading indicator
  const [error, setError] = useState("");               //Error message

  useEffect(() => 
  {
    const token = localStorage.getItem("token");          //Get auth token
    const storedUserId = localStorage.getItem("userId");  //Get current user ID
    const storedRole = localStorage.getItem("role");      //Get current user role

    //If no token, redirect to login
    if(!token) 
    { 
      alert("You must be logged in to update user info.");
      navigate("/login");
      return;
    }

    //Store current user details
    setCurrentUser({ 
      userId: storedUserId ? parseInt(storedUserId) : null,
      role: storedRole,
    });

    const fetchUserData = async () => 
    {
      try 
      {
        //Fetch user by ID
        const userRes = await axios.get(`/users/${userId}`, { 
          headers: { Authorization: `Bearer ${token}` },  //Include token in header
        });

        const userToEdit = userRes.data;  //Extract user data

        //Populate form with fetched user data
        setFormData((prev) => ({ 
          ...prev,
          ...userToEdit,
          password: "", //Keep password empty
        }));
      } 
      
      catch(err) 
      {
        console.error(err);                     //Log error
        setError("Failed to load user data.");  //Set error state
      } 
      
      finally 
      {
        setLoading(false);  //Stop loading indicator
      }
    };

    fetchUserData();      //Invoke fetch
  }, [userId, navigate]); //Run on userId or navigation change

  const isAdmin = currentUser?.role === "ADMIN" || currentUser?.role === "ROLE_ADMIN"; //Admin check
  const isSelf = currentUser?.userId === parseInt(userId);  //Is editing self?
  const canEdit = isAdmin || isSelf;                        //Can edit if admin or self
  const canEditRole = isAdmin;                              //Only admin can edit role

  const handleChange = (e) => 
  {
    if(!canEdit) return; //Prevent unauthorized input change

    const { name, value } = e.target;

    if(name === "role" && !canEditRole) return; //Block role change if not admin

    //Update form value
    setFormData((prev) => ({ 
      ...prev,
      [name]: value,
    }));
  };

  const handleSubmit = async (e) => 
  {
    e.preventDefault(); //Prevent default form behavior

    //Block submission if unauthorized
    if(!canEdit) 
    { 
      alert("You are not authorized to update this user.");
      return;
    }

    const token = localStorage.getItem("token");  //Get auth token

    //If token is missing
    if(!token) 
    { 
      alert("Authentication token missing. Please log in again.");
      navigate("/login");
      return;
    }

    try 
    {
      const payload = { ...formData };        //Prepare form data

      if(!canEditRole) delete payload.role;  //Remove role if not admin

      //Send update request
      await axios.patch(`/users/${userId}`, payload, 
      { 
        headers: { Authorization: `Bearer ${token}` },  //Include auth header
      });

      alert("User updated successfully. Please log in again."); //Success message
      navigate("/login");                                       //Redirect to login
    } 
    
    catch(err) 
    {
      console.error(err);               //Log error
      alert("Failed to update user.");  //Show error alert
    }
  };

  if(loading)  //Show loading message
    return <p className="success-message" style={{ textAlign: "center" }}>Loading...</p>;

  if(error)   //Show error message
    return (
      <p className="error-message" style={{ textAlign: "center" }}>
        {error}
      </p>
    );

  return (
    <div className="container form-wrapper"> {/* Form container */}
      <h2 className="dashboard-title">Update User</h2>

      {!canEdit && ( //Show error if not authorized
        <p className="error-message" style={{ textAlign: "center" }}>
          You are not authorized to update this user’s profile.
        </p>
      )}

      <form onSubmit={handleSubmit} className="register-form"> {/* Form */}
        {[
          "firstName",
          "lastName",
          "username",
          "email",
          "phoneNumber",
          "homeAddress",
          "password",
        ].map((field) => (
          <div className="form-row" key={field}> {/* Form input row */}
            <label className="form-label" htmlFor={field}> {/* Label */}
              {field.charAt(0).toUpperCase() + field.slice(1)} {/* Capitalize label */}
            </label>
            <input
              className="form-input"
              id={field}
              type={field === "password" ? "password" : "text"} //Set input type
              name={field}
              value={formData[field] || ""} //Bind value to state
              onChange={handleChange} //Handle input change
              disabled={!canEdit} //Disable if unauthorized
              autoComplete={field === "password" ? "new-password" : undefined} //Disable autocomplete for password
            />
          </div>
        ))}

        {canEditRole && ( //Show role field only if admin
          <div className="form-row">
            <label className="form-label" htmlFor="role">Role</label>
            <select
              className="form-input"
              id="role"
              name="role"
              value={formData.role || ""} //Bind role to state
              onChange={handleChange} //Handle role change
              disabled={!canEditRole} //Disable if not admin
            >
              <option value="">Select a role</option>
              <option value="USER">User</option>
              <option value="CUSTOMER">Customer</option>
              <option value="ADMIN">Admin</option>
            </select>
          </div>
        )}

        <div className="form-actions"> {/* Submit button */}
          <button type="submit" className="btn btn-blue" disabled={!canEdit}>
            Update
          </button>
        </div>
      </form>
    </div>
  );
};

export default UpdateUser;  //Export component