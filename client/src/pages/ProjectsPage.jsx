import React from 'react';
import './ProjectsPage.css';
import { Button } from 'react-bootstrap';
import { Link } from 'react-router-dom';

const ProjectsPage = (props) => {
  const onProjectViewClick = () => {
    props.history.push('/project');
  };
  return (
    <>
      <Button className="btn-createNewProject" onClick={onProjectViewClick} variant="info">
        Create new project
      </Button>{' '}
      <h1>Your projects</h1>
      <div className="card w-75">
        <div className="card-body">
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
              <Button onClick={onProjectViewClick} variant="info">
                Message board & chat
              </Button>
              <Button className="btn-admin" onClick={onProjectViewClick} variant="info">
                Admin
              </Button>{' '}
            </div>
          </div>
          <a href="/project" className="stretched-link"></a>
        </div>
      </div>
      <div className="card w-75">
        <div className="card-body">
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
              <Button onClick={onProjectViewClick} variant="info">
                Message board & chat
              </Button>
              <Button className="btn-admin" onClick={onProjectViewClick} variant="info">
                Admin
              </Button>{' '}
            </div>
          </div>
          <a href="/project" className="stretched-link"></a>
        </div>
      </div>
      <div className="card w-75">
        <div className="card-body">
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
              <Button onClick={onProjectViewClick} variant="info">
                Message board & chat
              </Button>
              <Button className="btn-admin" onClick={onProjectViewClick} variant="info">
                Admin
              </Button>{' '}
            </div>
          </div>
          <a href="/project" className="stretched-link"></a>
        </div>
      </div>
      <h1>Joined projects</h1>
      <div className="card w-75">
        <div className="card-body">
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
              <Button onClick={onProjectViewClick} variant="info">
                Message board & chat
              </Button>{' '}
            </div>
          </div>
          <a href="/project" className="stretched-link"></a>
        </div>
      </div>
      <div className="card w-75">
        <div className="card-body">
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
              <Button onClick={onProjectViewClick} variant="info">
                Message board & chat
              </Button>{' '}
            </div>
          </div>
          <a href="/project" className="stretched-link"></a>
        </div>
      </div>
      <div className="card w-75">
        <div className="card-body">
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
              <Button onClick={onProjectViewClick} variant="info">
                Message board & chat
              </Button>{' '}
            </div>
          </div>
          <a href="/project" className="stretched-link"></a>
        </div>
      </div>
    </>
  );
};

export default ProjectsPage;
