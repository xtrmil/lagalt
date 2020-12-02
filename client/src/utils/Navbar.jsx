import React from 'react';
import { Navbar as BootstrapNavbar, Nav, Button } from 'react-bootstrap';

const Navbar = (props) => {
  const onLoginClick = () => {
    props.history.push('/login');
  };
  return (
    <BootstrapNavbar bg="dark" variant="dark" expand="lg">
      <BootstrapNavbar.Brand className="light" href="/">
        Lagalt
      </BootstrapNavbar.Brand>

      <Nav className="mr-auto">
        <Nav.Link href="#placeholder">Placeholder</Nav.Link>
        <Nav.Link href="#placeholder">Placeholder</Nav.Link>
      </Nav>
      <Button onClick={onLoginClick} variant="secondary">
        Login
      </Button>
    </BootstrapNavbar>
  );
};
export default Navbar;
