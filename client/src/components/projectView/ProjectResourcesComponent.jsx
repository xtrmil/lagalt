import React from 'react';
import { Row, Card } from 'react-bootstrap';

const ProjectResourcesComponent = (props) => {
  const { placeholdertext } = props;
  return (
    <Row className=" mt-3 no-gutters">
      <h3>Resources</h3>
      <Card>
        <Card.Body>
          <Row className="mb-4 no-gutters">
            <h6>Public screen/photos/progress</h6>
            <div>{placeholdertext}</div>
          </Row>
          <Row className="no-gutters">
            <h6>Git commits</h6>
            <div>{placeholdertext}</div>
          </Row>
        </Card.Body>
      </Card>
    </Row>
  );
};

export default ProjectResourcesComponent;
