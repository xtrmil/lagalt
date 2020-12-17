import React, { useEffect, useState, useCallback } from 'react';
import { Button, Form } from 'react-bootstrap';
import { Formik } from 'formik';
import TextInput from '../form/TextInput';
import SelectInput from '../form/SelectInput';
import MultiSelectInput from '../form/MultiSelectInput';
import { createProjectSchema } from '../../utils/form/FormUtils';
import { getAllIndustries, getTagsByIndustry } from '../../utils/api/industry';
const ProjectSettingsForm = (props) => {
  const { project, setProject, hideModal } = props;
  const [industryOptions, setIndustryOptions] = useState();
  const [tagOptions, setTagOptions] = useState();
  const [initialTags] = useState(
    project.tags.map((tag) => ({
      value: tag,
      label: tag,
    })),
  );
  const [initialValues] = useState({
    title: project.title,
    industry: project.industry,
    tags: initialTags,
    description: project.description,
  });
  const currentIndustry = { value: project.industry, label: project.industry };

  const mapOptions = (array, data) => {
    for (const [key, value] of Object.entries(data)) {
      const option = { value: key, label: value };
      array.push(option);
    }
    return array;
  };
  const fetchTagOptions = useCallback(async (industry) => {
    let tagOptions = [];
    await getTagsByIndustry(industry).then((response) => {
      mapOptions(tagOptions, response.data);
      setTagOptions(tagOptions);
    });
  }, []);
  const fetchIndustries = useCallback(async () => {
    let industriesOptions = [];
    await getAllIndustries().then((response) => {
      mapOptions(industriesOptions, response.data);
      setIndustryOptions(industriesOptions);
    });
  }, []);

  useEffect(() => {
    fetchTagOptions('Game');
    fetchIndustries();
  }, [fetchIndustries, fetchTagOptions]);

  const onFormSubmit = (values) => {
    const { title, industry, tags, description } = values;
    const newProject = {
      ...project,
      title,
      industry,
      tags: tags.map((tag) => tag.value),
      description,
    };
    console.log(newProject);
    setProject(newProject);
    hideModal();
  };

  const onIndustryChange = (selected, setFieldValue) => {
    setFieldValue('industry', selected.value);
    setFieldValue('tags', null);
    let label = selected.label;
    label = label.split(' ')[0];
    console.log(label);
    fetchTagOptions(label);
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
              onChange={(selected) => onIndustryChange(selected, setFieldValue)}
            />
            <MultiSelectInput
              label="Select Tags*"
              name="tags"
              options={tagOptions}
              values={values}
              touched={touched}
              errors={errors}
              defaultValue={initialTags}
              setFieldTouched={setFieldTouched}
              setFieldValue={setFieldValue}
            ></MultiSelectInput>
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
