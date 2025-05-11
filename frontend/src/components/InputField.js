// src/components/InputField.js

import React from 'react';

// A reusable InputField component for handling text inputs
const InputField = ({id, label, type, value, onChange, required}) => {
  return (
    <div>
      <label htmlFor={id}>{label}:</label>
      <input
        type={type}
        id={id}
        value={value}
        onChange={onChange}
        required={required}
      />
    </div>
  );
};

export default InputField;