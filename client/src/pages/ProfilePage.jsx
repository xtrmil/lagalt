import React, { useEffect, useState } from 'react';
import { Container } from 'react-bootstrap';
import ProfileComponent from '../components/profile/ProfileComponent';
import { getUserByUserId } from '../utils/api/user';
import * as Auth from '../utils/Auth';

const ProfilePage = (props) => {
  const [loggedInUser, setLoggedInUser] = useState();
  const [mockLoggedInUser, setMockLoggedInUser] = useState('');
  const [tempUser, setTempUser] = useState();
  const [isLoading, setIsLoading] = useState(true);

  const getUserProfile = async () => {
    await getUserByUserId('').then((response) => {
      setTempUser(response.data);
    });
    setIsLoading(false);
  };
  useEffect(() => {
    Auth.loggedInUser().subscribe((user) => {
      setMockLoggedInUser(user.username);
      setLoggedInUser('Bumpfel');
      getUserProfile(user.username);
    });
  }, []);

  return (
    <Container className="justify-content-center">
      {!isLoading && (
        <ProfileComponent
          user={tempUser}
          loggedInUserId={loggedInUser}
          mockLoggedInUser={mockLoggedInUser}
        />
      )}
    </Container>
  );
};
export default ProfilePage;
