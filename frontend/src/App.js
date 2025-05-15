import { BrowserRouter as Router, Routes, Route, Navigate } from "react-router-dom";
import { AuthProvider } from "./context/AuthContext";

import LoginForm from "./components/Auth/LoginForm";
import ForgotLogin from "./components/Auth/ForgotLogin";
import Home from "./components/Home/Home";
import AdminDashboard from "./Pages/AdminDashboard";
import CreateUser from "./Pages/CreateUser";
import ProtectedRoute from "./components/Auth/ProtectedRoute";

const AppRoutes = () => (
  <Routes>
    <Route path="/" element={<Navigate to="/login" replace />} />

    <Route path="/login" element={<LoginForm />} />
    <Route path="/forgot-login" element={<ForgotLogin />} />
    <Route path="/create-user" element={<CreateUser />} />

    <Route
      path="/home"
      element={
        <ProtectedRoute>
          <Home />
        </ProtectedRoute>
      }
    />
    <Route
      path="/admin"
      element={
        <ProtectedRoute>
          <AdminDashboard />
        </ProtectedRoute>
      }
    />

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
