import {BrowserRouter as Router, Routes, Route, Navigate} from 'react-router-dom';  //Importing necessary components and hooks from react-router-dom for routing
import {AuthProvider} from './context/AuthContext';                                 //Importing AuthProvider from context to wrap the app and provide authentication context
import Login from './components/Auth/LoginForm';                                    //Importing components for Login and Home pages
import Home from './components/Home/Home';
import ProtectedRoute from './components/Auth/ProtectedRoute';                      //Importing the ProtectedRoute component to protect routes that require authentication

//Defining routes for the application using React Router
const AppRoutes = () => (
  <Routes>
    <Route path="/" element={<Navigate to="/login" replace/>} />                {/*Redirect from the root URL to the login page*/}
    <Route path="/login" element={<Login/>} />                                  {/* Route for login page */}
    <Route path="/home" element={<ProtectedRoute><Home /></ProtectedRoute>} />  {/*Protected route for home page, only accessible if authenticated*/}
  </Routes>
);

//Main App component wrapping everything inside the AuthProvider and Router
const App = () => 
  (
  <AuthProvider>    {/*Providing authentication context to the entire app*/}
    <Router>        {/*Wrapping app with Router to handle routing*/}
      <AppRoutes/>  {/*Rendering the defined routes*/}
    </Router>
  </AuthProvider>
);

export default App; //Exporting the App component for use in other parts of the application