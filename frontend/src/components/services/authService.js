// src/services/authService.js
import axios from 'axios';

const API_URL = 'http://localhost:8080/auth';  // Ensure this URL is correct for your backend API

// Login request
export const loginUser = async (username, password) => {
  try {
    const response = await axios.post(`${API_URL}/login`, { username, password });
    return response.data;
  } catch (err) {
    console.error(err);
    throw new Error('Failed to login. Please check your credentials or try again later.');
  }
};
