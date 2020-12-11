import React from 'react';
import { Button, Form } from 'react-bootstrap';
import { Formik } from 'formik';
import TextInput from './form/TextInput';
import SelectInput from './form/SelectInput';
const ProjectForm = () => {
  const initialValues = { title: '', description: '', skills: null, industry: '' };

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
    const project = {
      ...values,
      industry: values.industry,
      skills: values.skills.map((skill) => skill.value),
    };
    console.log(project);
  };
  return (
    <Formik
      validationSchema={createProjectSchema}
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
              label="Project Name*"
              name="title"
              values={values}
              touched={touched}
              errors={errors}
              handleChange={handleChange}
              handleBlur={handleBlur}
            ></TextInput>
            <SelectInput
              label="Select Industry*"
              name="industry"
              options={industryOptions}
              values={values}
              touched={touched}
              errors={errors}
              setFieldTouched={setFieldTouched}
              setFieldValue={setFieldValue}
              isMulti={false}
            ></SelectInput>
            <SelectInput
              label="Select Skills*"
              name="skills"
              options={options}
              values={values}
              touched={touched}
              errors={errors}
              setFieldTouched={setFieldTouched}
              setFieldValue={setFieldValue}
              isMulti={true}
            ></SelectInput>
            <TextInput
              type="text"
              label="Description*"
              name="description"
              values={values}
              touched={touched}
              errors={errors}
              handleChange={handleChange}
              handleBlur={handleBlur}
              textarea="textarea"
            ></TextInput>

            <Button type="submit"> Submit!</Button>
          </Form>
        </>
      )}
    </Formik>
  );
};

export default ProjectForm;
