import React from 'react';
import { Button, Form } from 'react-bootstrap';
import Select from 'react-select';
import { Formik } from 'formik';
import * as yup from 'yup';

const ProjectForm = () => {
  const schema = yup.object({
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

  const options = [
    { value: 'DRUMMER', label: 'Drummer' },
    { value: 'WEB_DEV', label: 'WEB_DEV' },
    { value: 'REACT', label: 'REACT' },
    { value: 'SECURITY', label: 'SECURITY' },
    { value: 'ANGULAR', label: 'ANGULAR' },
  ];

  const industryOptions = [
    { value: 'MUSIC', label: 'Music' },
    { value: 'FILM', label: 'Film' },
    { value: 'GAMEDEVELOPMENT', label: 'Game Development' },
    { value: 'WEBDEVELOPMENT', label: 'Web Development' },
  ];
  const onFormSubmit = (values) => {
    const project = { ...values, skills: values.skills.map((skill) => skill.value) };
    console.log(project);
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

  const selectBorderColor = (errors, touched, values, name) => {
    return errors[name] && touched[name]
      ? errorBorder
      : touched[name] && values[name]
      ? successBorder
      : {};
  };

  const inputBorderColor = (errors, touched, values, name) => {
    return errors[name] && touched[name]
      ? { borderColor: 'red' }
      : touched[name] && values[name]
      ? { borderColor: 'green' }
      : {};
  };
  const checkInputField = (errors, touched, name) => {
    return errors[name] && touched[name] && <div style={{ color: 'red' }}>{errors[name]}</div>;
  };
  return (
    <Formik
      validationSchema={schema}
      onSubmit={(values) => onFormSubmit(values)}
      initialValues={{ title: '', description: '', skills: null, industry: '' }}
    >
      {({
        handleSubmit,
        handleChange,
        handleBlur,
        setFieldValue,
        setFieldTouched,
        values,
        touched,
        errors,
      }) => (
        <>
          <Form onSubmit={handleSubmit}>
            <Form.Group>
              <Form.Label>Project Name*</Form.Label>
              <Form.Control
                type="text"
                name="title"
                value={values.title}
                onChange={handleChange}
                onBlur={handleBlur}
                style={inputBorderColor(errors, touched, values, 'title')}
              />
              {checkInputField(errors, touched, 'title')}
            </Form.Group>

            <Form.Group>
              <Form.Label>Select Industry*</Form.Label>
              <Select
                name="industry"
                placeholder="Select Industry..."
                options={industryOptions}
                onBlur={() => setFieldTouched('industry')}
                onChange={(opt, event) => {
                  setFieldValue('industry', opt.value);
                }}
                styles={selectBorderColor(errors, touched, values, 'industry')}
              ></Select>
              {checkInputField(errors, touched, 'industry')}
            </Form.Group>

            <Form.Group>
              <Form.Label>Skills Needed*</Form.Label>
              <Select
                className="basic-multi-select mb-1"
                placeholder="Select Skills..."
                isMulti
                name="skills"
                options={options}
                closeMenuOnSelect={false}
                onBlur={() => setFieldTouched('skills')}
                onChange={(opt) => {
                  setFieldValue('skills', opt);
                }}
                styles={selectBorderColor(errors, touched, values, 'skills')}
              ></Select>
              {checkInputField(errors, touched, 'skills')}
            </Form.Group>

            <Form.Group>
              <Form.Label>Description*</Form.Label>
              <Form.Control
                type="text"
                name="description"
                value={values.description}
                onChange={handleChange}
                onBlur={handleBlur}
                as="textarea"
                style={inputBorderColor(errors, touched, values, 'description')}
              />
              {checkInputField(errors, touched, 'description')}
            </Form.Group>

            <Button type="submit"> Submit!</Button>
          </Form>
        </>
      )}
    </Formik>
  );
};

export default ProjectForm;
