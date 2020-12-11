import React from 'react';
import { Modal, Button, Form } from 'react-bootstrap';
import { Formik } from 'formik';
import TextInput from '../form/TextInput';
import SelectInput from '../form/SelectInput';
import { editProjectSchema } from '../../utils/form/FormUtils';
const ProjectSettingsModal = (props) => {
  const { showModal, handleCloseModal, handleSaveChanges, user } = props;

  const currentSkills = user.skills.map((skill) => ({
    value: skill,
    label: skill,
  }));

  const initialValues = {
    name: user.name,
    description: user.description,
    skills: currentSkills,
    email: user.email,
    hidden: user.hidden,
  };

  const options = [
    { value: 'DRUMMER', label: 'Drummer' },
    { value: 'WEB_DEV', label: 'WEB_DEV' },
    { value: 'REACT', label: 'REACT' },
    { value: 'SECURITY', label: 'SECURITY' },
    { value: 'ANGULAR', label: 'ANGULAR' },
  ];

  const onFormSubmit = (values) => {
    const updatedUser = {
      ...user,
      ...values,
      hidden: values.hidden.label ? values.hidden.value : values.hidden,
      skills: values.skills.map((skill) => skill.value),
    };
    console.log(updatedUser);
    handleSaveChanges(updatedUser);
  };

  const onHideModal = () => {
    handleCloseModal();
  };

  return (
    <Modal show={showModal} onHide={onHideModal}>
      <Modal.Header className="border-0" closeButton>
        <Modal.Body>
          <Formik
            validationSchema={editProjectSchema}
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
                    label="Title*"
                    name="name"
                    values={values}
                    touched={touched}
                    errors={errors}
                    handleChange={handleChange}
                    handleBlur={handleBlur}
                  ></TextInput>

                  <SelectInput
                    label="Industry*"
                    name="industry"
                    options={industryOptions}
                    values={values}
                    touched={touched}
                    errors={errors}
                    setFieldTouched={setFieldTouched}
                    setFieldValue={setFieldValue}
                    isMulti={false}
                  ></SelectInput>

                  <TextInput
                    type="text"
                    label="Project description*"
                    name="project description"
                    values={values}
                    touched={touched}
                    errors={errors}
                    handleChange={handleChange}
                    handleBlur={handleBlur}
                  ></TextInput>

                  <SelectInput
                    label="Skills needed*"
                    name="skills needed"
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
                    label="Links*"
                    name="links"
                    values={values}
                    touched={touched}
                    errors={errors}
                    handleChange={handleChange}
                    handleBlur={handleBlur}
                  ></TextInput>

                  <SelectInput
                    label="Administrator(s)*"
                    name="administrator(s)"
                    options={industryOptions}
                    values={values}
                    touched={touched}
                    errors={errors}
                    setFieldTouched={setFieldTouched}
                    setFieldValue={setFieldValue}
                    isMulti={false}
                  ></SelectInput>

                  <Button type="submit">Save</Button>
                </Form>
              </>
            )}
          </Formik>
        </Modal.Body>
      </Modal.Header>
    </Modal>
  );
};
export default ProjectSettingsModal;
