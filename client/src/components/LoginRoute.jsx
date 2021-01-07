import React from 'react';
import { Route, Redirect } from 'react-router-dom';

const LoginRoute = ({ component: Component, loggedInUser: loggedInUser, ...rest }) => {
  return (
    <Route
      {...rest}
      render={(props) =>
        !loggedInUser ? (
          <Component {...props} loggedInUser={loggedInUser} />
        ) : (
          <Redirect
            to={{
              pathname: '/',
              state: { from: props.location },
            }}
          />
        )
      }
    />
  );
};

export default LoginRoute;
