import React from 'react';
import { Container } from 'react-bootstrap';

export default class ErrorPage extends React.Component {
  render = () => (
    <Container>
      <h1>Page not found</h1>
      Four, oh four; ✋ this is not the page you are looking for
    </Container>
  );
}
