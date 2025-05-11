import {createContext, useState, useEffect, useContext} from 'react';   //Import necessary hooks and functions from React
import axios from 'axios';                                              //Import axios to handle HTTP requests and manage authentication header

const AuthContext = createContext();  //Create the AuthContext to hold authentication-related state and methods

//Function to check if the token has expired
const isTokenExpired = (token) => 
{
  try 
  {
    const decoded = JSON.parse(atob(token.split('.')[1]));  //Decoding the JWT token
    return decoded.exp * 1000 < Date.now();                 //Checking expiration
  } 
  
  catch (error) 
  {
    return true; //If the token is invalid or malformed
  }
};

//Function to set authentication headers globally for axios requests
const setAuthHeaders = (token) => 
{
  axios.defaults.headers.common['Authorization'] = `Bearer ${token}`; //Set the Authorization header for axios requests with the provided token
};

//Function to clear authentication headers
const clearAuthHeaders = () => 
{
  delete axios.defaults.headers.common['Authorization'];  //Remove the Authorization header from axios to clear authentication state
};

//Function to initialize authentication state from localStorage
const initializeAuthState = (setIsAuthenticated) => 
{
  const token = localStorage.getItem('token');  //Retrieve the token from localStorage
  //If token exists and is not expired, set the authentication state
  if(token && !isTokenExpired(token)) 
  {
    setAuthHeaders(token);    //Set auth header
    setIsAuthenticated(true)  //Set authenticated state
  }
};

//Login function to handle user login and save authentication details
const login = (userData, setUser, setIsAuthenticated) => 
{
  setUser(userData);                              //userData must contain role
  setIsAuthenticated(true);                       //Mark the user as authenticated
  localStorage.setItem('token', userData.token);  //Save token and role in localStorage
  localStorage.setItem('role', userData.role);    //store role
  setAuthHeaders(userData.token);                 //Set auth headers
};

//Logout function to handle user logout and clear authentication details
const logout = (setUser, setIsAuthenticated) => 
  {
  setUser(null);                      //Reset user data in state
  setIsAuthenticated(false);          //Mark the user as not authenticated
  localStorage.removeItem('token');   //Remove the token and role from localStorage
  localStorage.removeItem('role');
  clearAuthHeaders();                 //Clear auth headers
};

//The AuthProvider component provides authentication state and methods to the app
export const AuthProvider = ({children}) => 
{
  const [isAuthenticated, setIsAuthenticated] = useState(false);  //Define state to track if the user is authenticated
  const [user, setUser] = useState(null);                         //Define state to store the authenticated user data

  //UseEffect to load the token from localStorage and check if the user is authenticated
  useEffect(() => 
  {
    initializeAuthState(setIsAuthenticated);  //Check token expiration on mount
  }, []);                                     //Empty dependency array ensures this runs once on component mount

  //Return the AuthContext provider to make authentication state and functions available throughout the app
  return (
    <AuthContext.Provider value={{isAuthenticated, user, login: (userData) => login(userData, setUser, setIsAuthenticated), logout: () => logout(setUser, setIsAuthenticated)}}>
      {children}
    </AuthContext.Provider>
  );
};

//Custom hook to access authentication state and functions
export const useAuth = () => useContext(AuthContext); //Provides access to AuthContext's value in other components