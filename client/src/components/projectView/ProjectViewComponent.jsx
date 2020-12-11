import React from 'react';
import { Card } from 'react-bootstrap';
import ProjectUpdatesComponent from './ProjectUpdatesComponent';
import ProjectViewMainSection from './ProjectViewMainSection';

const ProjectViewComponent = (props) => {
  const { isAdmin, loggedIn, memberOf, onJoinClick } = props;

  return (
    <Card>
      <Card.Body>
        <ProjectViewMainSection
          isAdmin={isAdmin}
          loggedIn={loggedIn}
          memberOf={memberOf}
          onJoinClick={onJoinClick}
        />
        <ProjectUpdatesComponent />
      </Card.Body>
    </Card>
  );
};

export default ProjectViewComponent;
