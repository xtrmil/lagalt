import React, { useEffect, useState, useCallback } from 'react';
import { Button, Form } from 'react-bootstrap';
import { Formik } from 'formik';
import TextInput from '../form/TextInput';
import SelectInput from '../form/SelectInput';
import MultiSelectInput from '../form/MultiSelectInput';
import { editProjectSchema } from '../../utils/form/FormUtils';
import { getAllIndustries, getTagsByIndustry } from '../../utils/api/industry';
import { updateProject } from '../../utils/api/project';
import { mapOptions } from '../../utils/MapOptions';

const ProjectSettingsForm = (props) => {
  const { project, setProject, hideModal } = props;
  const [industryOptions, setIndustryOptions] = useState();
  const [tagOptions, setTagOptions] = useState();
  const initialTags = mapOptions([], project.tags);
  const initialIndustry = mapOptions([], project.industry);
  const initialTagSelect = initialIndustry[0].label.split(' ')[0];

  const [initialValues] = useState({
    industry: initialIndustry,
    tags: initialTags,
    description: project.description,
  });

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
    fetchTagOptions(initialTagSelect);
    fetchIndustries();
  }, [fetchIndustries, fetchTagOptions, initialTagSelect]);

  const onFormSubmit = (values) => {
    const { industry, tags, description } = values;
    const { owner, title } = project;
    const newProject = {
      industry: industry.reduce((acc, cur) => ({ ...acc, [cur.value]: cur.label }), {}),
      tags: tags.reduce((acc, cur) => ({ ...acc, [cur.value]: cur.label }), {}),
      description,
    };
    setProject(newProject);
    console.log(JSON.stringify(newProject, null, 2));
    updateProject(newProject, owner, title);
    hideModal();
  };

  const onIndustryChange = (selected, setFieldValue) => {
    setFieldValue('industry', [selected]);
    setFieldValue('tags', null);
    let label = selected.label;
    label = label.split(' ')[0];
    fetchTagOptions(label);
  };
  return (
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
            <SelectInput
              label="Select Industry*"
              name="industry"
              options={industryOptions}
              values={values}
              touched={touched}
              errors={errors}
              defaultValue={initialIndustry}
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
