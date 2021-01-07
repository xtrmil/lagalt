import React, { useEffect, useState } from 'react';
import Navbar from './utils/Navbar';
import HomePage from './pages/HomePage';
import LoginPage from './pages/LoginPage';
import ProjectViewPage from './pages/ProjectViewPage';
import ProfilePage from './pages/ProfilePage';
import CreateProjectPage from './pages/CreateProjectPage';
import { Router, Route, Switch } from 'react-router-dom';
import 'bootstrap/dist/css/bootstrap.min.css';
import { createBrowserHistory } from 'history';
import ChatPage from './pages/ChatPage';
import * as Auth from './utils/Auth';
import ProtectedRoute from './components/ProtectedRoute';
import LoginRoute from './components/LoginRoute';
import ErrorPage from './pages/ErrorPage';
const history = createBrowserHistory();

function App() {
  const [loggedInUser, setLoggedInUser] = useState('');
  const [isLoading, setIsLoading] = useState(true);
  useEffect(() => {
    Auth.loggedInUser().subscribe((response) => {
      setLoggedInUser(response.username);
      setIsLoading(false);
    }, []);
  });
  return (
    <Router history={history}>
      {!isLoading && (
        <>
          <Navbar history={history} loggedInUser={loggedInUser} />
          <Switch>
            <Route exact path="/" component={HomePage} />
            <LoginRoute exact path="/login" component={LoginPage} loggedInUser={loggedInUser} />
            <ProtectedRoute
              exact
              path="/project/create"
              component={CreateProjectPage}
              loggedInUser={loggedInUser}
            />
            <Route exact path="/project/:userId/:projectId" component={ProjectViewPage} />
            <ProtectedRoute
              exact
              path="/profile"
              component={ProfilePage}
              loggedInUser={loggedInUser}
            />
            <ProtectedRoute
              exact
              path="/project/:owner/:projectName/chat"
              component={ChatPage}
              loggedInUser={loggedInUser}
            />
            <Route path="*">
              <ErrorPage />
            </Route>
          </Switch>
        </>
      )}
    </Router>
  );
}

export default App;
