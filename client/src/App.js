import React from 'react';
import Navbar from './utils/Navbar';
import HomePage from './pages/HomePage';
import LoginPage from './pages/LoginPage';
import RegisterPage from './pages/RegisterPage';
import ProjectViewPage from './pages/ProjectViewPage';
import ProjectsPage from './pages/ProjectsPage';
import ProjectAdminPage from './pages/ProjectAdminPage';
import ProfilePage from './pages/ProfilePage';
import { Router, Route, Switch } from 'react-router-dom';
import 'bootstrap/dist/css/bootstrap.min.css';
import { createBrowserHistory } from 'history';
import CreateProjectPage from './pages/CreateProjectPage';

const history = createBrowserHistory();

function App() {
  return (
    <Router history={history}>
      <Navbar history={history} />
      <Switch>
        <Route exact path="/" component={HomePage} />
        <Route exact path="/login" component={LoginPage} />
        <Route exact path="/register" component={RegisterPage} />
        <Route exact path="/project/create" component={CreateProjectPage} />
        <Route exact path="/project/:userId/:projectId" component={ProjectViewPage} />
        <Route exact path="/admin" component={ProjectAdminPage} />
        <Route exact path="/profile" component={ProfilePage} />
        <Route exact path="/projects" component={ProjectsPage} />
      </Switch>
    </Router>
  );
}

export default App;
