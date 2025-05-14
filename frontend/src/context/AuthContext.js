import React, { createContext, useState, useEffect, useContext } from 'react';
import axios from 'axios';

const AuthContext = createContext();

const isTokenExpired = (token) => {
  try {
    const decoded = JSON.parse(atob(token.split('.')[1]));
    return decoded.exp * 1000 < Date.now();
  } catch (error) {
    return true;
  }
};

const setAuthHeaders = (token) => {
  axios.defaults.headers.common['Authorization'] = `Bearer ${token}`;
};

const clearAuthHeaders = () => {
  delete axios.defaults.headers.common['Authorization'];
};

const initializeAuthState = (setIsAuthenticated) => {
  const token = localStorage.getItem('token');
  if (token && !isTokenExpired(token)) {
    setAuthHeaders(token);
    setIsAuthenticated(true);
  }
};

const login = (userData, setUser, setIsAuthenticated) => {
  setUser(userData);
  setIsAuthenticated(true);
  localStorage.setItem('token', userData.token);
  localStorage.setItem('role', userData.role);
  setAuthHeaders(userData.token);
};

const logout = (setUser, setIsAuthenticated) => {
  setUser(null);
  setIsAuthenticated(false);
  localStorage.removeItem('token');
  localStorage.removeItem('role');
  clearAuthHeaders();
};

export const AuthProvider = ({ children }) => {
  const [isAuthenticated, setIsAuthenticated] = useState(false);
  const [user, setUser] = useState(null);

  useEffect(() => {
    initializeAuthState(setIsAuthenticated);
  }, []);

  return (
    <AuthContext.Provider
      value={{
        isAuthenticated,
        user,
        login: (userData) => login(userData, setUser, setIsAuthenticated),
        logout: () => logout(setUser, setIsAuthenticated),
      }}
    >
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => useContext(AuthContext);