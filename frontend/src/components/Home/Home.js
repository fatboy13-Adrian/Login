import {useLocation} from 'react-router-dom'; //Importing the 'useLocation' hook from 'react-router-dom' to access the current URL and location state

//Home component that displays a welcome message based on the user's role
const Home = () => 
{
  //Using the useLocation hook to get the location object, which includes the current URL and any state passed via navigation
  const location = useLocation();
  
  //Destructuring the role from location.state, if available, with a fallback default value of 'user'
  const {role} = location.state || {role: 'user'};

  //Returning the JSX to display the role-based welcome message
  return <h1>Welcome to {role} home page</h1>;
};

export default Home;  //Exporting the Home component to be used in other parts of the application