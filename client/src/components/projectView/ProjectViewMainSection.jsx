import React from 'react';
import { Col, Row, Button } from 'react-bootstrap';
import ProjectResourcesComponent from './ProjectResourcesComponent';

const ProjectViewMainSection = (props) => {
  const { project, isAdmin, loggedIn, memberOf, onJoinClick, onSettingsClick } = props;
  const industry = {
    value: Object.keys(project.industry)[0],
    label: Object.values(project.industry)[0],
  };
  const tagsArray = Object.values(project.tags);

  const membersList =
    project.members != null ? (
      project.members.map((member, index) => {
        return <div key={index}>{member}</div>;
      })
    ) : (
      <div>No members</div>
    );
  const onMessageBoardAndChatPageClick = () => {};

  const tagsList = tagsArray.map((tag, index) => {
    return (
      <Col
        sm={5}
        className={
          index % 2 == 0 ? 'mr-1 mt-1 skill odd text-center' : 'mr-1 mt-1 skill text-center'
        }
        key={index}
      >
        {tag}
      </Col>
    );
  });
  return (
    <Row className="mt-3 ml-2">
      <Col sm={4}>
        <div className="imgplaceholder mb-4">IMAGE</div>
        <div className="mb-4">
          <h3 className="mb-2 text-center">Skills</h3>
          <div className="text-center">{tagsList}</div>
        </div>
        <h3 className="mb-2 text-center">Members</h3>
        <div className="text-center">{membersList}</div>
      </Col>
      <Col sm={8} className="pl-0">
        <Row className="no-gutters">
          <Col sm={8}>
            <h2>{project.title}</h2>
            <h4>
              <i>Industry: {industry.label}</i>
            </h4>
            <h5>Status: {project.status}</h5>
          </Col>
          <Col sm={4}>
            <div className="mr-4 text-right">
              {loggedIn && !memberOf && (
                <Button variant="success" onClick={onJoinClick}>
                  Join
                </Button>
              )}
              {loggedIn && isAdmin && (
                <Button className="mb-2" onClick={onSettingsClick}>
                  Settings
                </Button>
              )}
              {loggedIn && memberOf && (
                <Button onClick={onMessageBoardAndChatPageClick} variant="info">
                  Message Board & Chat
                </Button>
              )}
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
