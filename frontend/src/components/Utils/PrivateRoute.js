import {Navigate} from 'react-router-dom';      //Import Navigate to redirect users
import {useAuth} from '../context/AuthContext'; //Import custom hook to get authentication status

//Function component that wraps private routes
function PrivateRoute({children}) 
{
  const {isAuthenticated} = useAuth();  //Get the authentication status from context

  if(!isAuthenticated)                  //If the user is not authenticated
    return <Navigate to="/login"/>;     //Redirect to login page

  return children;                      //If authenticated, render the children components (protected route)
}

export default PrivateRoute;            //Export the component for use in other parts of the app