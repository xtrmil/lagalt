import React from 'react';
import * as Auth from '../utils/Auth';

const MSG_TIMEOUT = 5000;

export default class AuthTestComponent extends React.Component {
  componentDidMount = () => {
    Auth.loggedInUser().subscribe((user) => {
      this.setState({ authState: user.state, loggedInUser: user.username });
    });
  };

  username = React.createRef();
  email = React.createRef();
  password = React.createRef();

  verificationCode = React.createRef();
  phoneNumber = React.createRef();
  timeout;

  state = {
    msg: '',
    msgColour: 'black',
    loggedInUser: null,
    authState: Auth.AuthState.none,
  };

  setFadingMsg = (msg) => {
    console.log(`setFadingMsg(${msg})`);
    this.setState({ msg, msgColour: 'black' });
    if (this.timeout) {
      clearTimeout(this.timeout);
    }
    this.timeout = setTimeout(() => {
      this.setState({ msgColour: 'lightgrey' });
    }, MSG_TIMEOUT);
  };

  clearMsg = () => {
    console.log('clearMsg()');
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

  render = () => {
    let content;
    switch (this.state.authState) {
      case Auth.AuthState.authed:
        content = <button onClick={this.handleSignout}>Sign out</button>;
        break;
      case Auth.AuthState.chooseUsername:
        content = (
          <form onSubmit={this.handlePickUsername}>
            Continue user registration
            <br />
            <input type="text" ref={this.username} placeholder="Enter a user name" />
            <br />
            <button type="submit">Submit</button>
            <button type="button" onClick={this.handleAbortRegistration}>
              Abort registration
            </button>
          </form>
        );
        break;
      default:
        content = (
          <div>
            <fieldset>
              <legend>Sign in</legend>
              <button
                id="googleLoginButton"
                onClick={() => this.handleSignIn(Auth.Providers.google)}
              >
                Sign in with Google
              </button>
            </fieldset>

            <br />
            <br />
            <fieldset>
              <legend>Sign up</legend>
              <input type="text" placeholder="User name" ref={this.username} autoFocus></input>
              <br />
              <button
                id="googleLoginButton"
                onClick={() => this.handleSignUp(Auth.Providers.google)}
              >
                Sign up with Google
              </button>
            </fieldset>

            <br />

            <select
              name="devMode"
              value={Auth.dev.mode}
              onChange={(e) => {
                Auth.dev.mode = e.target.value;
                this.forceUpdate();
              }}
            >
              <option value={Auth.DevMode.ignoreAll}>Full dev mode</option>
              <option value={Auth.DevMode.verify}>Enter verification code</option>
              <option value={Auth.DevMode.off}>Dev mode off</option>
            </select>
          </div>
        );
    }

    return (
      <div style={{ margin: '20px' }}>
        {this.state.loggedInUser && this.state.authState === Auth.AuthState.authed && (
          <div>You are logged in as {this.state.loggedInUser}</div>
        )}

        {this.state.msg && <small style={{ color: this.state.msgColour }}>{this.state.msg}</small>}

        <br />

        {content}

        <div id="authContainer"></div>
      </div>
    );
  };
}
