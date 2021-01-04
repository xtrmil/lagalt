import React from 'react';

const ProfileDescription = (props) => {
  const { description } = props.user;
  return (
    <div className="justify-content-center">
      <h4>About me</h4>
      {description ? <div>{description}</div> : <div>No description</div>}
    </div>
  );
};
export default ProfileDescription;
