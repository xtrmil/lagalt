import React from 'react';
import { Form } from 'react-bootstrap';
import Select from 'react-select';
import { checkInputField, selectBorderColor } from '../../utils/form/FormUtils';

const MultiSelectInput = (props) => {
  const {
    label,
    name,
    setFieldValue,
    setFieldTouched,
    errors,
    touched,
    values,
    options,
    defaultValue,
  } = props;

  return (
    <Form.Group>
      <Form.Label>{label}</Form.Label>
      <Select
        className="basic-multi-select mb-1"
        isMulti={true}
        isClearable={false}
        defaultValue={defaultValue}
        value={values[name]}
        name={name}
        options={options}
        closeMenuOnSelect={false}
        onBlur={() => {
          setFieldTouched(name);
        }}
        onChange={(opt) => setFieldValue(name, opt)}
        styles={selectBorderColor(errors, touched, values, name)}
      ></Select>
      {checkInputField(errors, touched, name)}
    </Form.Group>
  );
};

export default MultiSelectInput;
