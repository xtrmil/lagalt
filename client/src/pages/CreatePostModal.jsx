import React from 'react';
import { Modal } from 'react-bootstrap';
import './CreatePostModal.css';
import CreatePostForm from './CreatePostForm';

const CreatePostModal = (props) => {
  const { showCreatePostModal, hideCreatePostModal } = props;
  return (
    <Modal show={showCreatePostModal} onHide={hideCreatePostModal}>
      <Modal.Body className="mx-3">
        <h2 className="text-center mb-5">Create a new post</h2>
        <CreatePostForm></CreatePostForm>
      </Modal.Body>
    </Modal>
  );
};

export default CreatePostModal;
