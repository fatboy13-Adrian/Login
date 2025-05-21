//Import necessary React hooks and Axios for HTTP requests
import React, {createContext, useState, useEffect, useContext} from 'react';
import axios from 'axios';

const AuthContext = createContext();  //Create an authentication context to share auth state across components

//Helper function to check if a JWT token is expired
const isTokenExpired = (token) => 
{
  try 
  {
    //Decode the JWT and check if the expiry time has passed
    const decoded = JSON.parse(atob(token.split('.')[1]));
    return decoded.exp * 1000 < Date.now();
  } 
  
  catch(error) 
  {
    return true;  //If decoding fails, treat the token as expired
  }
};

//Set the Authorization header for all Axios requests
const setAuthHeaders = (token) => 
{
  axios.defaults.headers.common['Authorization'] = `Bearer ${token}`;
};

//Remove the Authorization header from Axios
const clearAuthHeaders = () => 
{
  delete axios.defaults.headers.common['Authorization'];
};

//Check for a valid token on initial load and set auth state
const initializeAuthState = (setIsAuthenticated) => 
{
  const token = localStorage.getItem('token');
  if(token && !isTokenExpired(token)) 
  {
    setAuthHeaders(token);    //Set token for future requests
    setIsAuthenticated(true); //Set user as authenticated
  }
};

//Handle user login: update state, localStorage, and Axios headers
const login = (userData, setUser, setIsAuthenticated) => 
{
  setUser(userData);
  setIsAuthenticated(true);
  localStorage.setItem('token', userData.token);
  localStorage.setItem('role', userData.role);
  setAuthHeaders(userData.token);
};

//Handle user logout: clear state, localStorage, and Axios headers
const logout = (setUser, setIsAuthenticated) => 
{
  setUser(null);
  setIsAuthenticated(false);
  localStorage.removeItem('token');
  localStorage.removeItem('role');
  clearAuthHeaders();
};

//Provider component to wrap around app and manage auth state
export const AuthProvider = ({ children }) => 
  {
  const [isAuthenticated, setIsAuthenticated] = useState(false);  //Track login state
  const [user, setUser] = useState(null);                         //Track current user data

  //On component mount, check for existing auth state
  useEffect(() => 
  {
    initializeAuthState(setIsAuthenticated);
  }, []);

  return (
    <AuthContext.Provider value={{isAuthenticated,  user, login: (userData) => login(userData, setUser, setIsAuthenticated), logout: () => logout(setUser, setIsAuthenticated),}}>
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => useContext(AuthContext); //Custom hook to access auth context from other components