import React from 'react';
import * as Auth from '../utils/Auth';
import { Button, Row, Card, Container } from 'react-bootstrap';
import { GoogleLoginButton } from 'react-social-login-buttons';
import './LoginPage.css';
const MSG_TIMEOUT = 5000;

export default class LoginPage extends React.Component {
  username = React.createRef();
  email = React.createRef();
  password = React.createRef();

  verificationCode = React.createRef();
  phoneNumber = React.createRef();
  timeout;

  state = {
    msg: '',
    msgColour: 'black',
    authState: Auth.AuthState.none,
    showSignUp: false,
  };

  setFadingMsg = (msg) => {
    this.setState({ msg, msgColour: 'black' });
    if (this.timeout) {
      clearTimeout(this.timeout);
    }
    this.timeout = setTimeout(() => {
      this.setState({ msgColour: 'lightgrey' });
    }, MSG_TIMEOUT);
  };

  clearMsg = () => {
    this.setState({ msg: '' });
  };

  handleSignIn = async (provider) => {
    this.clearMsg();
    this.setFadingMsg(await Auth.login(provider));
  };

  handleSignUp = async (provider) => {
    this.clearMsg();
    const msg = await Auth.signUp(provider, this.username.current.value);
    this.setFadingMsg(msg);
  };

  handleSignout = async () => {
    this.clearMsg();
    this.setFadingMsg(await Auth.logout());
  };

  handlePickUsername = async (e) => {
    e.preventDefault();
    this.clearMsg();
    this.setFadingMsg(await Auth.createUser(this.username.current.value));
  };

  handleAbortRegistration = async () => {
    this.clearMsg();
    await Auth.logout();
    this.setState({ msg: 'Registration aborted' });
  };

  showSignUp = () => {
    this.setState({ showSignUp: !this.state.showSignUp });
  };

  render = () => {
    return (
      <div style={{ margin: '20px' }}>
        {this.state.loggedInUser && this.state.authState === Auth.AuthState.authed && (
          <div>You are logged in as {this.state.loggedInUser}</div>
        )}
        {this.state.msg && <small style={{ color: this.state.msgColour }}>{this.state.msg}</small>}

        <div>
          <Container className="justify-content-center">
            <Row className=" mt-5 justify-content-center">
              <Card className="loginCard">
                <Card.Body className="justify-content-center text-center mt-5">
                  {this.state.showSignUp ? (
                    <fieldset>
                      <h1>Sign up</h1>

                      <input
                        className="mt-3"
                        type="text"
                        placeholder="User name"
                        ref={this.username}
                        autoFocus
                      ></input>
                      <div>
                        <Button
                          className="mt-3 mr-2"
                          variant="outline-danger"
                          onClick={() => this.showSignUp()}
                        >
                          Back
                        </Button>
                        <Button
                          id="googleLoginButton"
                          className="mt-3"
                          variant="outline-secondary"
                          onClick={() => this.handleSignUp(Auth.Providers.google)}
                        >
                          Sign up with Google
                        </Button>
                      </div>
                    </fieldset>
                  ) : (
                    <>
                      <h1 className="text-center">Sign in</h1>
                      <GoogleLoginButton
                        className="mt-3"
                        variant="outline-secondary"
                        onClick={() => this.handleSignIn(Auth.Providers.google)}
                      >
                        Sign in with Google
                      </GoogleLoginButton>
                      <div className="mt-3">
                        New to Lagalt?{' '}
                        <Button variant="success" onClick={() => this.showSignUp()}>
                          Sign up
                        </Button>
                      </div>
                    </>
                  )}
                </Card.Body>
              </Card>
            </Row>
          </Container>

          <br />
          <br />

          <br />
        </div>
        <div id="authContainer"></div>
      </div>
    );
  };
}
