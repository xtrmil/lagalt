import React from 'react';
import { Row, Col, Card } from 'react-bootstrap';

const ProjectUpdatesComponent = () => {
  return (
    <Row className="ml-2 mt-4 justify-content-center">
      <Col sm={12}>
        <div className="text-center">
          <h3>Project Updates</h3>
        </div>
        <Card>
          <Card.Body>
            <Row className="border-bottom pb-2 mt-2">
              <Col sm={9}>
                <div>
                  Lorem ipsum dolor sit amet consectetur adipisicing elit. Assumenda id voluptate
                  odit sequi inventore quisquam autem accusantium provident sunt odio! Tempore
                  dolorem corporis aut corrupti dolorum sint adipisci molestiae ducimus.
                </div>
              </Col>
              <Col sm={3} className="text-center">
                <div>2020-08-12</div>
              </Col>
            </Row>
            <Row className="border-bottom pb-2 mt-2">
              <Col sm={9}>
                <div>
                  Lorem ipsum dolor sit amet consectetur adipisicing elit. Assumenda id voluptate
                  odit sequi inventore quisquam autem accusantium provident sunt odio! Tempore
                  dolorem corporis aut corrupti dolorum sint adipisci molestiae ducimus.
                </div>
              </Col>
              <Col sm={3} className=" text-center">
                <div>2020-08-12</div>
              </Col>
            </Row>
            <Row className="mt-2">
              <Col sm={9}>
                <div>
                  Lorem ipsum dolor sit amet consectetur adipisicing elit. Assumenda id voluptate
                  odit sequi inventore quisquam autem accusantium provident sunt odio! Tempore
                  dolorem corporis aut corrupti dolorum sint adipisci molestiae ducimus.
                </div>
              </Col>
              <Col sm={3} className="text-center">
                <div>2020-08-12</div>
              </Col>
            </Row>
          </Card.Body>
        </Card>
      </Col>
    </Row>
  );
};

export default ProjectUpdatesComponent;
