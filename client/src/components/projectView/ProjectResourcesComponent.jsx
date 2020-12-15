import React from 'react';
import { Row, Card, Col } from 'react-bootstrap';

const ProjectResourcesComponent = (props) => {
  const { placeholdertext } = props;
  return (
    <Row className=" mt-3 no-gutters">
      <Col sm={12}>
        <h3>Resources</h3>
      </Col>

      <Col sm={12}>
        <Card>
          <Card.Body>
            <Row className="mb-4 no-gutters">
              <Col sm={12}>
                <h6>Public screen/photos/progress</h6>
              </Col>
              <Col sm={12}>
                <div>{placeholdertext}</div>
              </Col>
            </Row>
            <Row className="no-gutters">
              <Col sm={12}>
                <h6>Git commits</h6>
              </Col>
              <Col sm={12}>
                <div>{placeholdertext}</div>
              </Col>
            </Row>
          </Card.Body>
        </Card>
      </Col>
    </Row>
  );
};

export default ProjectResourcesComponent;
