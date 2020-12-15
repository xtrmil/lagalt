import React from 'react';
import { Button, Form } from 'react-bootstrap';
import { Formik } from 'formik';
import SelectInput from './form/SelectInput';
import TextInput from './form/TextInput';
import { updateProjectSchema } from '../utils/form/FormUtils';
const AdminForm = () => {
  const initialValues = { updateText: '', progress: '' };

  const progressOptions = [
    { value: 'FOUNDING', label: 'Founding' },
    { value: 'IN_PROGRESS', label: 'In progress' },
    { value: 'STALLED', label: 'Stalled' },
    { value: 'COMPLETED', label: 'Completed' },
  ];

  const onFormSubmit = (values) => {
    console.log(values);
  };
  return (
    <Formik
      validationSchema={updateProjectSchema}
      onSubmit={(values) => onFormSubmit(values)}
      initialValues={initialValues}
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
            <TextInput
              type="text"
              label="Project updates (public)*"
              name="updateText"
              values={values}
              touched={touched}
              errors={errors}
              handleChange={handleChange}
              handleBlur={handleBlur}
              textarea="textarea"
            ></TextInput>
            <SelectInput
              label="Progress*"
              name="progress"
              options={progressOptions}
              values={values}
              touched={touched}
              errors={errors}
              setFieldTouched={setFieldTouched}
              setFieldValue={setFieldValue}
              isMulti={false}
            ></SelectInput>
            <Button className="btn-save" type="submit" variant="success">
              Save
            </Button>
          </Form>
        </>
      )}
    </Formik>
  );
};

export default AdminForm;
