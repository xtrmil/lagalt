import React, { useState } from 'react';
import { Card, Col, Container, Row, Button, Modal, Form } from 'react-bootstrap';

const ProjectViewPage = (props) => {
  const [showJoinModal, setShowJoinModal] = useState(false);
  const isAdmin = false;
  const loggedIn = true;
  const memberOf = true;
  const skills = ['WEB_DEV', 'SECURITY', 'REACT', 'ANGULAR'];
  const placeholdertext =
    'Lorem ipsum dolor sit amet consectetur adipisicing elit. Totam ea hic eaque cumque asperiores nisi eligendi explicabo voluptatibus aliquid omnis, a atque magnam iure facilis laudantium! Quidem illo doloribus itaque maxime recusandae explicabo nulla quaerat nemo est blanditiis veritatis omnis neque vero praesentium laudantium officia consectetur non atque repudiandae, quia dolor debitis! Nisi, aut. Ducimus voluptatem cumque necessitatibus sapiente accusantium minus laborum alias quibusdam dolor, dolorum sequi deserunt explicabo iure ad sunt nesciunt repudiandae officiis, ipsa similique, exercitationem doloribus! Aspernatur recusandae quos similique eos rem dicta esse repellat, inventore laboriosam! At impedit voluptas delectus. Soluta tempore ab accusamus impedit sit?';

  const skillsList = skills.map((skill, index) => {
    return (
      <Col
        sm={5}
        className={
          index % 2 == 0 ? 'mr-1 mt-1 skill odd text-center' : 'mr-1 mt-1 skill text-center'
        }
        key={index}
      >
        {skill}
      </Col>
    );
  });

  const onJoinClick = () => {
    setShowJoinModal(true);
  };
  const hideJoinModal = () => {
    setShowJoinModal(false);
  };
  return (
    <Container className="justify-content-center">
      <Modal show={showJoinModal} onHide={hideJoinModal}>
        <Modal.Body className="mx-3">
          <h2 className="text-center mb-5">Apply to this project</h2>
          <Form>
            <Form.Group>
              <Form.Label>Motivation</Form.Label>
              <Form.Control type="text" as="textarea"></Form.Control>
            </Form.Group>
            <Form.Group>
              <Form.Label></Form.Label>
            </Form.Group>
            <Button type="submit" variant="success">
              Apply now!
            </Button>
          </Form>
        </Modal.Body>
      </Modal>
      <Card>
        <Card.Body>
          <Row className="mt-3 ml-2">
            <Col sm={4}>
              <div className="imgplaceholder mb-4">IMAGE</div>
              <div className="mb-4">
                <h3 className="mb-2 text-center">Skills</h3>
                <div className="text-center">{skillsList}</div>
              </div>
              <h3 className="mb-2 text-center">Members</h3>
              <div className="text-center">
                <div>there should be a list of people here</div>
                <div>there should be a list of people here</div>
                <div>there should be a list of people here</div>
                <div>there should be a list of people here</div>
                <div>there should be a list of people here</div>
              </div>
            </Col>
            <Col sm={8} className="pl-0">
              <Row className="no-gutters">
                <Col sm={8}>
                  <h2>Project Name!</h2>
                  <h4>
                    <i>Industry: Music</i>
                  </h4>
                  <h5>Status: In progress</h5>
                </Col>
                <Col sm={4}>
                  <div className="mr-4 text-right">
                    {loggedIn && !memberOf && (
                      <Button variant="success" onClick={onJoinClick}>
                        Join!
                      </Button>
                    )}
                    {loggedIn && isAdmin && <Button className="mb-2">Settings</Button>}
                    {loggedIn && memberOf && <Button>Message Board & Chat</Button>}
                  </div>
                </Col>
              </Row>
              <Row className="mt-3 no-gutters">
                <h6>Project Description</h6>
                <div>{placeholdertext}</div>
              </Row>
              {loggedIn && memberOf && (
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
              )}
            </Col>
          </Row>
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
                        Lorem ipsum dolor sit amet consectetur adipisicing elit. Assumenda id
                        voluptate odit sequi inventore quisquam autem accusantium provident sunt
                        odio! Tempore dolorem corporis aut corrupti dolorum sint adipisci molestiae
                        ducimus.
                      </div>
                    </Col>
                    <Col sm={3} className="text-center">
                      <div>2020-08-12</div>
                    </Col>
                  </Row>
                  <Row className="border-bottom pb-2 mt-2">
                    <Col sm={9}>
                      <div>
                        Lorem ipsum dolor sit amet consectetur adipisicing elit. Assumenda id
                        voluptate odit sequi inventore quisquam autem accusantium provident sunt
                        odio! Tempore dolorem corporis aut corrupti dolorum sint adipisci molestiae
                        ducimus.
                      </div>
                    </Col>
                    <Col sm={3} className=" text-center">
                      <div>2020-08-12</div>
                    </Col>
                  </Row>
                  <Row className="mt-2">
                    <Col sm={9}>
                      <div>
                        Lorem ipsum dolor sit amet consectetur adipisicing elit. Assumenda id
                        voluptate odit sequi inventore quisquam autem accusantium provident sunt
                        odio! Tempore dolorem corporis aut corrupti dolorum sint adipisci molestiae
                        ducimus.
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
        </Card.Body>
      </Card>
    </Container>
  );
};
export default ProjectViewPage;
