import React, {useState} from "react";        //Import React and useState hook for managing state
import axios from "axios";                    //Import axios for making HTTP requests
import {useNavigate} from "react-router-dom"; //Import useNavigate for programmatic navigation
import "../styles/styles.css";                //Import external CSS styles

function CreateUser() 
{
  const navigate = useNavigate();             //Initialize navigate function to redirect user after registration

  //State for holding user input fields
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

  //State to hold error messages, success status, and loading status
  const [error, setError] = useState(null);
  const [success, setSuccess] = useState(false);
  const [loading, setLoading] = useState(false);

  //Update user state when any input changes
  const onInputChange = (e) => 
  {
    setUser({...user, [e.target.name]: e.target.value}); //Use computed property name to update specific field
  };

  //Handle form submission
  const onSubmit = async (e) => 
  {
    e.preventDefault(); //Prevent default form submit behavior (page reload)
    setError(null); //Reset error message
    setSuccess(false); //Reset success message

    //Trim whitespace from string inputs before sending
    const trimmedUser = 
    {
      firstName: user.firstName.trim(),
      lastName: user.lastName.trim(),
      username: user.username.trim(),
      email: user.email.trim(),
      phoneNumber: user.phoneNumber.trim(),
      homeAddress: user.homeAddress.trim(),
      password: user.password, //Password is not trimmed to preserve exact input
      role: user.role,
    };

    setLoading(true); //Show loading state

    try 
    {
      //Send POST request to backend API to create new user
      await axios.post("http://localhost:8080/users", trimmedUser);
      setSuccess(true); //Show success message on completion

      //Redirect to homepage after 5 seconds
      setTimeout(() => navigate("/"), 5000);
    } 
    
    catch (err) 
    {
      //Handle error responses
      if(err.response) 
      {
        //Conflict error - user already exists
        if(err.response.status === 409) 
        {
          setError("User with this email or username already exists.");
        } 
        
        //Other backend-provided error messages
        else if(err.response.data?.message) 
        {
          setError(err.response.data.message);
        } 

        //Generic HTTP error message
        else 
        {
          setError(`Error: ${err.response.status} - ${err.response.statusText}`);
        }
      }

      //Network or other errors without response
      else 
      {
        setError("Network error. Please check your connection.");
      }
    } 
    
    finally 
    {
      setLoading(false); //Reset loading state regardless of success or error
    }
  };

  return (
    <div className="container">
      <div className="form-wrapper">
        <h2 className="text-center">Register User</h2>

        {/*Show error message if error exists*/}
        {error && <div className="alert alert-danger">{error}</div>}
        
        {/*Show success message if registration succeeded*/}
        {success && (
          <div className="alert alert-success">
            Registration successful! Redirecting...
          </div>
        )}

        <form onSubmit={onSubmit}>
          {/*Map through form fields to generate input elements*/}
          {[
            {name: "firstName", label: "First Name", type: "text", required: true},
            {name: "lastName", label: "Last Name", type: "text", required: true},
            {name: "username", label: "Username", type: "text", required: true},
            {name: "email", label: "Email", type: "email", required: true},
            {name: "phoneNumber", label: "Phone Number", type: "tel"},
            {name: "homeAddress", label: "Home Address", type: "text"},
            {name: "password", label: "Password", type: "password", required: true},
          ].map(({name, label, type, required}) => (
            <div className="form-row" key={name}>
              <label htmlFor={name}>{label}</label>
              <input type={type} name={name} id={name} value={user[name]} onChange={onInputChange} required={required} placeholder={`Enter your ${label.toLowerCase()}`}
              disabled={loading || success} minLength={name === "password" ? 6 : 4} maxLength={name === "firstName" ? 30 : 20}/>
            </div>
          ))}

          {/*Role select dropdown*/}
          <div className="form-row">
            <label htmlFor="role">Role</label>
            <select name="role" id="role" value={user.role} onChange={onInputChange} requireddisabled={loading || success}>
              <option value="">Select role</option>
              <option value="ADMIN">Admin</option>
              <option value="CUSTOMER">Customer</option>
              <option value="WAREHOUSE_SUPERVISOR">Warehouse Supervisor</option>
              <option value="SALES_CLERK">Sales Clerk</option>
            </select>
          </div>

          {/*Submit button*/}
          <div className="form-actions">
            <button type="submit" className="btn" disabled={loading || success}>
              {/*Show loading text or Register*/}
              {loading ? "Registering..." : "Register"}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}

export default CreateUser; //Export component as default