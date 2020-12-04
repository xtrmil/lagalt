import React, { useState } from 'react';
import { Modal, Button } from 'react-bootstrap';
import Select from 'react-select';

const ProfileModal = (props) => {
  const { showModal, handleCloseModal, handleSaveChanges, user } = props;
  const [newUser, setNewUser] = useState(user);

  const currentSkills = user.skills.map((skill) => ({
    value: skill,
    label: skill,
  }));
  const currentStatus = { value: user.hidden, label: user.hidden ? 'Hidden' : 'Public' };
  const options = [
    { value: 'DRUMMER', label: 'Drummer' },
    { value: 'WEB_DEV', label: 'WEB_DEV' },
    { value: 'REACT', label: 'REACT' },
    { value: 'SECURITY', label: 'SECURITY' },
    { value: 'ANGULAR', label: 'ANGULAR' },
  ];

  const profileStatusOptions = [
    { value: true, label: 'Hidden' },
    { value: false, label: 'Public' },
  ];

  const onSaveChangesClick = () => {
    handleSaveChanges(newUser);
  };
  const onFieldChange = (event) => {
    setNewUser({ ...newUser, [event.target.name]: event.target.value });
  };
  const onHideModal = () => {
    handleCloseModal();
    setNewUser(user);
  };

  const onChangeSkills = (event) => {
    let selected = [];
    if (event) {
      event.forEach((item) => {
        selected.push(item['value']);
      });
    }
    setNewUser({ ...newUser, skills: selected });
  };
  const onStatusChange = (event) => {
    setNewUser({ ...newUser, hidden: event['value'] });
  };

  return (
    <Modal show={showModal} onHide={onHideModal}>
      <Modal.Header className="border-0" closeButton>
        <Modal.Body>
          <div>
            <h3 className="mb-2 text-center">Edit your profile</h3>
            <div className="mb-1">Name</div>
            <input
              className="input-box mb-2"
              name="name"
              value={newUser.name}
              onChange={onFieldChange}
              type="text"
            />
            <div className="mb-1">Email</div>
            <input
              className="input-box mb-2"
              name="email"
              value={newUser.email}
              onChange={onFieldChange}
              type="text"
            />
            <div className="mb-2">
              <div className="mb-1">Skills</div>
              <Select
                className="basic-multi-select mb-1"
                isMulti
                name="skills"
                defaultValue={currentSkills}
                options={options}
                closeMenuOnSelect={false}
                onChange={onChangeSkills}
              ></Select>
            </div>
            <div className="mb-1">Description</div>
            <textarea
              className="description-text-area mb-2"
              name="description"
              value={newUser.description}
              onChange={onFieldChange}
              type="text"
            />
            <div className="mb-3">
              <div className="mb-1">Profile Status</div>
              <Select
                name="profileStatus"
                defaultValue={currentStatus}
                options={profileStatusOptions}
                onChange={onStatusChange}
              ></Select>
            </div>
            <div>
              <Button onClick={onSaveChangesClick}>Save Changes</Button>
            </div>
          </div>
        </Modal.Body>
      </Modal.Header>
    </Modal>
  );
};
export default ProfileModal;
