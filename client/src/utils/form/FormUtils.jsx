import React from 'react';
import * as yup from 'yup';

export const checkInputField = (errors, touched, name) => {
  return errors[name] && touched[name] && <div style={{ color: 'red' }}>{errors[name]}</div>;
};

const errorBorder = {
  control: (provided) => ({
    ...provided,
    borderColor: 'red',
  }),
};

const successBorder = {
  control: (provided) => ({
    ...provided,
    borderColor: 'green',
  }),
};

export const selectBorderColor = (errors, touched, values, name) => {
  return errors[name] && touched[name]
    ? errorBorder
    : touched[name] && values[name]
    ? successBorder
    : {};
};

export const inputBorderColor = (errors, touched, values, name) => {
  return errors[name] && touched[name]
    ? { borderColor: 'red' }
    : touched[name] && values[name]
    ? { borderColor: 'green' }
    : {};
};

export const editProfileSchema = yup.object({
  name: yup.string().required('Name is a required field'),
  skills: yup
    .array()
    .of(
      yup.object().shape({
        label: yup.string().required(),
        value: yup.string().required(),
      }),
    )
    .nullable()
    .required('Select atleast one skill'),
  description: yup.string().required('Description is a required field'),
});

export const createProjectSchema = yup.object({
  title: yup.string().required('Title is required'),
  description: yup.string().required('Description is required'),
  skills: yup
    .array()
    .of(
      yup.object().shape({
        label: yup.string().required(),
        value: yup.string().required(),
      }),
    )
    .nullable()
    .required('At least one skill is required'),
  industry: yup.string().required('Select an industry'),
});

export const joinProjectSchema = yup.object({
  motivation: yup.string().required('Motivation is required'),
  acceptTerms: yup.bool().oneOf([true], 'Accept Terms is required'),
});
