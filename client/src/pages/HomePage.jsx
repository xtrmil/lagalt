import React, { useState } from 'react';
import './HomePage.css';
import { Button, Tabs, Tab, Form, FormControl } from 'react-bootstrap';
import { Link } from 'react-router-dom';
import JoinProjectModal from '../components/projectView/JoinProjectModal';

const HomePage = (props) => {
  const [showJoinModal, setShowJoinModal] = useState(false);

  const onJoinClick = () => {
    setShowJoinModal(true);
  };

  const hideJoinModal = () => {
    setShowJoinModal(false);
  };

  const onProjectAdminPageClick = () => {
    props.history.push('/admin');
  };

  const onMessageBoardAndChatPageClick = () => {
    props.history.push('/message_board_and_chat');
  };

  function refreshPage() {
    window.location.reload(false);
  }

  return (
    <>
      <JoinProjectModal showJoinModal={showJoinModal} hideJoinModal={hideJoinModal} />

      <Form inline>
        <h5 className="filter-text">FILTER BY</h5>
        <div className="filter-function">
          <FormControl
            type="text"
            placeholder="Search for projects..."
            className="search mr-sm-2"
          />
          <Button variant="outline-success">Search</Button>
        </div>
      </Form>

      <Tabs defaultActiveKey="filterTabs" className="filter-tabs">
        <Tab eventKey="industry" title="Industry">
          <p>
            Music........................Film........................Game
            Development........................Web Development........................
          </p>
        </Tab>
        <Tab eventKey="skills" title="Skills">
          <p>Different skills........................</p>
        </Tab>
        <Tab eventKey="time" title="Time">
          <p>Newest projects........................Oldest projects........................</p>
        </Tab>
      </Tabs>

      <Button onClick={refreshPage} className="btn-viewAll" variant="secondary">
        View all
      </Button>

      <h1 className="homePageHeading">Projects</h1>
      <div className="card w-75 homePageCard">
        <div className="card-body homePageCardBody">
          <div className="row project">
            <div className="ml-2">
              <img className="homepage-img" src="nedladdning.jpg"></img>
            </div>
            <div className="ml-2">
              <div className="row">
                <div className="col">
                  <h4>Project</h4>
                </div>
                <div className="col">
                  <h6 className="matchedNotMatched">Matched/not matched</h6>
                </div>
              </div>
              <h6>
                Project
                description..........................................................................................................................
                <Link to="/project">(Read more)</Link>
              </h6>
              <h6>Industry: ............... Skills needed: ..............................</h6>
              <div className="stretched-button-join">
                <Button onClick={onJoinClick} variant="success">
                  Join
                </Button>
              </div>
            </div>
          </div>
          <a href="/project" className="stretched-link"></a>
        </div>
      </div>

      <div className="card w-75 homePageCard">
        <div className="card-body homePageCardBody">
          <div className="row project">
            <div className="ml-2">
              <img className="homepage-img" src="nedladdning.jpg"></img>
            </div>
            <div className="ml-2">
              <div className="row">
                <div className="col">
                  <h4>Project</h4>
                </div>
                <div className="col">
                  <h6 className="matchedNotMatched">Matched/not matched</h6>
                </div>
              </div>
              <h6>
                Project
                description..........................................................................................................................
                <Link to="/project">(Read more)</Link>
              </h6>
              <h6>Industry: ............... Skills needed: ..............................</h6>
              <div className="stretched-button-join">
                <Button onClick={onJoinClick} variant="success">
                  Join
                </Button>
              </div>
            </div>
          </div>
          <a href="/project" className="stretched-link"></a>
        </div>
      </div>

      <div className="card w-75 homePageCard">
        <div className="card-body homePageCardBody">
          <div className="row project">
            <div className="ml-2">
              <img className="homepage-img" src="nedladdning.jpg"></img>
            </div>
            <div className="ml-2">
              <h4>Project</h4>
              <h6>
                Project
                description..........................................................................................................................
                <Link to="/project">(Read more)</Link>
              </h6>
              <h6>Industry: ............... Skills needed: ..............................</h6>
              <div className="stretched-button-admin">
                <Button onClick={onMessageBoardAndChatPageClick} variant="info">
                  Message Board & Chat
                </Button>
                <Button className="ml-4" onClick={onProjectAdminPageClick} variant="info">
                  Admin
                </Button>
              </div>
            </div>
          </div>
          <a href="/project" className="stretched-link"></a>
        </div>
      </div>

      <div className="card w-75 homePageCard">
        <div className="card-body homePageCardBody">
          <div className="row project">
            <div className="ml-2">
              <img className="homepage-img" src="nedladdning.jpg"></img>
            </div>
            <div className="ml-2">
              <h4>Project</h4>
              <h6>
                Project
                description..........................................................................................................................
                <Link to="/project">(Read more)</Link>
              </h6>
              <h6>Industry: ............... Skills needed: ..............................</h6>
              <div className="stretched-button-mb">
                <Button onClick={onMessageBoardAndChatPageClick} variant="info">
                  Message Board & Chat
                </Button>
              </div>
            </div>
          </div>
          <a href="/project" className="stretched-link"></a>
        </div>
      </div>
    </>
  );
};

export default HomePage;
