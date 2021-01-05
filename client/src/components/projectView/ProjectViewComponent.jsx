import React, { useState } from 'react';
import { Card } from 'react-bootstrap';
import ProjectUpdatesComponent from './ProjectUpdatesComponent';
import ProjectViewMainSection from './ProjectViewMainSection';
import ProjectSettingsModal from '../project/ProjectSettingsModal';
import JoinProjectModal from './JoinProjectModal';
import { createApplication } from '../../utils/api/application';
const ProjectViewComponent = (props) => {
  const {
    project,
    setProject,
    isAdmin,
    loggedIn,
    memberOf,
    loggedInUser,
    hasApplied,
    setHasApplied,
  } = props;

  const [showJoinModal, setShowJoinModal] = useState(false);
  const [showProjectSettingsModal, setShowProjectSettingsModal] = useState(false);

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
  const handleJoinProject = (motivation) => {
    const { owner, title } = project;
    createApplication(owner, title, motivation, loggedInUser.username).then((response) => {
      hideJoinModal();
      setHasApplied(true);
    });
  };
  return (
    <>
      <JoinProjectModal
        showJoinModal={showJoinModal}
        hideJoinModal={hideJoinModal}
        handleJoinProject={handleJoinProject}
      />
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
            hasApplied={hasApplied}
          />
          <ProjectUpdatesComponent />
        </Card.Body>
      </Card>
    </>
  );
};

export default ProjectViewComponent;
