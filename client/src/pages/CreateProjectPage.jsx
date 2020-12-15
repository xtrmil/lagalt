import React from 'react';
import { Container, Card } from 'react-bootstrap';
import ProjectForm from '../components/ProjectForm';

const CreateProjectPage = (props) => {
  return (
    <Container className="justify-content-center">
      <Card>
        <Card.Body>
          <h1 className="text-center">Create a new project!</h1>
          <ProjectForm />
        </Card.Body>
      </Card>
    </Container>
  );
};

export default CreateProjectPage;
