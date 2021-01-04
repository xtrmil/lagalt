import { Formik } from 'formik';
import React from 'react';
import { Button, Form } from 'react-bootstrap';
import { deletePostSchema } from '../utils/form/FormUtils';

const DeletePostForm = () => {
  const initialValues = { deleteText: '' };
  const onFormSubmit = (values) => {
    console.log('Values from the form: ', values);
    const application = { deleteText: values.motivation };
    console.log('This should be the application', application);
  };

  const onMessageBoardPostClick = () => {
    props.history.push('/message_board_post');
  };

  return (
    <Formik
      validationSchema={deletePostSchema}
      initialValues={initialValues}
      onSubmit={(values) => onFormSubmit(values)}
    >
      {({ handleSubmit }) => (
        <Form onSubmit={handleSubmit}>
          <Button onClick={onMessageBoardPostClick} type="submit" variant="danger">
            Delete
          </Button>
          <button
            onClick={onMessageBoardPostClick}
            type="button"
            className="btn btn-outline-secondary"
          >
            Cancel
          </button>
        </Form>
      )}
    </Formik>
  );
};

export default DeletePostForm;
