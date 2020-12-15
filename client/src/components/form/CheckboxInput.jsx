import React from 'react';
import { Form } from 'react-bootstrap';
import { checkInputField } from '../../utils/form/FormUtils';

const CheckboxInput = (props) => {
  const { name, label, errors, touched, handleChange, handleBlur } = props;

  return (
    <Form.Group>
      <div className="d-flex">
        <Form.Check name={name} onChange={handleChange} onBlur={handleBlur}></Form.Check>
        <Form.Label>{label}</Form.Label>
      </div>
      {checkInputField(errors, touched, name)}
    </Form.Group>
  );
};

export default CheckboxInput;
