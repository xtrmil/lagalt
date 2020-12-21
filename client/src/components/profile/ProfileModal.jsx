import React from 'react';
import { Modal, Button, Form } from 'react-bootstrap';
import { Formik } from 'formik';
import TextInput from '../form/TextInput';
import SelectInput from '../form/SelectInput';
import MultiSelectInput from '../form/MultiSelectInput';
import { editProfileSchema } from '../../utils/form/FormUtils';
const ProfileModal = (props) => {
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

  const currentStatus = { value: user.hidden, label: user.hidden ? 'Hidden' : 'Public' };

  const options = [
    { value: 'DRUMMER', label: 'Drummer' },
    { value: 'WEB_DEV', label: 'WEB_DEV' },
    { value: 'REACT', label: 'REACT' },
    { value: 'SECURITY', label: 'SECURITY' },
    { value: 'ANGULAR', label: 'ANGULAR' },
  ];

  const profileStatusOptions = [
    { value: true, label: 'Hidden' },
    { value: false, label: 'Public' },
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
          <h3 className="mb-2 text-center">Edit your profile</h3>
          <Formik
            validationSchema={editProfileSchema}
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
                    label="Name*"
                    name="name"
                    values={values}
                    touched={touched}
                    errors={errors}
                    handleChange={handleChange}
                    handleBlur={handleBlur}
                  ></TextInput>

                  <MultiSelectInput
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
                  ></MultiSelectInput>

                  <SelectInput
                    label="Status*"
                    name="hidden"
                    options={profileStatusOptions}
                    values={values}
                    touched={touched}
                    errors={errors}
                    defaultValue={currentStatus}
                    setFieldTouched={setFieldTouched}
                    setFieldValue={setFieldValue}
                    isMulti={false}
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

                  <Button type="submit">Save Changes</Button>
                </Form>
              </>
            )}
          </Formik>
        </Modal.Body>
      </Modal.Header>
    </Modal>
  );
};
export default ProfileModal;
