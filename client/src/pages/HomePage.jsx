import React, { useState, useEffect } from 'react';
import './HomePage.css';
import { Container } from 'react-bootstrap';
import { getProjectsList } from '../utils/api/project';
import ProjectItem from '../components/home/ProjectItem';
import * as Auth from '../utils/Auth';
import { getUserByUserId } from '../utils/api/user';

const HomePage = (props) => {
  const [projectList, setProjectList] = useState([]);
  const [loggedInUser, setLoggedInUser] = useState();
  const [isLoading, setIsLoading] = useState(true);
  useEffect(() => {
    let isSubscribed = true;
    const fetchProjects = async () => {
      await getProjectsList().then((response) => {
        if (isSubscribed) {
          setProjectList(response.data.data);
          setIsLoading(false);
        }
      });
    };
    const fetchUser = async (userId) => {
      await getUserByUserId(userId).then((response) => {
        if (isSubscribed) {
          setLoggedInUser(response.data);
        }
      });
    };
    Auth.loggedInUser().subscribe((user) => {
      if (user.username) {
        fetchUser(user.username);
      }
    });
    fetchProjects();
    return () => {
      isSubscribed = false;
    };
  }, []);

  const displayProjects = projectList.map((project, index) => {
    if (projectList.map.length > 0)
      return (
        <ProjectItem
          key={index}
          project={project}
          loggedInUser={loggedInUser}
          history={props.history}
        ></ProjectItem>
      );
  });

  return <>{!isLoading ? <Container className="mt-4">{displayProjects}</Container> : ''}</>;
};

export default HomePage;
