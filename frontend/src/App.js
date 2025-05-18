import { BrowserRouter as Router, Routes, Route, Navigate } from "react-router-dom";
import { AuthProvider } from "./context/AuthContext";
import LoginForm from "./components/Auth/LoginForm";
import ForgotLogin from "./components/Auth/ForgotLogin";
import Dashboard from "./Pages/Dashboard"; // Dashboard component
import CreateUser from "./Pages/CreateUser";
import UpdateUser from "./Pages/UpdateUser";
import ProtectedRoute from "./components/Auth/ProtectedRoute";

const AppRoutes = () => (
  <Routes>
    {/* Redirect root to login */}
    <Route path="/" element={<Navigate to="/login" replace />} />

    {/* Public Routes */}
    <Route path="/login" element={<LoginForm />} />
    <Route path="/forgot-login" element={<ForgotLogin />} />
    <Route path="/create-user" element={<CreateUser />} />

    {/* Protected Routes */}
    <Route
      path="/dashboard"
      element={
        <ProtectedRoute>
          <Dashboard />
        </ProtectedRoute>
      }
    />
    <Route
      path="/admin"
      element={
        <ProtectedRoute>
          <Dashboard />
        </ProtectedRoute>
      }
    />
    <Route
      path="/profile"
      element={
        <ProtectedRoute>
          <Dashboard />
        </ProtectedRoute>
      }
    />
    <Route
      path="/update-user"
      element={
        <ProtectedRoute>
          <UpdateUser />
        </ProtectedRoute>
      }
    />

    {/* Catch-all route */}
    <Route path="*" element={<Navigate to="/login" replace />} />
  </Routes>
);

const App = () => (
  <AuthProvider>
    <Router>
      <AppRoutes />
    </Router>
  </AuthProvider>
);

export default App;
