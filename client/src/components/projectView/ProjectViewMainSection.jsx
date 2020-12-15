import React from 'react';
import { Col, Row, Button } from 'react-bootstrap';
import ProjectResourcesComponent from './ProjectResourcesComponent';

const ProjectViewMainSection = (props) => {
  const { isAdmin, loggedIn, memberOf, onJoinClick } = props;
  const placeholdertext =
    'Lorem ipsum dolor sit amet consectetur adipisicing elit. Totam ea hic eaque cumque asperiores nisi eligendi explicabo voluptatibus aliquid omnis, a atque magnam iure facilis laudantium! Quidem illo doloribus itaque maxime recusandae explicabo nulla quaerat nemo est blanditiis veritatis omnis neque vero praesentium laudantium officia consectetur non atque repudiandae, quia dolor debitis! Nisi, aut. Ducimus voluptatem cumque necessitatibus sapiente accusantium minus laborum alias quibusdam dolor, dolorum sequi deserunt explicabo iure ad sunt nesciunt repudiandae officiis, ipsa similique, exercitationem doloribus! Aspernatur recusandae quos similique eos rem dicta esse repellat, inventore laboriosam! At impedit voluptas delectus. Soluta tempore ab accusamus impedit sit?';
  const skills = ['WEB_DEV', 'SECURITY', 'REACT', 'ANGULAR'];

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
  return (
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
                  Join
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
        {loggedIn && memberOf && <ProjectResourcesComponent placeholdertext={placeholdertext} />}
      </Col>
    </Row>
  );
};

export default ProjectViewMainSection;
