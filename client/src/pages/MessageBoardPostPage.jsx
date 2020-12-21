import React from 'react';
import './MessageBoardPostPage.css';
import { Button, Container, Card } from 'react-bootstrap';

const MessageBoardPostPage = (props) => {
  const onMessageBoardAndChatPageClick = () => {
    props.history.push('/message_board_and_chat');
  };

  return (
    <>
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
                <Button
                  className="btn-reply"
                  onClick={onMessageBoardAndChatPageClick}
                  variant="info"
                >
                  Reply
                </Button>
                <Button
                  className="btn-newPostPt2"
                  onClick={onMessageBoardAndChatPageClick}
                  variant="info"
                >
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
                    className="btn-edit btn btn-primary btn-sm"
                    onClick={onMessageBoardAndChatPageClick}
                  >
                    Edit
                  </button>
                  <button
                    type="button"
                    className="btn-delete btn btn-primary btn-sm"
                    onClick={onMessageBoardAndChatPageClick}
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
                  <div>
                    Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor
                    incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis
                    nostrud exercitation ullamco...
                  </div>
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
