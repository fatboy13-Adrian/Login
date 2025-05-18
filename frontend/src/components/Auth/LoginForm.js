// LoginForm.js
import React, { useState } from "react";
import { loginUser } from "../services/authService";
import { useNavigate } from "react-router-dom";
import InputField from "../InputField";
import ErrorMessage from "../ErrorMessage";
import Button from "../Button";

const LoginForm = () => {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");
  const navigate = useNavigate();

  const handleLogin = async (e) => {
    e.preventDefault();
    setError("");

    try {
      const userData = await loginUser(username, password);
      const { token, userId, roleMessage } = userData;

      const role = roleMessage?.split(":")[1]?.trim().toUpperCase();

      if (!token || !userId || !role) {
        throw new Error("Missing login data.");
      }

      localStorage.setItem("token", token);
      localStorage.setItem("userId", userId);
      localStorage.setItem("role", role);

      navigate("/dashboard");
    } catch (err) {
      setError("Invalid credentials or server error.");
    }
  };

  return (
    <div>
      <h2>Login</h2>
      <form onSubmit={handleLogin}>
        <InputField
          id="username"
          label="Username"
          type="text"
          value={username}
          onChange={(e) => setUsername(e.target.value)}
          required
        />
        <InputField
          id="password"
          label="Password"
          type="password"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
          required
        />
        <ErrorMessage message={error} />
        <Button type="submit">Login</Button>
      </form>

      <div style={{ marginTop: "1rem" }}>
        <Button onClick={() => navigate("/create-user")}>Register New User</Button>
      </div>
      <div style={{ marginTop: "1rem" }}>
        <Button onClick={() => navigate("/forgot-login")}>Forgot My Password</Button>
      </div>
    </div>
  );
};

export default LoginForm;
