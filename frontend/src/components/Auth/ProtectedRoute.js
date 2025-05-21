import React from 'react';                            //Import React library for JSX support
import {Navigate} from 'react-router-dom';          //Import Navigate from React Router to handle redirection
import {useAuth} from '../../context/AuthContext';  //Import custom authentication context hook to access auth state

//Define a ProtectedRoute component that receives children (nested components)
const ProtectedRoute = ({ children }) => 
{
  const { isAuthenticated } = useAuth();      //Destructure isAuthenticated value from the authentication context

  //If user is not authenticated, redirect to the login page
  if(!isAuthenticated) 
    return <Navigate to="/login" replace />;  //'replace' prevents back navigation to the protected page

  return children;   //If authenticated, render the protected content        
};

export default ProtectedRoute;  //Export the component for use in routing