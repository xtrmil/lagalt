import React, { useEffect, useState } from 'react';
import { Container } from 'react-bootstrap';
import ProfileComponent from '../components/profile/ProfileComponent';
import { getUserByUserId } from '../utils/api/user';

const ProfilePage = (props) => {
  const [loggedInUser] = useState(props.loggedInUser);
  const [user, setUser] = useState();
  const [isLoading, setIsLoading] = useState(true);

  const getUserProfile = async () => {
    await getUserByUserId().then((response) => {
      setUser(response.data);
    });
    setIsLoading(false);
  };
  useEffect(() => {
    getUserProfile(loggedInUser);
  }, [loggedInUser]);

  return (
    <Container className="justify-content-center">
      {!isLoading && <ProfileComponent user={user} loggedInUserId={loggedInUser} />}
    </Container>
  );
};
export default ProfilePage;
