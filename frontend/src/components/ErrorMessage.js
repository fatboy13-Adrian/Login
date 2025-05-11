// src/components/ErrorMessage.js

import React from 'react';

// A reusable ErrorMessage component for displaying errors
const ErrorMessage = ({ message }) => {
  return message ? <div style={{ color: 'red' }}>{message}</div> : null;
};

export default ErrorMessage;
