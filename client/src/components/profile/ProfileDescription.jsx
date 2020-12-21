import React from 'react';

const ProfileDescription = (props) => {
  const { description } = props.user;
  return (
    <div className="justify-content-center">
      <h4>About me</h4>
      <div>
        {description} Lorem ipsum dolor sit amet consectetur adipisicing elit. Id officiis laborum
        aut temporibus illum ad voluptates, tempore sed totam assumenda exercitationem error
        consequuntur beatae quae blanditiis quis cupiditate aliquam earum. Lorem ipsum dolor sit
        amet consectetur adipisicing elit. Nostrum quibusdam iste ducimus sit delectus pariatur
        quam, molestias odit alias impedit reiciendis quae? Ullam consectetur deserunt explicabo ut.
        Eius eveniet quam culpa modi vitae vel repellat facere omnis asperiores quae excepturi
        laboriosam obcaecati est placeat necessitatibus, saepe accusantium sit similique voluptate
        itaque aliquam!
      </div>
    </div>
  );
};
export default ProfileDescription;
