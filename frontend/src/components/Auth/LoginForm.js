import React, { useState } from "react";                   //React and useState hook
import { loginUser } from "../services/authService";       //Login API service
import { useNavigate } from "react-router-dom";            //Navigation hook
import '../../styles/styles.css';                        //Component styles

const LoginForm = () => 
{
  const [username, setUsername] = useState("");            //Username state
  const [password, setPassword] = useState("");            //Password state
  const [error, setError] = useState("");                  //Error message state
  const navigate = useNavigate();                           //Navigate function

  const handleLogin = async (e) => 
  {
    e.preventDefault();                                     //Prevent form reload
    setError("");                                           //Clear error

    try 
    {
      const userData = await loginUser(username, password); //Call login API
      const { token, userId, roleMessage } = userData;     //Destructure response

      const role = roleMessage?.split(":")[1]?.trim().toUpperCase();  //Extract role

      //Validate response
      if(!token || !userId || !role) 
      {                     
        throw new Error("Missing login data.");
      }

      localStorage.setItem("token", token);                 //Save token
      localStorage.setItem("userId", userId);               //Save userId
      localStorage.setItem("role", role);                   //Save role

      navigate("/dashboard");                                //Redirect to dashboard
    } 
    
    catch(err) 
    {
      setError("Invalid credentials or server error.");     //Set error message
    }
  };

  return (
    <div className="login-container">                        {/*Container*/}
      <h2>Login</h2>
      <form onSubmit={handleLogin} className="login-form">
        <div className="form-row">
          <label htmlFor="username" className="form-label">Username</label>
          <input id="username" type="text" value={username} onChange={(e) => setUsername(e.target.value)} required className="form-input"/>
        </div>

        <div className="form-row">
          <label htmlFor="password" className="form-label">Password</label>
          <input id="password" type="password" value={password} onChange={(e) => setPassword(e.target.value)} required className="form-input"/>
        </div>

        {error && <div className="error-message">{error}</div>}  {/*Show error*/}

        <div className="buttons-group">
          <button type="submit" className="btn">Login</button>                                                      {/*Submit login*/}
          <button type="button" onClick={() => navigate("/create-user")} className="btn">Register Account</button> {/*Go to register*/}
          <button type="button" onClick={() => navigate("/forgot-login")} className="btn">Forgot Password</button> {/*Go to forgot*/}
        </div>
      </form>
    </div>
  );
};

export default LoginForm; //Export component