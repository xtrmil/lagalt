import React, { useEffect, useState, useCallback } from 'react';
import { Modal, Button, Form } from 'react-bootstrap';
import { Formik } from 'formik';
import TextInput from '../form/TextInput';
import SelectInput from '../form/SelectInput';
import MultiSelectInput from '../form/MultiSelectInput';
import { editProfileSchema } from '../../utils/form/FormUtils';
import { getAllTags } from '../../utils/api/industry';
const ProfileModal = (props) => {
  const { showModal, handleCloseModal, handleSaveChanges, user } = props;
  const [tagOptions, setTagOptions] = useState();
  const mapOptions = (array, data) => {
    for (const [key, value] of Object.entries(data)) {
      const option = { value: key, label: value };
      array.push(option);
    }
    return array;
  };
  const currentTags = mapOptions([], user.tags);
  const fetchTags = useCallback(async () => {
    await getAllTags().then((response) => {
      const temp = mapOptions([], response.data);
      setTagOptions(temp);
    });
  }, []);
  useEffect(() => {
    fetchTags();
  }, [fetchTags]);

  const initialValues = {
    name: user.name,
    description: user.description,
    tags: currentTags,
    email: user.email,
    hidden: user.hidden,
  };

  const currentStatus = { value: user.hidden, label: user.hidden ? 'Hidden' : 'Public' };

  const profileStatusOptions = [
    { value: true, label: 'Hidden' },
    { value: false, label: 'Public' },
  ];
  const onFormSubmit = (values) => {
    const { tags } = values;
    const updatedUser = {
      ...user,
      ...values,
      hidden: values.hidden.label ? values.hidden.value : values.hidden,
      tags: tags.reduce((acc, cur) => ({ ...acc, [cur.value]: cur.label }), {}),
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
                    name="tags"
                    options={tagOptions}
                    values={values}
                    touched={touched}
                    errors={errors}
                    defaultValue={currentTags}
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
