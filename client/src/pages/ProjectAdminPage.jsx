import React from 'react';
import './ProjectAdminPage.css';
import { Button } from 'react-bootstrap';
import AdminForm from '../components/AdminForm';

const ProjectAdminPage = (props) => {
  const onProjectViewClick = () => {
    props.history.push('/project');
  };

  const onCreateProjectPageClick = () => {
    props.history.push('/project/create');
  };

  return (
    <>
      <Button className="btn-createNewProject" onClick={onCreateProjectPageClick} variant="info">
        Create new project
      </Button>
      <h1 className="projectAdminHeading">Incoming applications</h1>
      <table className="table w-50">
        <thead>
          <tr>
            <th className="user" scope="col">
              User
            </th>
            <th className="project" scope="col">
              Project
            </th>
            <th className="resolve" scope="col">
              Resolve
            </th>
          </tr>
        </thead>
        <tbody>
          <tr>
            <td className="username">username</td>
            <td className="projectNameHere">Project name here</td>
            <td>
              <Button className="btn-approve" onClick={onProjectViewClick} variant="success">
                Approve
              </Button>
              <Button className="btn-decline" onClick={onProjectViewClick} variant="danger">
                Decline
              </Button>
              <Button className="btn-details" onClick={onProjectViewClick} variant="secondary">
                Details
              </Button>
            </td>
          </tr>
          <tr>
            <td className="username">username</td>
            <td className="projectNameHere">Project name here</td>
            <td>
              <Button className="btn-approve" onClick={onProjectViewClick} variant="success">
                Approve
              </Button>
              <Button className="btn-decline" onClick={onProjectViewClick} variant="danger">
                Decline
              </Button>
              <Button className="btn-details" onClick={onProjectViewClick} variant="secondary">
                Details
              </Button>
            </td>
          </tr>
          <tr>
            <td className="username">username</td>
            <td className="projectNameHere">Project name here</td>
            <td>
              <Button className="btn-approve" onClick={onProjectViewClick} variant="success">
                Approve
              </Button>
              <Button className="btn-decline" onClick={onProjectViewClick} variant="danger">
                Decline
              </Button>
              <Button className="btn-details" onClick={onProjectViewClick} variant="secondary">
                Details
              </Button>
            </td>
          </tr>
        </tbody>
      </table>
      <h1 className="projectOverviewHeading">Project overview update</h1>
      <div className="card w-50 projectAdminPageCard">
        <div className="card-body projectAdminPageCardBody">
          <div className="row project">
            <div className="ml-2"></div>
            <div className="ml-2">
              <h4>Project</h4>
              <AdminForm />
            </div>
          </div>
        </div>
      </div>
      <div className="card w-50 projectAdminPageCard">
        <div className="card-body projectAdminPageCardBody">
          <div className="row project">
            <div className="ml-2"></div>
            <div className="ml-2">
              <h4>Project</h4>
              <AdminForm />
            </div>
          </div>
        </div>
      </div>
      <div className="card w-50 projectAdminPageCard">
        <div className="card-body projectAdminPageCardBody">
          <div className="row project">
            <div className="ml-2"></div>
            <div className="ml-2">
              <h4>Project</h4>
              <AdminForm />
            </div>
          </div>
        </div>
      </div>
    </>
  );
};

export default ProjectAdminPage;
