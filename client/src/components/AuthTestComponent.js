import React from 'react'
import * as Auth from '../utils/Auth';

const MSG_TIMEOUT = 5000

export default class AuthTestComponent extends React.Component {

  componentDidMount = () => {
    Auth.loggedInUser().subscribe(async user => {
      this.setState({ loggedInUser: await user })
    })
  }

  username = React.createRef()
  
  state = {
    msg: '',
    loggedInUser: null
  }

  setMsg = (msg) => {
    this.setState({ msg })
    setTimeout(() => {
      this.setState({ msg: '' })
    }, MSG_TIMEOUT)
  }

  handleSignUp = async provider => {
    this.setState({ msg: '' })
    const msg = await Auth.signUp(provider, this.username.current.value)
    this.setMsg(msg)
  }

  handleSignIn = async provider => {
    this.setState({ msg: '' })
    const msg = await Auth.login(provider)
    this.setMsg(msg)
  }
  
  handleSignout = async () => {
    this.setState({ msg: '' })
    const msg = await Auth.logout()
    this.setMsg(msg)
  }

  render = () => (
    <div style={{ margin: '20px' }}>


      {this.state.loggedInUser &&
        <div>
          You are logged in as {this.state.loggedInUser}
        </div>
      }

      {this.state.msg &&
        <small>
          {this.state.msg}
        </small>
      }

      <br />
      {!this.state.loggedInUser
        ?
          <div>
            <fieldset>
              <legend>Sign in</legend>
              <button id="googleLoginButton" onClick={() => this.handleSignIn(Auth.providers.google)}>Sign in with Google</button>
            </fieldset>

              <br />
              <br />
            <fieldset>
              <legend>Sign up</legend>
              <input type="text" placeholder="User name" ref={this.username} autoFocus></input>
              <br />
              <button id="googleLoginButton" onClick={() => this.handleSignUp(Auth.providers.google)}>Sign up with Google</button>
            </fieldset>

            <br />

            <select name="devMode" value={Auth.dev.mode} onChange={e => { Auth.dev.mode = e.target.value; this.forceUpdate() }}>
              <option value={Auth.DevMode.ignoreAll}>Full dev mode</option>
              <option value={Auth.DevMode.verify}>Enter verification code</option>
              <option value={Auth.DevMode.off}>Dev mode off</option>
            </select>

          </div>
        : <button onClick={this.handleSignout}>Sign out</button>
      }

      <div id="authContainer"></div>
    </div>
  )

}
