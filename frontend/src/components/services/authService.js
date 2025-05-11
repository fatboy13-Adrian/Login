import axios from 'axios';  //Importing axios library to make HTTP requests to the backend API

const API_URL = 'http://localhost:8080/auth'; //Defining the base URL for authentication-related API calls

//Login request function to authenticate a user with username and password
export const loginUser = async (username, password) => 
{
  try 
  {
    //Making a POST request to the backend API with username and password in the request body
    const response = await axios.post(`${API_URL}/login`, {username, password});
    return response.data; //Returning the data from the response (usually contains user info or a token)
  } 
  
  catch(err) 
  {
    console.error(err); //Logging the error if the request fails

    //Throwing a custom error message for failed login attempts
    throw new Error('Failed to login. Please check your credentials or try again later.');
  }
};