import React, { useState, useEffect, useCallback } from 'react';
import { Button, Form } from 'react-bootstrap';
import { Formik } from 'formik';
import TextInput from './form/TextInput';
import SelectInput from './form/SelectInput';
import MultiSelectInput from './form/MultiSelectInput';
import { createProjectSchema } from '../utils/form/FormUtils';
import { createProject } from '../utils/api/project';
import { getAllIndustries, getTagsByIndustry } from '../utils/api/industry';
const ProjectForm = () => {
  const initialValues = { title: '', description: '', tags: null, industry: null };
  const [industryOptions, setIndustryOptions] = useState();
  const [tagOptions, setTagOptions] = useState();

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
    fetchIndustries();
  }, [fetchIndustries, fetchTagOptions]);

  const onIndustryChange = (selected, setFieldValue) => {
    setFieldValue('industry', [selected]);
    setFieldValue('tags', null);
    let label = selected.label;
    label = label.split(' ')[0];
    fetchTagOptions(label);
  };

  const onFormSubmit = (values) => {
    const { title, description, industry, tags } = values;

    const project = {
      title,
      description,
      industry: industry.reduce((acc, cur) => ({ ...acc, [cur.value]: cur.label }), {}),
      tags: tags.reduce((acc, cur) => ({ ...acc, [cur.value]: cur.label }), {}),
    };
    createProject(project);
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
              onChange={(selected) => onIndustryChange(selected, setFieldValue)}
              isMulti={false}
            ></SelectInput>
            <MultiSelectInput
              label="Select tags*"
              name="tags"
              options={tagOptions}
              values={values}
              touched={touched}
              errors={errors}
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

            <button type="button" className="btn btn-success">
              Submit
            </button>
          </Form>
        </>
      )}
    </Formik>
  );
};

export default ProjectForm;
