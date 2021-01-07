import React from 'react';
import { Navbar as BootstrapNavbar, Nav, Button } from 'react-bootstrap';
import * as Auth from '../utils/Auth';

const Navbar = (props) => {
  const { loggedInUser } = props;

  const onLoginClick = () => {
    props.history.push('/login');
  };

  const onLogoutClick = async () => {
    await Auth.logout().then(() => {
      props.history.push('/login');
    });
  };

  return (
    <BootstrapNavbar bg="dark" variant="dark" expand="lg">
      <BootstrapNavbar.Brand className="light" href="/">
        Lagalt
      </BootstrapNavbar.Brand>

      <Nav className="mr-auto">
        <Nav.Link href="/profile">Show profile</Nav.Link>
        <Nav.Link href="/project/create">New project</Nav.Link>
      </Nav>
      {loggedInUser ? (
        <>
          <Button onClick={onLogoutClick}>Logout</Button>
        </>
      ) : (
        <>
          <Button onClick={onLoginClick} variant="dark">
            Login
          </Button>
        </>
      )}
    </BootstrapNavbar>
  );
};
export default Navbar;
