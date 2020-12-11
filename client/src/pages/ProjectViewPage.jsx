import React, { useState } from 'react';
import { Container } from 'react-bootstrap';
import JoinProjectModal from '../components/projectView/JoinProjectModal';
import ProjectViewComponent from '../components/projectView/ProjectViewComponent';

const ProjectViewPage = (props) => {
  const [showJoinModal, setShowJoinModal] = useState(false);
  const isAdmin = true;
  const loggedIn = true;
  const memberOf = true;

  const onJoinClick = () => {
    setShowJoinModal(true);
  };
  const hideJoinModal = () => {
    setShowJoinModal(false);
  };
  return (
    <Container className="justify-content-center">
      <JoinProjectModal
        showJoinModal={showJoinModal}
        hideJoinModal={hideJoinModal}
      ></JoinProjectModal>

      <ProjectViewComponent
        isAdmin={isAdmin}
        loggedIn={loggedIn}
        memberOf={memberOf}
        onJoinClick={onJoinClick}
      ></ProjectViewComponent>
    </Container>
  );
};
export default ProjectViewPage;
