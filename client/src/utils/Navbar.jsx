import React from "react";
import { Navbar as BootstrapNavbar, Nav, Button } from "react-bootstrap";

const Navbar = (props) => {

  const  onLoginClick  = () =>{
      props.history.push('/login')

    }
  return (
    <BootstrapNavbar bg="light" expand="lg">
      <BootstrapNavbar.Brand href="/">Lagalt</BootstrapNavbar.Brand>

      <Nav></Nav>
      <Button onClick={onLoginClick} variant='secondary'>Login</Button>
    </BootstrapNavbar>
  );
};
export default Navbar;
