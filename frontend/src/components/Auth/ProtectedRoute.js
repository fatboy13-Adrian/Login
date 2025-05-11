
import React from 'react';                            //Importing React for building components
import {Navigate} from 'react-router-dom';            //Importing Navigate from react-router-dom to handle navigation in case of redirection
import {useAuth} from '../../context/AuthContext';    //Importing the custom useAuth hook to manage authentication state from context

//ProtectedRoute component checks if the user is authenticated before rendering children
const ProtectedRoute = ({children}) => 
{
  const {isAuthenticated} = useAuth();    //Destructuring isAuthenticated from the useAuth hook to check authentication status

  //Return the children if authenticated, otherwise redirect to the login page
  return isAuthenticated ? children : <Navigate to="/login"/>;
};

export default ProtectedRoute;  //Exporting ProtectedRoute component to use in other parts of the app                    