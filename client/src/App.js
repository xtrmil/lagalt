import React from 'react';
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
import ChatTest from './pages/ChatTest';

const history = createBrowserHistory();

function App() {
  return (
    <Router history={history}>
      <Navbar history={history} />
      <Switch>
        <Route exact path="/project/:owner/:projectName/chat" component={ChatTest} />
        <Route exact path="/home" component={HomePage} />
        <Route exact path="/login" component={LoginPage} />
        <Route exact path="/register" component={RegisterPage} />
        <Route exact path="/project/create" component={CreateProjectPage} />
        <Route exact path="/project/:userId/:projectId" component={ProjectViewPage} />
        <Route exact path="/admin" component={ProjectAdminPage} />
        <Route exact path="/profile" component={ProfilePage} />
        <Route exact path="/projects" component={ProjectsPage} />
        <Route exact path="/message_board_and_chat" component={MessageBoardAndChatPage} />
        <Route exact path="/message_board_post" component={MessageBoardPostPage} />
      </Switch>
    </Router>
  );
}

export default App;
