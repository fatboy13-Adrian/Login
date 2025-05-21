import React, { useState } from "react"         //React and useState hook
import { useNavigate } from "react-router-dom"  //Navigation hook
import "../../styles/styles.css"           //Component styles

export default function ForgotLogin() 
{
  //Form data state
  const [formData, setFormData] = useState({  
    email: "",
    newUsername: "",
    newPassword: ""
  })

  const [message, setMessage] = useState("")    //Success message state
  const [error, setError] = useState("")        //Error message state
  const [success, setSuccess] = useState(false) //Success flag
  const [loading, setLoading] = useState(false) //Loading flag
  const navigate = useNavigate()                //Navigation function

  const handleChange = (e) => 
  {
    const { id, value } = e.target  //Get input id and value
    setFormData((prev) => ({        //Update form data
      ...prev,
      [id]: value
    }))
  }

  const handleSubmit = async (e) => 
  {
    e.preventDefault()  //Prevent form reload
    setMessage("")      //Clear messages
    setError("")

    const payload = 
    {
      email: formData.email.trim(),           //Trim inputs
      username: formData.newUsername.trim(),
      password: formData.newPassword.trim()
    }

    if(!payload.email)  //Email validation
    {
      setError("Email is required")
      return
    }

    setLoading(true)  //Start loading

    try 
    {
      const response = await fetch("http://localhost:8080/auth/forgotLogin", 
      {
        method: "POST",                                 //POST request
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(payload)
      })

      const data = await response.json()               //Parse JSON response

      if(response.ok)                                   //Success response
      {
        setMessage(data.message || "Credentials reset successful! Redirecting to login...")
        setError("")
        setSuccess(true)
        setTimeout(() => navigate("/login"), 5000)  //Redirect after 5 sec
      } 

      else                                          //Failure response
      {
        setError(data.message || "Reset failed. Please try again")
        setMessage("")
      }
    } 
    catch                                         //Network/server error
    {
      setError("Network or server error")
      setMessage("")
    } 

    finally                                           //Stop loading
    {
      setLoading(false)
    }
  }

  return (
    <div className="forgot-login-container">                          {/*Container*/}
      <h2>Reset Credentials</h2>

      {message && <div className="success-message">{message}</div>}   {/*Show success*/}
      {error && <div className="error-message">{error}</div>}         {/*Show error*/}

      <form onSubmit={handleSubmit} className="forgot-login-form">
        <div className="form-row">
          <label htmlFor="email" className="form-label">Email (required)</label>
          <input 
            id="email" type="email" value={formData.email} onChange={handleChange} 
            placeholder="Registered Email" required disabled={loading || success}
            className="form-input"
          />
        </div>

        <div className="form-row">
          <label htmlFor="newUsername" className="form-label">New Username (optional)</label>
          <input 
            id="newUsername" type="text" value={formData.newUsername} onChange={handleChange} 
            placeholder="New Username" disabled={loading || success}
            className="form-input"
          />
        </div>

        <div className="form-row">
          <label htmlFor="newPassword" className="form-label">New Password (optional)</label>
          <input 
            id="newPassword" type="password" value={formData.newPassword} onChange={handleChange} 
            placeholder="New Password" disabled={loading || success}
            className="form-input"
          />
        </div>

        <div className="buttons-group">
          <button 
            type="submit" 
            disabled={loading || success} 
            className={`reset-button btn${loading || success ? " disabled" : ""}`}
          >
            {loading ? "Processing..." : "Reset"}
          </button>
        </div>
      </form>
    </div>
  )
}