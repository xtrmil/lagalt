import React from 'react';
import { Modal } from 'react-bootstrap';
import ProjectSettingsForm from './ProjectSettingsForm';

const ProjectSettingsModal = (props) => {
  const { showModal, hideModal, project, setProject } = props;
  const onHideModal = () => {
    hideModal();
  };

  return (
    <Modal show={showModal} onHide={onHideModal}>
      <Modal.Body className="mx-3">
        <h2 className="text-center mb-5">Edit project</h2>
        <ProjectSettingsForm project={project} setProject={setProject} hideModal={onHideModal} />
      </Modal.Body>
    </Modal>
  );
};
export default ProjectSettingsModal;
