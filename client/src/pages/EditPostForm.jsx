import { Formik } from 'formik';
import React from 'react';
import { Form, Button } from 'react-bootstrap';
import TextInput from '../components/form/TextInput';
import { editPostSchema } from '../utils/form/FormUtils';
const EditPostForm = () => {
  const initialValues = { topicText: '' };

  const onFormSubmit = (values) => {
    console.log('Values from the form: ', values);
    const application = { topicText: values.motivation };
    console.log('This should be the application', application);
  };

  return (
    <Formik
      validationSchema={editPostSchema}
      initialValues={initialValues}
      onSubmit={(values) => onFormSubmit(values)}
    >
      {({ errors, values, touched, handleChange, handleBlur, handleSubmit }) => (
        <Form onSubmit={handleSubmit}>
          <TextInput
            type="text"
            label="Topic heading"
            name="topicHeading"
            handleChange={handleChange}
            handleBlur={handleBlur}
            errors={errors}
            touched={touched}
            values={values}
            textarea="textarea"
          ></TextInput>
          <TextInput
            type="text"
            label="Topic text"
            name="topicText"
            handleChange={handleChange}
            handleBlur={handleBlur}
            errors={errors}
            touched={touched}
            values={values}
            textarea="textarea"
          ></TextInput>

          <Button type="submit" variant="success">
            Save changes
          </Button>
        </Form>
      )}
    </Formik>
  );
};

export default EditPostForm;
