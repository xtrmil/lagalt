import React from 'react';
import { Navbar as BootstrapNavbar, Nav, Button } from 'react-bootstrap';

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
        <Nav.Link href="/profile">Profile</Nav.Link>
        <Nav.Link href="/projects">Projects</Nav.Link>
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
