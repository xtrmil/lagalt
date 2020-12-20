import React from 'react';
import './ProjectsPage.css';
import { Button } from 'react-bootstrap';
import { Link } from 'react-router-dom';

const ProjectsPage = (props) => {
  const onProjectViewClick = () => {
    props.history.push('/project');
  };
  const onProjectAdminPageClick = () => {
    props.history.push('/admin');
  };

  return (
    <>
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
              <div className="stretched-button-admin">
                <Button onClick={onProjectViewClick} variant="info">
                  Message board & chat
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
              <div className="stretched-button-admin">
                <Button onClick={onProjectViewClick} variant="info">
                  Message board & chat
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
              <div className="stretched-button-mb">
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
              <div className="stretched-button-mb">
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
