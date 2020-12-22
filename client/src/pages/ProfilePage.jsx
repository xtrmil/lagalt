import React, { useEffect, useState } from 'react';
import { Container } from 'react-bootstrap';
import ProfileComponent from '../components/profile/ProfileComponent';
import { getUserByUserId } from '../utils/api/user';
const ProfilePage = (props) => {
  const loggedInUserId = 'Bumpfel';
  const [tempUser, setTempUser] = useState();
  const [isLoading, setIsLoading] = useState(true);

  const getUserProfile = async () => {
    await getUserByUserId('').then((response) => {
      setTempUser(response.data);
    });
    setIsLoading(false);
  };
  useEffect(() => {
    getUserProfile();
  }, []);

  return (
    <Container className="justify-content-center">
      {!isLoading && <ProfileComponent user={tempUser} loggedInUserId={loggedInUserId} />}
    </Container>
  );
};
export default ProfilePage;
