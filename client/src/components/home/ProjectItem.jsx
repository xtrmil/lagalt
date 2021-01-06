import React, { useState, useEffect } from 'react';
import { Button, Card, Row, Col } from 'react-bootstrap';
import './ProjectItem.css';
import JoinProjectModal from '../projectView/JoinProjectModal';
import { createApplication } from '../../utils/api/application';
const ProjectItem = (props) => {
  const { loggedInUser } = props;
  const [hasApplied, setHasApplied] = useState(false);
  const [memberOf, setMemberOf] = useState(false);
  const [isAdmin, setIsAdmin] = useState(false);
  const [loggedIn, setLoggedIn] = useState(false);
  const [userTags, setUserTags] = useState([]);
  const [showJoinModal, setShowJoinModal] = useState(false);
  const { project } = props;

  let tagsList;
  if (project.tags) {
    const tagsArray = Object.values(project.tags);
    tagsList = tagsArray.map((tag, index) => {
      const matched = userTags.includes(tag);
      return (
        <span className={matched ? ' mr-2 tag-border matched' : ' mr-2 tag-border'} key={index}>
          {tag}
        </span>
      );
    });
  }

  useEffect(() => {
    loggedInUser && setLoggedIn(true);
    if (loggedInUser && loggedInUser.username && project && project.owner) {
      if (loggedInUser.username.toUpperCase() === project.owner.toUpperCase()) {
        setMemberOf(true);
        setIsAdmin(true);
      } else if (
        project.members != null &&
        project.members.includes(loggedInUser.username.toLowerCase())
      ) {
        setMemberOf(true);
      }
      setUserTags(Object.values(loggedInUser.tags));

      if (loggedInUser.appliedTo) {
        loggedInUser.appliedTo.map((application) => {
          if (application.project === project.title) {
            setHasApplied(true);
          }
        });
      }
    }
  }, [loggedInUser, project]);

  const onJoinClick = () => {
    setShowJoinModal(true);
  };

  const hideJoinModal = () => {
    setShowJoinModal(false);
  };
  const handleJoinProject = (motivation) => {
    const { owner, title } = project;
    createApplication(owner, title, motivation, loggedInUser.username).then((response) => {
      hideJoinModal();
      setHasApplied(true);
    });
  };

  const onCardClick = () => {
    props.history.push(`/project/${project.owner}/${project.title}`);
  };
  return (
    <>
      <JoinProjectModal
        showJoinModal={showJoinModal}
        hideJoinModal={hideJoinModal}
        handleJoinProject={handleJoinProject}
      />
      <Card className="mb-3 project-card" onClick={() => onCardClick()}>
        <Card.Body>
          <Row className="no-gutters">
            <Col sm={3} className="text-center">
              <img src="nedladdning.jpg"></img>
            </Col>
            <Col className="ml-4" sm={8}>
              <Row className="no-gutters">
                <Col sm={8}>
                  <h4>{project.title}</h4>
                  <h6>{Object.values(project.industry)[0]}</h6>
                </Col>
                <Col sm={4}>{isAdmin && <h6 className="matchedNotMatched">Your Project</h6>}</Col>
              </Row>
              <h6>{project.description}</h6>

              <div className="mb-4">Looking for: {tagsList}</div>
              <div className="stretched-button-join">
                {loggedIn && !memberOf && (
                  <Button
                    disabled={hasApplied}
                    onClick={(e) => {
                      e.stopPropagation();
                      onJoinClick();
                    }}
                    variant="success"
                  >
                    {hasApplied ? 'Applied' : 'Join'}
                  </Button>
                )}
                {loggedIn && memberOf && (
                  <Button
                    variant="info"
                    onClick={(e) => {
                      e.stopPropagation();
                      props.history.push(`/projects/${project.owner}/${project.title}/chat`);
                    }}
                  >
                    Chat
                  </Button>
                )}
              </div>
            </Col>
          </Row>
        </Card.Body>
      </Card>
    </>
  );
};

export default ProjectItem;
