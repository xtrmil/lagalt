import React from 'react';
import './RegisterPage.css';
import { Formik } from 'formik';
import TextInput from '../components/form/TextInput';
import { Button, Form } from 'react-bootstrap';
import { registerSchema } from '../utils/form/FormUtils';

const RegisterPage = (props) => {
  const initialValues = { name: '', email: '', password: '' };

  return (
    <>
      <h1 className="registerHeading">Register</h1>

      <Formik
        validationSchema={registerSchema}
        onSubmit={(values) => onFormSubmit(values)}
        initialValues={initialValues}
      >
        {({
          handleSubmit,
          handleChange,
          handleBlur,

          values,
          touched,
          errors,
        }) => (
          <>
            <Form onSubmit={handleSubmit}>
              <TextInput
                type="text"
                label="Name"
                name="name"
                values={values}
                touched={touched}
                errors={errors}
                handleChange={handleChange}
                handleBlur={handleBlur}
                textarea="textarea"
              ></TextInput>

              <TextInput
                type="text"
                label="Email"
                name="email"
                values={values}
                touched={touched}
                errors={errors}
                handleChange={handleChange}
                handleBlur={handleBlur}
                textarea="textarea"
              ></TextInput>

              <TextInput
                type="text"
                label="Password"
                name="password"
                values={values}
                touched={touched}
                errors={errors}
                handleChange={handleChange}
                handleBlur={handleBlur}
                textarea="textarea"
              ></TextInput>
              <div>
                <Button variant="info">Continue with Google</Button>
              </div>
              <div>
                <Button type="submit">Register</Button>
              </div>
            </Form>
          </>
        )}
      </Formik>
    </>
  );
};

export default RegisterPage;