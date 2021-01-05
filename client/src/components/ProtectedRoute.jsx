import React from 'react';
import { Route, Redirect } from 'react-router-dom';

const ProtectedRoute = ({ component: Component, loggedInUser: loggedInUser, ...rest }) => {
  return (
    <Route
      {...rest}
      render={(props) =>
        loggedInUser ? (
          <Component {...props} loggedInUser={loggedInUser} />
        ) : (
          <Redirect
            to={{
              pathname: '/login',
              state: { from: props.location },
            }}
          />
        )
      }
    />
  );
};

export default ProtectedRoute;
