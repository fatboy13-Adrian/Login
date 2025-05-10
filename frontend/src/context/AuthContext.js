import { createContext, useState, useEffect, useContext } from 'react';
import axios from 'axios';

const AuthContext = createContext();

export const AuthProvider = ({ children }) => {
  const [isAuthenticated, setIsAuthenticated] = useState(false);
  const [user, setUser] = useState(null);

  // Helper function to check if the token is expired (or close to expiration)
  const isTokenExpired = (token) => {
    try {
      const decoded = JSON.parse(atob(token.split('.')[1])); // Decoding the JWT token
      return decoded.exp * 1000 < Date.now(); // Checking expiration
    } catch (error) {
      return true; // If the token is invalid or malformed
    }
  };

  // UseEffect to load token from localStorage and check expiration
  useEffect(() => {
    const token = localStorage.getItem('token');
    if (token && !isTokenExpired(token)) {
      axios.defaults.headers.common['Authorization'] = `Bearer ${token}`;
      setIsAuthenticated(true);
    }
  }, []);

  // Login function
  const login = (userData) => {
  setUser(userData); // userData must contain role
  setIsAuthenticated(true);
  localStorage.setItem('token', userData.token);
  localStorage.setItem('role', userData.role);  // store role
  axios.defaults.headers.common['Authorization'] = `Bearer ${userData.token}`;
};
  // Logout function
  const logout = () => {
    setUser(null);
    setIsAuthenticated(false);
    localStorage.removeItem('token');
    delete axios.defaults.headers.common['Authorization'];
  };

  return (
    <AuthContext.Provider value={{ isAuthenticated, user, login, logout }}>
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => useContext(AuthContext);
