import React, { useState } from 'react';
import { Card } from 'react-bootstrap';
import ProjectUpdatesComponent from './ProjectUpdatesComponent';
import ProjectViewMainSection from './ProjectViewMainSection';
import ProjectSettingsModal from '../project/ProjectSettingsModal';
import JoinProjectModal from './JoinProjectModal';
import { createApplication } from '../../utils/api/application';
const ProjectViewComponent = (props) => {
  const { project, setProject, isAdmin, loggedIn, memberOf } = props;
  const [showJoinModal, setShowJoinModal] = useState(false);
  const [showProjectSettingsModal, setShowProjectSettingsModal] = useState(false);
  const user = { username: 'eric' };

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
    const projectId = `${project.owner}-${project.title}`;
    console.log(projectId);
    console.log(user.username);
    console.log(motivation);
    createApplication(projectId, motivation, user.username).then((response) => {
      console.log(response);
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
          />
          <ProjectUpdatesComponent />
        </Card.Body>
      </Card>
    </>
  );
};

export default ProjectViewComponent;
