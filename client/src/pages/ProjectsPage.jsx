import React, { useState } from 'react';
import './ProjectsPage.css';
import { Button } from 'react-bootstrap';
import { Link } from 'react-router-dom';
import ProjectSettingsModal from '../components/project/ProjectSettingsModal';

const ProjectsPage = (props) => {
  const [showModal, setShowModal] = useState(false);

  const onEditClick = () => {
    setShowModal(!showModal);
  };
  const onProjectViewClick = () => {
    props.history.push('/project');
  };
  return (
    <>
      <ProjectSettingsModal
        showModal={showModal}
        handleCloseModal={onEditClick}
      ></ProjectSettingsModal>
      <Button className="btn-createNewProject" onClick={onProjectViewClick} variant="info">
        Create new project
      </Button>
      <h1>Your projects</h1>
      <div className="card w-75 projectsPageCard">
        <div className="card-body projectsPageCardBody">
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
              <div className="stretched-button">
                <Button onClick={onProjectViewClick} variant="info">
                  Message board & chat
                </Button>
                <Button className="ml-4" onClick={onProjectViewClick} variant="info">
                  Admin
                </Button>
              </div>
            </div>
          </div>
          <a href="/project" className="stretched-link"></a>
        </div>
      </div>
      <div className="card w-75 projectsPageCard">
        <div className="card-body projectsPageCardBody">
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
              <div className="stretched-button">
                <Button onClick={onProjectViewClick} variant="info">
                  Message board & chat
                </Button>
                <Button className="ml-4" onClick={onProjectViewClick} variant="info">
                  Admin
                </Button>
              </div>
            </div>
          </div>
          <a href="/project" className="stretched-link"></a>
        </div>
      </div>
      <h1>Joined projects</h1>
      <div className="card w-75 projectsPageCard">
        <div className="card-body projectsPageCardBody">
          <div className="row project">
            <div className="ml-2">
              <img className="homepage-img" src="nedladdning.jpg"></img>
            </div>
            <div className="ml-2">
              <a href="/project" className="stretched-link"></a>
              <h4>Project</h4>
              <h6>
                Project
                description..........................................................................................................................
                <Link to="/project">(Read more)</Link>
              </h6>
              <h6>Industry: ............... Skills needed: ..............................</h6>
              <div className="stretched-button">
                <Button onClick={onProjectViewClick} variant="info">
                  Message board & chat
                </Button>
              </div>
            </div>
          </div>
        </div>
      </div>
      <div className="card w-75 projectsPageCard">
        <div className="card-body projectsPageCardBody">
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
              <div className="stretched-button">
                <Button onClick={onProjectViewClick} variant="info">
                  Message board & chat
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

export default ProjectsPage;
