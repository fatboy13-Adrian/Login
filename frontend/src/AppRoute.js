import React from 'react';
import { Routes, Route } from 'react-router-dom';
import AdminDashboard from './components/AdminDashboard';
import HomePage from './components/HomePage';
import LoginForm from './components/Auth/LoginForm';
import ProtectedRoute from './components/ProtectedRoute';

const AppRoutes = () => (
  <Routes>
    <Route path="/" element={<LoginForm />} />
    <Route path="/admin" element={<ProtectedRoute><AdminDashboard /></ProtectedRoute>} />
    <Route path="/home" element={<ProtectedRoute><HomePage /></ProtectedRoute>} />
  </Routes>
);

export default AppRoutes;