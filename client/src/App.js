import React from 'react';
import Navbar from './utils/Navbar';
import HomePage from './pages/HomePage';
import LoginPage from './pages/LoginPage';
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
        <Route exact path="/profile" component={ProfilePage} />
        <Route exact path="/project/create" component={CreateProjectPage} />
      </Switch>
    </Router>
  );
}

export default App;
