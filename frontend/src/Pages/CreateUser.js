import React, { useState } from "react";        //Import React and useState hook
import axios from "axios";                      //Import axios for HTTP requests
import { useNavigate } from "react-router-dom"; //Import useNavigate for navigation
import "../styles/styles.css";                  //Import CSS styles

function CreateUser() 
{
  const navigate = useNavigate(); //Hook for programmatic navigation

  //State to hold user input fields
  const [user, setUser] = useState({
    firstName: "",
    lastName: "",
    username: "",
    email: "",
    phoneNumber: "",
    homeAddress: "",
    password: "",
    role: "",
  });

  const [error, setError] = useState(null);       //Error message state
  const [success, setSuccess] = useState(false);  //Success flag state
  const [loading, setLoading] = useState(false);  //Loading flag state

  //Update user state when any input changes
  const onInputChange = (e) => 
  {
    const { name, value } = e.target;
    setUser((prevUser) => ({ ...prevUser, [name]: value }));
  };

  //Handle form submission asynchronously
  const onSubmit = async (e) => 
  {
    e.preventDefault(); //Prevent default form submit behavior
    setError(null);     //Clear previous errors
    setSuccess(false);  //Reset success flag

    //Create trimmed user data, excluding password trimming
    const trimmedUser = 
    {
      firstName: user.firstName.trim(),
      lastName: user.lastName.trim(),
      username: user.username.trim(),
      email: user.email.trim(),
      phoneNumber: user.phoneNumber.trim(),
      homeAddress: user.homeAddress.trim(),
      password: user.password,
      role: user.role,
    };

    setLoading(true); //Show loading spinner/state

    try 
    {
      await axios.post("http://localhost:8080/users", trimmedUser); //Send POST request to API
      setSuccess(true);                                             //Set success flag on successful registration
      setTimeout(() => navigate("/"), 5000);                        //Redirect after 5 seconds
    } 
    
    catch(err) 
    {
      //Handle errors from API or network
      if(err.response) 
      {
        if(err.response.status === 409) 
          setError("User with this email or username already exists.");           //Conflict error
        
        else if(err.response.data?.message)
          setError(err.response.data.message);                                    //Show backend error message if available
        
        else
          setError(`Error: ${err.response.status} - ${err.response.statusText}`); //Other HTTP errors
      } 
      
      else 
        setError("Network error. Please check your connection."); //Network error fallback
    } 
    
    finally 
    {
      setLoading(false);  //Turn off loading state
    }
  };

  //Define metadata for form fields
  const fields = [
    { name: "firstName", label: "First Name", type: "text", required: true, maxLength: 30, minLength: 2, autoComplete: "given-name" },
    { name: "lastName", label: "Last Name", type: "text", required: true, maxLength: 30, minLength: 2, autoComplete: "family-name" },
    { name: "username", label: "Username", type: "text", required: true, maxLength: 20, minLength: 4, autoComplete: "username" },
    { name: "email", label: "Email", type: "email", required: true, minLength: 5, autoComplete: "email" },
    { name: "phoneNumber", label: "Phone Number", type: "tel", required: false, maxLength: 15, minLength: 7, autoComplete: "tel" },
    { name: "homeAddress", label: "Home Address", type: "text", required: false, maxLength: 50, autoComplete: "street-address" },
    { name: "password", label: "Password", type: "password", required: true, minLength: 6, maxLength: 50, autoComplete: "new-password" },
  ];

  return (
    <div className="container"> {/* Container div */}
      <div className="form-wrapper"> {/* Wrapper for form */}
        <h2 className="text-center">Register User</h2> {/* Form title */}

        {error && <div className="alert alert-danger">{error}</div>} {/* Display error message */}
        {success && <div className="alert alert-success">Registration successful! Redirecting...</div>} {/* Display success message */}

        <form onSubmit={onSubmit}> {/* Form element with submit handler */}
          {fields.map(({ name, label, type, required, maxLength, minLength, autoComplete }) => (
            <div className="form-row" key={name}> {/* Form row for each input */}
              <label htmlFor={name}>{label}</label> {/* Input label */}
              <input
                type={type} //Input type
                name={name} //Input name
                id={name} //Input id
                value={user[name]} //Controlled value from state
                onChange={onInputChange} //Input change handler
                required={required} //Required attribute
                placeholder={`Enter your ${label.toLowerCase()}`} //Placeholder text
                disabled={loading || success} //Disable input during loading or after success
                minLength={minLength} //Minimum input length
                maxLength={maxLength} //Maximum input length
                autoComplete={autoComplete} //Autocomplete attribute
              />
            </div>
          ))}

          <div className="form-row"> {/* Role selection */}
            <label htmlFor="role">Role</label>
            <select
              name="role"
              id="role"
              value={user.role}
              onChange={onInputChange}
              required
              disabled={loading || success}
            >
              <option value="">Select role</option>
              <option value="ADMIN">Admin</option>
              <option value="CUSTOMER">Customer</option>
              <option value="USER">User</option>
            </select>
          </div>

          <div className="form-actions"> {/* Submit button container */}
            <button type="submit" className="btn" disabled={loading || success}>
              {loading ? "Registering..." : "Register"} {/* Button text changes based on loading state */}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}

export default CreateUser;  //Export component