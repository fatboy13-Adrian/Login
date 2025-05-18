import React, { useState } from "react";
import axios from "axios";
import { useNavigate } from "react-router-dom";

function CreateUser() {
  const navigate = useNavigate();

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

  const [error, setError] = useState(null);
  const [success, setSuccess] = useState(false);
  const [loading, setLoading] = useState(false);

  const {
    firstName,
    lastName,
    username,
    email,
    phoneNumber,
    homeAddress,
    password,
    role,
  } = user;

  const onInputChange = (e) => {
    setUser({ ...user, [e.target.name]: e.target.value });
  };

  const onSubmit = async (e) => {
    e.preventDefault();
    setError(null);
    setSuccess(false);

    const trimmedUser = {
      firstName: firstName.trim(),
      lastName: lastName.trim(),
      username: username.trim(),
      email: email.trim(),
      phoneNumber: phoneNumber.trim(),
      homeAddress: homeAddress.trim(),
      password,
      role,
    };

    setLoading(true);
    try {
      await axios.post("http://localhost:8080/users", trimmedUser);
      setSuccess(true);
      setTimeout(() => {
        navigate("/");
      }, 5000); // Wait 5 seconds before redirecting
    } catch (err) {
      if (err.response) {
        if (err.response.status === 409) {
          setError(
            "User with this email or username already exists. Please use different credentials."
          );
        } else if (err.response.data?.message) {
          setError(err.response.data.message);
        } else {
          setError(`Error: ${err.response.status} - ${err.response.statusText}`);
        }
      } else {
        setError("Network error. Please check your connection and try again.");
      }
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="container">
      <div className="row">
        <div className="col-md-6 offset-md-3 border rounded p-4 mt-2 shadow">
          <h2 className="text-center m-4">Register User</h2>

          {error && (
            <div className="alert alert-danger" role="alert">
              {error}
            </div>
          )}

          {success && (
            <div className="alert alert-success" role="alert">
              Registration successful! Redirecting to login in 5 seconds...
            </div>
          )}

          <form onSubmit={onSubmit}>
            <div className="mb-3">
              <label htmlFor="firstName" className="form-label">
                First Name
              </label>
              <input
                type="text"
                className="form-control"
                name="firstName"
                value={firstName}
                onChange={onInputChange}
                required
                minLength={4}
                maxLength={30}
                placeholder="Enter your first name"
                disabled={loading || success}
              />
            </div>

            <div className="mb-3">
              <label htmlFor="lastName" className="form-label">
                Last Name
              </label>
              <input
                type="text"
                className="form-control"
                name="lastName"
                value={lastName}
                onChange={onInputChange}
                required
                minLength={4}
                maxLength={20}
                placeholder="Enter your last name"
                disabled={loading || success}
              />
            </div>

            <div className="mb-3">
              <label htmlFor="username" className="form-label">
                Username
              </label>
              <input
                type="text"
                className="form-control"
                name="username"
                value={username}
                onChange={onInputChange}
                required
                minLength={4}
                maxLength={20}
                placeholder="Choose a username"
                disabled={loading || success}
              />
            </div>

            <div className="mb-3">
              <label htmlFor="email" className="form-label">
                Email address
              </label>
              <input
                type="email"
                className="form-control"
                name="email"
                value={email}
                onChange={onInputChange}
                required
                placeholder="Enter your email"
                disabled={loading || success}
              />
            </div>

            <div className="mb-3">
              <label htmlFor="phoneNumber" className="form-label">
                Phone Number
              </label>
              <input
                type="tel"
                className="form-control"
                name="phoneNumber"
                value={phoneNumber}
                onChange={onInputChange}
                placeholder="Enter your phone number"
                disabled={loading || success}
              />
            </div>

            <div className="mb-3">
              <label htmlFor="homeAddress" className="form-label">
                Home Address
              </label>
              <input
                type="text"
                className="form-control"
                name="homeAddress"
                value={homeAddress}
                onChange={onInputChange}
                placeholder="Enter your home address"
                disabled={loading || success}
              />
            </div>

            <div className="mb-3">
              <label htmlFor="password" className="form-label">
                Password
              </label>
              <input
                type="password"
                className="form-control"
                name="password"
                value={password}
                onChange={onInputChange}
                required
                minLength={6}
                placeholder="Create a password"
                disabled={loading || success}
              />
            </div>

            <div className="mb-3">
              <label htmlFor="role" className="form-label">
                Role
              </label>
              <select
                className="form-select"
                name="role"
                value={role}
                onChange={onInputChange}
                required
                disabled={loading || success}
              >
                <option value="">Select role</option>
                <option value="ADMIN">Admin</option>
                <option value="CUSTOMER">Customer</option>
                <option value="WAREHOUSE_SUPERVISOR">Warehouse Supervisor</option>
                <option value="SALES_CLERK">Sales Clerk</option>
              </select>
            </div>

            <button
              type="submit"
              className="btn btn-primary w-100"
              disabled={loading || success}
            >
              {loading ? "Registering..." : "Register"}
            </button>
          </form>
        </div>
      </div>
    </div>
  );
}

export default CreateUser;
