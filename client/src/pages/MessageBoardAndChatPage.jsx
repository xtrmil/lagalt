import React, { useState } from 'react';
import './MessageBoardAndChatPage.css';
import { Button } from 'react-bootstrap';
import CreatePostModal from './CreatePostModal';

const MessageBoardAndChatPage = (props) => {
  const [showCreatePostModal, setShowCreatePostModal] = useState(false);

  const onCreatePostClick = () => {
    setShowCreatePostModal(true);
  };

  const hideCreatePostModal = () => {
    setShowCreatePostModal(false);
  };

  const onMessageBoardPostClick = () => {
    props.history.push('/message_board_post');
  };

  return (
    <>
      <CreatePostModal
        showCreatePostModal={showCreatePostModal}
        hideCreatePostModal={hideCreatePostModal}
      />

      <h1 className="messageBoardAndChatHeading">
        Message Board
        <Button className="btn-newPostPt1" onClick={onCreatePostClick} variant="info">
          New Post
        </Button>
      </h1>

      <table className="table w-50">
        <thead>
          <tr>
            <th className="topic" scope="col">
              Topic
            </th>
            <th className="user" scope="col">
              User
            </th>
            <th className="replies" scope="col">
              Replies
            </th>
            <th className="latestPost" scope="col">
              Latest Post
            </th>
          </tr>
        </thead>
        <tbody>
          <tr>
            <td className="topic" onClick={onMessageBoardPostClick}>
              Topic text here
            </td>
            <td className="user">username</td>
            <td className="replies">11</td>
            <td className="latestPost" onClick={onMessageBoardPostClick}>
              username (2020-12-09)
            </td>
          </tr>
          <tr>
            <td className="topic" onClick={onMessageBoardPostClick}>
              Topic text here
            </td>
            <td className="user">username</td>
            <td className="replies">6</td>
            <td className="latestPost" onClick={onMessageBoardPostClick}>
              username (2020-11-30)
            </td>
          </tr>
          <tr>
            <td className="topic" onClick={onMessageBoardPostClick}>
              Topic text here
            </td>
            <td className="user">username</td>
            <td className="replies">3</td>
            <td className="latestPost" onClick={onMessageBoardPostClick}>
              username (2020-11-29)
            </td>
          </tr>
          <tr>
            <td className="topic" onClick={onMessageBoardPostClick}>
              Topic text here
            </td>
            <td className="user">username</td>
            <td className="replies">4</td>
            <td className="latestPost" onClick={onMessageBoardPostClick}>
              username (2020-11-23)
            </td>
          </tr>
        </tbody>
      </table>

      <h1 className="messageBoardAndChatHeading">Chat</h1>
      <div className="card w-50 chatCard">
        <div className="card-body chatCardBody"></div>
      </div>
    </>
  );
};

export default MessageBoardAndChatPage;
