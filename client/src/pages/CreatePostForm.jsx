import { Formik } from 'formik';
import React from 'react';
import { Form, Button } from 'react-bootstrap';
import TextInput from '../components/form/TextInput';
import { createPostSchema } from '../utils/form/FormUtils';
const CreatePostForm = () => {
  const initialValues = { topicText: '' };

  const onFormSubmit = (values) => {
    console.log('Values from the form: ', values);
    const application = { topicText: values.motivation };
    console.log('This should be the application', application);
  };

  return (
    <Formik
      validationSchema={createPostSchema}
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
            Create post
          </Button>
        </Form>
      )}
    </Formik>
  );
};

export default CreatePostForm;
