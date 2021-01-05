import React, { useEffect, useState } from 'react';
import { Container } from 'react-bootstrap';
import ProfileComponent from '../components/profile/ProfileComponent';
import { getUserByUserId } from '../utils/api/user';
import * as Auth from '../utils/Auth';

const ProfilePage = (props) => {
  console.log(props.loggedInUser);
  const [loggedInUser] = useState(props.loggedInUser);
  const [user, setUser] = useState();
  const [isLoading, setIsLoading] = useState(true);

  const getUserProfile = async () => {
    await getUserByUserId().then((response) => {
      console.log(response.data);
      setUser(response.data);
    });
    setIsLoading(false);
  };
  useEffect(() => {
    getUserProfile(loggedInUser);
  }, []);

  return (
    <Container className="justify-content-center">
      {!isLoading && <ProfileComponent user={user} loggedInUserId={loggedInUser} />}
    </Container>
  );
};
export default ProfilePage;
