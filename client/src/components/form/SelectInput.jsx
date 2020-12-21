import React from 'react';
import { Form } from 'react-bootstrap';
import Select from 'react-select';
import { checkInputField, selectBorderColor } from '../../utils/form/FormUtils';

const SelectInput = (props) => {
  const {
    label,
    name,
    setFieldTouched,
    errors,
    touched,
    values,
    options,
    isMulti,
    defaultValue,
    onChange,
  } = props;

  return (
    <Form.Group>
      <Form.Label>{label}</Form.Label>
      <Select
        className="basic-multi-select mb-1"
        isClearable={false}
        defaultValue={defaultValue}
        name={name}
        options={options}
        closeMenuOnSelect={!isMulti}
        onBlur={() => {
          setFieldTouched(name);
        }}
        onChange={onChange}
        styles={selectBorderColor(errors, touched, values, name)}
      ></Select>
      {checkInputField(errors, touched, name)}
    </Form.Group>
  );
};

export default SelectInput;
