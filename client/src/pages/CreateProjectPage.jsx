import React, { useState } from 'react';
import './CreateProjectPage.css';
import { Container, Card, Modal } from 'react-bootstrap';
import ProjectForm from '../components/ProjectForm';
import { createProject } from '../utils/api/project';

const CreateProjectPage = (props) => {
  const { loggedInUser, history } = props;
  const [success, setSuccess] = useState(false);
  const [projectName, setProjectName] = useState();
  const onFormSubmit = async (values) => {
    const { title, description, industry, tags } = values;

    const project = {
      title,
      description,
      industry: industry.reduce((acc, cur) => ({ ...acc, [cur.value]: cur.label }), {}),
      tags: tags.reduce((acc, cur) => ({ ...acc, [cur.value]: cur.label }), {}),
    };
    await createProject(project).then((response) => {
      console.log(response);
      setProjectName(project.title.replace(/ /g, '-'));
      if (response.status === 201) {
        setSuccess(true);
      }
    });
  };
  return (
    <Container>
      <Modal
        show={success}
        onHide={() => {
          setSuccess(false);
          history.push(`/project/${loggedInUser}/${projectName}`);
        }}
      >
        <Modal.Header closeButton>
          <Modal.Body>Project created successfully!</Modal.Body>
        </Modal.Header>
      </Modal>
      <h1 className="createProjectHeading">Create a new project</h1>
      <Card>
        <Card.Body>
          <ProjectForm loggedInUser={loggedInUser} onFormSubmit={onFormSubmit} />
        </Card.Body>
      </Card>
    </Container>
  );
};

export default CreateProjectPage;
