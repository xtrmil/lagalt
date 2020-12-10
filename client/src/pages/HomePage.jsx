import React from 'react';
import './HomePage.css';
import { Button } from 'react-bootstrap';
import { Link } from 'react-router-dom';

const HomePage = (props) => {
  const onProjectViewClick = () => {
    props.history.push('/project');
  };
  return (
    <>
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
              <Button onClick={onProjectViewClick} variant="success">
                Join
              </Button>
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
              <Button onClick={onProjectViewClick} variant="success">
                Join
              </Button>
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
              <Button onClick={onProjectViewClick} variant="info">
                Message board & chat
              </Button>
            </div>
          </div>
          <a href="/project" className="stretched-link"></a>
        </div>
      </div>
    </>
  );
};

export default HomePage;
