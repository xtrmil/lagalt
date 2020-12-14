import React from 'react';
import { Button, Form } from 'react-bootstrap';
import { Formik } from 'formik';
import TextInput from '../form/TextInput';
import SelectInput from '../form/SelectInput';
import { createProjectSchema } from '../../utils/form/FormUtils';

const ProjectSettingsForm = (props) => {
  const { project, setProject } = props;

  const currentSkills = project.skills.map((skill) => ({
    value: skill,
    label: skill,
  }));
  const initialValues = {
    title: project.title,
    industry: project.industry,
    skills: currentSkills,
    description: project.description,
  };
  const currentIndustry = { value: project.industry, label: project.industry };

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
    const { title, industry, skills, description } = values;
    console.log(skills);
    const newProject = {
      ...project,
      title,
      industry,
      skills: skills.map((skill) => skill.value),
      description,
    };
    console.log(newProject);
    setProject(newProject);
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
              defaultValue={currentIndustry}
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
              defaultValue={currentSkills}
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
            <div className="text-center">
              <Button type="submit" variant="success">
                Save Changes
              </Button>
            </div>
          </Form>
        </>
      )}
    </Formik>
  );
};

export default ProjectSettingsForm;
