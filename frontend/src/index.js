import React from 'react';                //Importing React to enable JSX syntax and create React components
import ReactDOM from 'react-dom/client';  //Importing ReactDOM from 'react-dom/client' to work with React 18's new root API
import './index.css';                     //Importing the global CSS file to apply styles across the app
import App from './App';                  //Importing the main App component, which contains the core logic of the app

//Creating a root element for the React application and targeting the DOM element with the id 'root'
const root = ReactDOM.createRoot(document.getElementById('root'));  //Use createRoot for React 18

//Rendering the React application into the root element, wrapped in React.StrictMode for development checks
root.render(
  <React.StrictMode>
    <App/> {/*Rendering the main App component*/}
  </React.StrictMode>
);