import React from 'react';
import { Navbar as BootstrapNavbar, Nav, Button, NavDropdown } from 'react-bootstrap';

const Navbar = (props) => {
  const onLoginClick = () => {
    props.history.push('/login');
  };
  const onRegisterClick = () => {
    props.history.push('/register');
  };

  return (
    <BootstrapNavbar bg="dark" variant="dark" expand="lg">
      <BootstrapNavbar.Brand className="light" href="/">
        Lagalt
      </BootstrapNavbar.Brand>

      <Nav className="mr-auto">
        <NavDropdown title="Profile" id="basic-nav-dropdown">
          <NavDropdown.Item href="/profile">Show profile</NavDropdown.Item>
          <NavDropdown.Item href="/profile">Profile settings</NavDropdown.Item>
        </NavDropdown>
        <NavDropdown title="Projects" id="basic-nav-dropdown">
          <NavDropdown.Item href="/project/create">New project</NavDropdown.Item>
          <NavDropdown.Item href="/projects">Users projects</NavDropdown.Item>
          <NavDropdown.Divider />
          <NavDropdown.Item href="/admin">Project admin</NavDropdown.Item>
        </NavDropdown>
      </Nav>

      <Button onClick={onLoginClick} variant="dark">
        Login
      </Button>
      <Button onClick={onRegisterClick} variant="primary">
        Sign up
      </Button>
    </BootstrapNavbar>
  );
};
export default Navbar;
