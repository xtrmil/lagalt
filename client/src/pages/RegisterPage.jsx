import React from 'react';
import './RegisterPage.css';
import { Formik } from 'formik';
import TextInput from '../components/form/TextInput';
import { Button, Form, Card, Container } from 'react-bootstrap';
import { registerSchema } from '../utils/form/FormUtils';
import { Link } from 'react-router-dom';

const RegisterPage = (props) => {
  const initialValues = { name: '', email: '', password: '' };

  return (
    <>
      <h1 className="registerHeading">Register</h1>
      <h4 className="loginInvite">
        <Link to="/login">Already have a Lagalt account? Log in here</Link>
      </h4>
      <Container>
        <Card>
          <Card.Body>
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
                    ></TextInput>

                    <TextInput
                      type="password"
                      label="Password"
                      name="password"
                      values={values}
                      touched={touched}
                      errors={errors}
                      handleChange={handleChange}
                      handleBlur={handleBlur}
                    ></TextInput>

                    <div>
                      <Button className="google-button" variant="outline-secondary">
                        Sign up with Google
                      </Button>
                    </div>
                    <div>
                      <Button type="submit">Register</Button>
                    </div>
                  </Form>
                </>
              )}
            </Formik>
          </Card.Body>
        </Card>
      </Container>
    </>
  );
};

export default RegisterPage;
