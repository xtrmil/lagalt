import React, { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import { Container } from 'react-bootstrap';
import ProjectViewComponent from '../components/projectView/ProjectViewComponent';
import { getProject } from '../utils/api/project';

const ProjectViewPage = (props) => {
  const [project, setProject] = useState({});
  const [isLoading, setIsLoading] = useState(true);
  const isAdmin = true;
  const loggedIn = true;
  const memberOf = true;
  const { userId, projectId } = useParams();
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
        ></ProjectViewComponent>
      )}
    </Container>
  );
};
export default ProjectViewPage;
