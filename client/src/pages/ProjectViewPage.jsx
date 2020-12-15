import React from 'react';
import { Container } from 'react-bootstrap';
import ProjectViewComponent from '../components/projectView/ProjectViewComponent';

const ProjectViewPage = (props) => {
  const isAdmin = true;
  const loggedIn = true;
  const memberOf = true;
  const project = {
    title: 'Some Title',
    owner: 'ownersUserId',
    description:
      'Lorem ipsum dolor sit amet consectetur adipisicing elit. Totam ea hic eaque cumque asperiores nisi eligendi explicabo voluptatibus aliquid omnis, a atque magnam iure facilis laudantium! Quidem illo doloribus itaque maxime recusandae explicabo nulla quaerat nemo est blanditiis veritatis omnis neque vero praesentium laudantium officia consectetur non atque repudiandae, quia dolor debitis! Nisi, aut. Ducimus voluptatem cumque necessitatibus sapiente accusantium minus laborum alias quibusdam dolor, dolorum sequi deserunt explicabo iure ad sunt nesciunt repudiandae officiis, ipsa similique, exercitationem doloribus! Aspernatur recusandae quos similique eos rem dicta esse repellat, inventore laboriosam! At impedit voluptas delectus. Soluta tempore ab accusamus impedit sit?',
    industry: 'Music',
    status: 'IN_PROGRESS',
    createdTimeStamp: '2020-10-02',
    skills: ['WEB_DEV', 'SECURITY', 'REACT', 'ANGULAR'],
  };

  return (
    <Container className="justify-content-center">
      <ProjectViewComponent
        project={project}
        isAdmin={isAdmin}
        loggedIn={loggedIn}
        memberOf={memberOf}
      ></ProjectViewComponent>
    </Container>
  );
};
export default ProjectViewPage;
