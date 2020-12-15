import React from 'react';
import { Container } from 'react-bootstrap';
import ProfileComponent from '../components/profile/ProfileComponent';

const ProfilePage = (props) => {
  const loggedInUserId = '1';
  const user = {
    uid: '1',
    name: 'someName',
    memberOf: [1, 2, 3, 4],
    email: 'email@email.com',
    hidden: false,
    skills: ['WEB_DEV', 'SECURITY', 'REACT', 'ANGULAR'],
    description: 'Here is a description of me, i have experience with everything!',
  };

  return (
    <Container className="justify-content-center">
      <ProfileComponent user={user} loggedInUserId={loggedInUserId} />
    </Container>
  );
};
export default ProfilePage;
