import React, { useEffect, useState } from 'react';
import Navbar from './utils/Navbar';
import HomePage from './pages/HomePage';
import LoginPage from './pages/LoginPage';
import RegisterPage from './pages/RegisterPage';
import ProjectViewPage from './pages/ProjectViewPage';
import ProjectsPage from './pages/ProjectsPage';
import ProjectAdminPage from './pages/ProjectAdminPage';
import ProfilePage from './pages/ProfilePage';
import MessageBoardAndChatPage from './pages/MessageBoardAndChatPage';
import CreateProjectPage from './pages/CreateProjectPage';
import MessageBoardPostPage from './pages/MessageBoardPostPage';
import { Router, Route, Switch } from 'react-router-dom';
import 'bootstrap/dist/css/bootstrap.min.css';
import { createBrowserHistory } from 'history';
import ChatPage from './pages/ChatPage';
import AuthTestComponent from './components/AuthTestComponent';
import * as Auth from './utils/Auth';
import ProtectedRoute from './components/ProtectedRoute';
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
            <Route exact path="/projects/:owner/:projectName/chat" component={ChatPage} />
            <Route exact path="/home" component={HomePage} />
            <Route exact path="/login" component={AuthTestComponent} />
            <Route exact path="/register" component={RegisterPage} />
            <ProtectedRoute
              exact
              path="/project/create"
              component={CreateProjectPage}
              loggedInUser={loggedInUser}
            />
            <Route exact path="/project/:userId/:projectId" component={ProjectViewPage} />
            <Route exact path="/admin" component={ProjectAdminPage} />
            <ProtectedRoute
              exact
              path="/profile"
              component={ProfilePage}
              loggedInUser={loggedInUser}
            />
            <Route exact path="/projects" component={ProjectsPage} />
            <Route exact path="/message_board_and_chat" component={MessageBoardAndChatPage} />
            <Route exact path="/message_board_post" component={MessageBoardPostPage} />
          </Switch>
        </>
      )}
    </Router>
  );
}

export default App;
