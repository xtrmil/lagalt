import React from 'react';
import './LoginPage.css';
import { Formik } from 'formik';
import TextInput from '../components/form/TextInput';
import { Button, Form, Card, Container } from 'react-bootstrap';
import { loginSchema } from '../utils/form/FormUtils';
import { Link } from 'react-router-dom';

const LoginPage = (props) => {
  const initialValues = { email: '', password: '' };

  return (
    <>
      <h1 className="loginHeading">Log in</h1>
      <h4 className="registerInvite">
        <Link to="/register">Not having a Lagalt account? Register here</Link>
      </h4>
      <Container>
        <Card>
          <Card.Body>
            <Formik
              validationSchema={loginSchema}
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
                        Continue with Google
                      </Button>
                    </div>
                    <div>
                      <Button type="submit">Log in</Button>
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

export default LoginPage;
