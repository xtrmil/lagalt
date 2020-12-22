import React from 'react';
import { Modal } from 'react-bootstrap';
import ReplyPostForm from './ReplyPostForm';

const ReplyPostModal = (props) => {
  const { showReplyPostModal, hideReplyPostModal } = props;
  return (
    <Modal show={showReplyPostModal} onHide={hideReplyPostModal}>
      <Modal.Body className="mx-3">
        <h2 className="text-center mb-5">Reply to post</h2>
        <ReplyPostForm></ReplyPostForm>
      </Modal.Body>
    </Modal>
  );
};

export default ReplyPostModal;
