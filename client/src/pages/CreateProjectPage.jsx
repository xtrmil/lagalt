import React from 'react';
import './CreateProjectPage.css';
import { Container, Card } from 'react-bootstrap';
import ProjectForm from '../components/ProjectForm';

const CreateProjectPage = (props) => {
  return (
    <Container>
      <h1 className="createProjectHeading">Create a new project</h1>
      <Card>
        <Card.Body>
          <ProjectForm />
        </Card.Body>
      </Card>
    </Container>
  );
};

export default CreateProjectPage;
