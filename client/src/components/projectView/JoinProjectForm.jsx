import { Formik } from 'formik';
import React from 'react';
import { Form, Button } from 'react-bootstrap';
import TextInput from '../form/TextInput';
import CheckboxInput from '../form/CheckboxInput';
import { joinProjectSchema } from '../../utils/form/FormUtils';
const JoinProjectForm = () => {
  const initialValues = { motivation: '', acceptTerms: false };

  const onFormSubmit = (values) => {
    console.log('Values from the form: ', values);
    const application = { motivation: values.motivation };
    console.log('This should be the application', application);
  };

  return (
    <Formik
      validationSchema={joinProjectSchema}
      initialValues={initialValues}
      onSubmit={(values) => onFormSubmit(values)}
    >
      {({ errors, values, touched, handleChange, handleBlur, handleSubmit }) => (
        <Form onSubmit={handleSubmit}>
          <TextInput
            type="text"
            label="Motivation"
            name="motivation"
            handleChange={handleChange}
            handleBlur={handleBlur}
            errors={errors}
            touched={touched}
            values={values}
            textarea="textarea"
          ></TextInput>

          <CheckboxInput
            name="acceptTerms"
            label="I am aware that by applying to this project, I give permission to the project admins to
            see my profile regardless of my profile status."
            errors={errors}
            touched={touched}
            handleChange={handleChange}
            handleBlur={handleBlur}
          ></CheckboxInput>

          <Button type="submit" variant="success">
            Apply now!
          </Button>
        </Form>
      )}
    </Formik>
  );
};

export default JoinProjectForm;
