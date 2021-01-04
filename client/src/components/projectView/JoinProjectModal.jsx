import React from 'react';
import { Modal } from 'react-bootstrap';
import JoinProjectForm from './JoinProjectForm';

const JoinProjectModal = (props) => {
  const { showJoinModal, hideJoinModal, handleJoinProject } = props;

  return (
    <Modal show={showJoinModal} onHide={hideJoinModal}>
      <Modal.Body className="mx-3">
        <h2 className="text-center mb-5">Apply to this project</h2>
        <JoinProjectForm handleJoinProject={handleJoinProject}></JoinProjectForm>
      </Modal.Body>
    </Modal>
  );
};

export default JoinProjectModal;
