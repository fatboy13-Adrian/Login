import React from 'react';

const ErrorMessage = ({ message }) => {
  return message ? <div style={{ color: 'red' }}>{message}</div> : null;
};

export default ErrorMessage;
