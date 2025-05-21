import {BrowserRouter as Router, Routes, Route, Navigate} from "react-router-dom";
import {AuthProvider} from "./context/AuthContext";

import LoginForm from "./components/Auth/LoginForm";
import ForgotLogin from "./components/Auth/ForgotLogin";
import ProtectedRoute from "./components/Auth/ProtectedRoute";

import Dashboard from "./Pages/Dashboard";
import CreateUser from "./Pages/CreateUser";
import UpdateUser from "./Pages/UpdateUser";

const AppRoutes = () => (
  <Routes future={{v7_startTransition: true, v7_relativeSplatPath: true}}>
    {/*Default route redirects to login*/}
    <Route path="/" element={<Navigate to="/login" replace/>}/>

    {/*Public Routes*/}
    <Route path="/login" element={<LoginForm/>}/>
    <Route path="/forgot-login" element={<ForgotLogin/>}/>
    <Route path="/create-user" element={<CreateUser/>}/>

    {/*Protected Routes*/}
    <Route
      path="/dashboard"
      element={
        <ProtectedRoute>
          <Dashboard/>
        </ProtectedRoute>
      }
   />
    <Route
      path="/admin"
      element={
        <ProtectedRoute>
          <Dashboard/>
        </ProtectedRoute>
}
   />
    <Route
      path="/profile"
      element={
        <ProtectedRoute>
          <Dashboard/>
        </ProtectedRoute>
      }
   />
    <Route
      path="/update-user/:userId"
      element={
        <ProtectedRoute>
          <UpdateUser/>
        </ProtectedRoute>
      }
   />

    {/*Catch-all Route*/}
    <Route path="*" element={<Navigate to="/login" replace/>}/>
  </Routes>
);

const App = () => (
  <AuthProvider>
    <Router future={{v7_startTransition: true, v7_relativeSplatPath: true}}>
      <AppRoutes/>
    </Router>
  </AuthProvider>
);

export default App;
