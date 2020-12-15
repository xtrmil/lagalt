import React, { useState } from 'react';
import { Card } from 'react-bootstrap';
import ProjectUpdatesComponent from './ProjectUpdatesComponent';
import ProjectViewMainSection from './ProjectViewMainSection';
import ProjectSettingsModal from '../project/ProjectSettingsModal';
import JoinProjectModal from './JoinProjectModal';

const ProjectViewComponent = (props) => {
  const [showJoinModal, setShowJoinModal] = useState(false);
  const [showProjectSettingsModal, setShowProjectSettingsModal] = useState(false);
  const { isAdmin, loggedIn, memberOf } = props;
  const [project, setProject] = useState(props.project);

  const onJoinClick = () => {
    setShowJoinModal(true);
  };
  const onSettingsClick = () => {
    setShowProjectSettingsModal(true);
  };
  const hideJoinModal = () => {
    setShowJoinModal(false);
  };
  const hideProjectSettingsModal = () => {
    setShowProjectSettingsModal(false);
  };
  return (
    <>
      <JoinProjectModal showJoinModal={showJoinModal} hideJoinModal={hideJoinModal} />
      <ProjectSettingsModal
        project={project}
        setProject={setProject}
        showModal={showProjectSettingsModal}
        hideModal={hideProjectSettingsModal}
      />
      <Card>
        <Card.Body>
          <ProjectViewMainSection
            project={project}
            isAdmin={isAdmin}
            loggedIn={loggedIn}
            memberOf={memberOf}
            onJoinClick={onJoinClick}
            onSettingsClick={onSettingsClick}
          />
          <ProjectUpdatesComponent />
        </Card.Body>
      </Card>
    </>
  );
};

export default ProjectViewComponent;
