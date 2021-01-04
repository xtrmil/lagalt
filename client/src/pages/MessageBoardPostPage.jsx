import React, { useState } from 'react';
import './MessageBoardPostPage.css';
import { Button, Container, Card } from 'react-bootstrap';
import CreatePostModal from './CreatePostModal';
import ReplyPostModal from '../pages/ReplyPostModal';
import EditPostModal from '../pages/EditPostModal';
import DeletePostModal from '../pages/DeletePostModal';

const MessageBoardPostPage = (props) => {
  const [showCreatePostModal, setShowCreatePostModal] = useState(false);

  const onCreatePostClick = () => {
    setShowCreatePostModal(true);
  };

  const hideCreatePostModal = () => {
    setShowCreatePostModal(false);
  };

  const [showReplyPostModal, setShowReplyPostModal] = useState(false);

  const onReplyPostClick = () => {
    setShowReplyPostModal(true);
  };

  const hideReplyPostModal = () => {
    setShowReplyPostModal(false);
  };

  const [showEditPostModal, setShowEditPostModal] = useState(false);

  const onEditPostClick = () => {
    setShowEditPostModal(true);
  };

  const hideEditPostModal = () => {
    setShowEditPostModal(false);
  };

  const [showDeletePostModal, setShowDeletePostModal] = useState(false);

  const onDeletePostClick = () => {
    setShowDeletePostModal(true);
  };

  const hideDeletePostModal = () => {
    setShowDeletePostModal(false);
  };

  const onMessageBoardAndChatPageClick = () => {
    props.history.push('/message_board_and_chat');
  };

  return (
    <>
      <CreatePostModal
        showCreatePostModal={showCreatePostModal}
        hideCreatePostModal={hideCreatePostModal}
      />

      <ReplyPostModal
        showReplyPostModal={showReplyPostModal}
        hideReplyPostModal={hideReplyPostModal}
      />

      <EditPostModal showEditPostModal={showEditPostModal} hideEditPostModal={hideEditPostModal} />

      <DeletePostModal
        showDeletePostModal={showDeletePostModal}
        hideDeletePostModal={hideDeletePostModal}
      />

      <Container className="postCard">
        <Card>
          <Card.Body>
            <div className="row">
              <div className="col">
                <Button
                  className="btn-back"
                  onClick={onMessageBoardAndChatPageClick}
                  variant="info"
                >
                  Back
                </Button>
                <div className="topicText">
                  <h5>Topic text here</h5>
                </div>
              </div>
              <div className="col">
                <Button className="btn-reply" onClick={onReplyPostClick} variant="info">
                  Reply
                </Button>
                <Button className="btn-newPostPt2" onClick={onCreatePostClick} variant="info">
                  New post
                </Button>
              </div>
            </div>

            <div className="row">
              <div className="col">
                <div className="usernamePost">
                  <h6>username</h6>
                </div>
                <div className="postReply">
                  <div>
                    Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor
                    incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis
                    nostrud exercitation ullamco...
                  </div>
                </div>
              </div>
            </div>

            <div className="row">
              <div className="col">
                <div className="usernamePost">
                  <h6>username</h6>
                </div>
                <div className="postReply">
                  Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor
                  incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud
                  exercitation ullamco...
                  <button
                    type="button"
                    className="btn-edit btn btn-info btn-sm"
                    onClick={onEditPostClick}
                  >
                    Edit
                  </button>
                  <button
                    type="button"
                    className="btn-delete btn btn-danger btn-sm"
                    onClick={onDeletePostClick}
                  >
                    Delete
                  </button>
                </div>
              </div>
            </div>

            <div className="row">
              <div className="col">
                <div className="usernamePost">
                  <h6>username</h6>
                </div>
                <div className="postReply">
                  Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor
                  incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud
                  exercitation ullamco...
                </div>
              </div>
            </div>

            <div className="row">
              <div className="col">
                <div className="usernamePost">
                  <h6>username</h6>
                </div>
                <div className="postReply">
                  Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor
                  incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud
                  exercitation ullamco...
                </div>
              </div>
            </div>
          </Card.Body>
        </Card>
      </Container>
    </>
  );
};

export default MessageBoardPostPage;
