import React from 'react';

const Button = ({ type = 'button', onClick, children }) => {
  return (
    <button type={type} onClick={onClick} style={{ padding: '0.5rem 1rem', backgroundColor: '#4CAF50', color: 'white', border: 'none', cursor: 'pointer' }}>
      {children}
    </button>
  );
};

export default Button;