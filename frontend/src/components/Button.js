import React from 'react';  //Importing React for building components

//A reusable Button component for handling button rendering
const Button = ({type, onClick, children}) => 
{
  return (
    <button type={type} onClick={onClick}>
      {children}
    </button>
  );
};

export default Button;