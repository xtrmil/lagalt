import React, { useState } from 'react';
import ProfileDescription from './ProfileDescription';
import './ProfileComponent.css';
import { Button } from 'react-bootstrap';
import ProfileModal from './ProfileModal';
const ProfileComponent = (props) => {
  const { loggedInUserId } = props;
  const [user, setUser] = useState(props.user);
  const profileOwner = loggedInUserId === user.uid ? true : false;

  const skillsList = user.skills.map((skill, index) => {
    return (
      <div className={index % 2 == 0 ? 'mr-1 mt-1 skill odd' : 'mr-1 mt-1 skill'} key={index}>
        {skill}
      </div>
    );
  });
  const [showModal, setShowModal] = useState(false);

  const onEditClick = () => {
    setShowModal(!showModal);
  };

  const handleCloseModal = () => {
    setShowModal(false);
  };

  const handleSaveChanges = (newUser) => {
    setUser(newUser);
    handleCloseModal();
  };

  return (
    <>
      <ProfileModal
        showModal={showModal}
        handleCloseModal={handleCloseModal}
        handleSaveChanges={handleSaveChanges}
        user={user}
      ></ProfileModal>

      <div className="card">
        <div className="card-body">
          <div className="row mt-3 ml-2">
            <div className="col-sm-4">
              <div className="imgplaceholder mb-2">IMAGE</div>
              <h3 className="mb-1 text-center">Skills</h3>
              <div className="mb-2 text-center">{skillsList}</div>

              <h3 className="mb-1 text-center">Portfolio</h3>
              <div className="text-center">
                <div>there should be a list of projects here</div>
                <div>there should be a list of projects here</div>
                <div>there should be a list of projects here</div>
                <div>there should be a list of projects here</div>
                <div>there should be a list of projects here</div>
              </div>
            </div>
            <div className="col-sm-8 pl-0">
              <div className="row no-gutters">
                <div className="col-sm-8">
                  <h2>{user.name}</h2>
                  <div className="mb-2">{user.email}</div>
                </div>
                <div className="col-sm-4">
                  <div className="edit-button-wrapper mr-4">
                    {profileOwner ? (
                      <div>
                        <div className="d-inline-block mr-5">
                          {user.hidden ? 'Hidden' : 'Public'}
                        </div>
                        <Button onClick={onEditClick}>Edit</Button>
                      </div>
                    ) : (
                      ''
                    )}
                  </div>
                </div>
              </div>
              <ProfileDescription user={user} />
            </div>
          </div>
        </div>
      </div>
    </>
  );
};
export default ProfileComponent;
