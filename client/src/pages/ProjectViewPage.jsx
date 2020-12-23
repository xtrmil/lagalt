import React, { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import { Container } from 'react-bootstrap';
import ProjectViewComponent from '../components/projectView/ProjectViewComponent';
import { getProject } from '../utils/api/project';
import * as Auth from '../utils/Auth';
import { getUserByUserId } from '../utils/api/user';
const ProjectViewPage = (props) => {
  const [project, setProject] = useState({});
  const [isLoading, setIsLoading] = useState(true);
  const [loggedInUser, setLoggedInUser] = useState();
  const [isAdmin, setIsAdmin] = useState(false);
  const [loggedIn, setLoggedIn] = useState(false);
  const [memberOf, setMemberOf] = useState(false);
  const { userId, projectId } = useParams();

  useEffect(() => {
    const fetchUser = async (userId) => {
      getUserByUserId(userId).then((response) => {
        setLoggedInUser(response.data);
      });
    };
    Auth.loggedInUser().subscribe((user) => {
      //Eftersom att backend alltid kommer returna Bumpfel så kan vi skicka in det här just nu
      fetchUser(user.username);

      //När vi har fixat att man kan logga in så ska det här användas istället
      // if (user.username) {
      //   fetchUser(user.username);
      // }
    });
  }, []);

  //När auth är fixat så ska den här fungera
  useEffect(() => {
    loggedInUser && setLoggedIn(true);
    // loggedInUser != undefined &&
    //   loggedInUser.username.toUpperCase() === project.owner.toUpperCase() &&
    //   setIsAdmin(true);
  }, [loggedInUser, project]);

  useEffect(() => {
    const fetchData = async () => {
      await getProject(userId, projectId).then((response) => {
        const { data } = response.data;
        setProject(data);
        setIsLoading(false);
      });
    };
    fetchData();
  }, [userId, projectId]);

  return (
    <Container className="justify-content-center">
      {!isLoading && (
        <ProjectViewComponent
          project={project}
          setProject={setProject}
          isAdmin={isAdmin}
          loggedIn={loggedIn}
          memberOf={memberOf}
          loggedInUser={loggedInUser}
        ></ProjectViewComponent>
      )}
    </Container>
  );
};
export default ProjectViewPage;
