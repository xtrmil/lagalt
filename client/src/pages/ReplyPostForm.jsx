import { Formik } from 'formik';
import React from 'react';
import { Form, Button } from 'react-bootstrap';
import TextInput from '../components/form/TextInput';
import { replyPostSchema } from '../utils/form/FormUtils';
const ReplyPostForm = () => {
  const initialValues = { replyText: '' };
  const onFormSubmit = (values) => {
    console.log('Values from the form: ', values);
    const application = { replyText: values.motivation };
    console.log('This should be the application', application);
  };

  return (
    <Formik
      validationSchema={replyPostSchema}
      initialValues={initialValues}
      onSubmit={(values) => onFormSubmit(values)}
    >
      {({ errors, values, touched, handleChange, handleBlur, handleSubmit }) => (
        <Form onSubmit={handleSubmit}>
          <TextInput
            type="text"
            label="Reply"
            name="replyPost"
            handleChange={handleChange}
            handleBlur={handleBlur}
            errors={errors}
            touched={touched}
            values={values}
            textarea="textarea"
          ></TextInput>

          <Button type="submit" variant="success">
            Reply
          </Button>
        </Form>
      )}
    </Formik>
  );
};

export default ReplyPostForm;
