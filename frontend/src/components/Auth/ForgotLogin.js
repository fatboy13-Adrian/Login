import React, { useState } from "react";
import { useNavigate } from "react-router-dom";

export default function ForgotLogin() {
  const [email, setEmail] = useState("");
  const [newUsername, setNewUsername] = useState("");
  const [newPassword, setNewPassword] = useState("");
  const [message, setMessage] = useState("");
  const [error, setError] = useState("");
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    setMessage("");
    setError("");

    const payload = {
      email: email.trim(),
      username: newUsername.trim(),
      password: newPassword.trim(),
    };

    if (!payload.email) {
      setError("Email is required");
      return;
    }

    try {
      const response = await fetch("http://localhost:8080/auth/forgotLogin", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(payload),
      });

      const data = await response.json();

      if (response.ok) {
        setMessage(data.message || "Credentials reset successful!");
        setError("");

        // Redirect to login after 1.5 seconds delay to show success message
        setTimeout(() => {
          navigate("/login");
        }, 1500);
      } else {
        setError(data.message || "Reset failed. Please try again.");
        setMessage("");
      }
    } catch {
      setError("Network or server error.");
      setMessage("");
    }
  };

  return (
    <div>
      <h2>Reset Credentials</h2>
      <form onSubmit={handleSubmit}>
        <label>Email (required)</label>
        <input
          type="email"
          value={email}
          onChange={(e) => setEmail(e.target.value)}
          required
          placeholder="Registered Email"
        />
        <label>New Username (optional)</label>
        <input
          type="text"
          value={newUsername}
          onChange={(e) => setNewUsername(e.target.value)}
          placeholder="New Username"
        />
        <label>New Password (optional)</label>
        <input
          type="password"
          value={newPassword}
          onChange={(e) => setNewPassword(e.target.value)}
          placeholder="New Password"
        />
        <button type="submit">Reset</button>
      </form>
      {message && <p style={{ color: "green" }}>{message}</p>}
      {error && <p style={{ color: "red" }}>{error}</p>}
    </div>
  );
}
