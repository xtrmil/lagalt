import React from 'react';
import { Modal } from 'react-bootstrap';
import './CreatePostModal.css';
import DeletePostForm from './DeletePostForm';

const DeletePostModal = (props) => {
  const { showDeletePostModal, hideDeletePostModal } = props;
  return (
    <Modal show={showDeletePostModal} onHide={hideDeletePostModal}>
      <Modal.Body className="mx-3">
        <h2 className="text-center mb-5">Are you sure you want to delete?</h2>
        <DeletePostForm></DeletePostForm>
      </Modal.Body>
    </Modal>
  );
};

export default DeletePostModal;
