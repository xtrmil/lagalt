import React from 'react';
import { Modal } from 'react-bootstrap';
import EditPostForm from './EditPostForm';

const EditPostModal = (props) => {
  const { showEditPostModal, hideEditPostModal } = props;
  return (
    <Modal show={showEditPostModal} onHide={hideEditPostModal}>
      <Modal.Body className="mx-3">
        <h2 className="text-center mb-5">Edit post</h2>
        <EditPostForm></EditPostForm>
      </Modal.Body>
    </Modal>
  );
};

export default EditPostModal;
