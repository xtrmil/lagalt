import React from 'react'
import * as Auth from '../utils/Auth';

export default class AuthTestComponent extends React.Component {
  
  componentDidMount() {
    Auth.loginStatus().subscribe(status => {
      this.setState({ isLoggedIn : Boolean(status) })
    })
  }

  state = {
    msg: '',
    loginStatus: false
  }
  
  handleSignIn = async provider => {
    this.setState({ msg: '' })
    const msg = await Auth.login(provider)
    this.setState({ msg })
  }
  
  handleSignout = async () => {
    this.setState({ msg: '' })
    const msg = await Auth.logout()
    this.setState({ msg })
  }  

  render = () => {
    return (
      <div style={{ margin: '20px' }}>
        {this.state.msg}
        
        <br />
        {!this.state.isLoggedIn
          ?
          <div>
            <button id="googleLoginButton" onClick={() => this.handleSignIn(Auth.providers.google)}>Sign in with Google</button>
            <br />
          </div>
          : <button onClick={this.handleSignout}>Sign out</button>
        }

        <div id="authContainer"></div>
      </div>
    )
  }

}
