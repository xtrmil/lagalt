import React from 'react';
import { Col, Row, Button } from 'react-bootstrap';
import ProjectResourcesComponent from './ProjectResourcesComponent';

const ProjectViewMainSection = (props) => {
  const { project, isAdmin, loggedIn, memberOf, onJoinClick, onSettingsClick } = props;
  const { skills } = project;

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
            <h2>{project.title}</h2>
            <h4>
              <i>Industry: {project.industry}</i>
            </h4>
            <h5>Status: {project.status}</h5>
          </Col>
          <Col sm={4}>
            <div className="mr-4 text-right">
              {loggedIn && !memberOf && (
                <Button variant="success" onClick={onJoinClick}>
                  Join!
                </Button>
              )}
              {loggedIn && isAdmin && (
                <Button className="mb-2" onClick={onSettingsClick}>
                  Settings
                </Button>
              )}
              {loggedIn && memberOf && <Button>Message Board & Chat</Button>}
            </div>
          </Col>
        </Row>
        <Row className="mt-3 no-gutters">
          <Col sm={12}>
            <h6>Project Description</h6>
          </Col>
          <Col sm={12}>
            <div>{project.description}</div>
          </Col>
        </Row>
        {loggedIn && memberOf && (
          <ProjectResourcesComponent placeholdertext={project.description} />
        )}
      </Col>
    </Row>
  );
};

export default ProjectViewMainSection;
