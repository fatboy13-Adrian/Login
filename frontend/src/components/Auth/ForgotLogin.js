import React, { useState } from "react";
import { useNavigate } from "react-router-dom";

export default function ForgotLogin() {
  const [email, setEmail] = useState("");
  const [newUsername, setNewUsername] = useState("");
  const [newPassword, setNewPassword] = useState("");
  const [message, setMessage] = useState("");
  const [error, setError] = useState("");
  const [success, setSuccess] = useState(false);
  const [loading, setLoading] = useState(false);

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
      setError("Email is required.");
      return;
    }

    setLoading(true);

    try {
      const response = await fetch("http://localhost:8080/auth/forgotLogin", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(payload),
      });

      const data = await response.json();

      if (response.ok) {
        setMessage(data.message || "Credentials reset successful! Redirecting to login...");
        setError("");
        setSuccess(true);

        setTimeout(() => {
          navigate("/login");
        }, 5000); // 5-second delay
      } else {
        setError(data.message || "Reset failed. Please try again.");
        setMessage("");
      }
    } catch {
      setError("Network or server error.");
      setMessage("");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="container mt-5">
      <div className="col-md-6 offset-md-3 border rounded p-4 shadow">
        <h2 className="text-center mb-4">Reset Credentials</h2>

        {message && <div className="alert alert-success">{message}</div>}
        {error && <div className="alert alert-danger">{error}</div>}

        <form onSubmit={handleSubmit}>
          <div className="mb-3">
            <label>Email (required)</label>
            <input
              type="email"
              className="form-control"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              required
              placeholder="Registered Email"
              disabled={loading || success}
            />
          </div>

          <div className="mb-3">
            <label>New Username (optional)</label>
            <input
              type="text"
              className="form-control"
              value={newUsername}
              onChange={(e) => setNewUsername(e.target.value)}
              placeholder="New Username"
              disabled={loading || success}
            />
          </div>

          <div className="mb-3">
            <label>New Password (optional)</label>
            <input
              type="password"
              className="form-control"
              value={newPassword}
              onChange={(e) => setNewPassword(e.target.value)}
              placeholder="New Password"
              disabled={loading || success}
            />
          </div>

          <button
            type="submit"
            className="btn btn-primary w-100"
            disabled={loading || success}
          >
            {loading ? "Processing..." : "Reset"}
          </button>
        </form>
      </div>
    </div>
  );
}
