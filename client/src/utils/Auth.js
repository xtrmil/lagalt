import firebase from 'firebase/app';
import 'firebase/auth';
import { Observable } from 'rxjs';
import { EventEmitter } from 'events'

const loginStatusEmitter = new EventEmitter()
const loginStatusEvent = 'change'

const host = 'http://localhost:8080/api/v1'

const firebaseConfig = {
  apiKey: 'AIzaSyBvGQf08nkANAW5Vb1BNq6P0FUS-a2GsNw',
  authDomain: 'experis-lagalt.firebaseapp.com',
  databaseURL: 'https://experis-lagalt.firebaseio.com',
  projectId: 'experis-lagalt',
  storageBucket: 'experis-lagalt.appspot.com',
  messagingSenderId: '98680547683',
  appId: '1:98680547683:web:9fc1bf29f760458553cb56',
  measurementId: 'G-XL0C77WZWN'
};

if (firebase.apps.length === 0) {
  firebase.initializeApp(firebaseConfig);
}

export const DevMode = {
  ignoreAll: '0', // circumvents reCaptcha, sending real texts, and verification code
  verify: '1', // circumvents reCaptcha & sending real texts, but prompts for verification code
  off: '2' // off. to avoid sending real texts, use testPhoneNr below
}

// Dev Settings
const testPhoneNr = '+46700000000'
const testVerificationCode = '150803'

const recaptchaContainer = 'authContainer' // id of html element where the reCaptcha auth is placed

// dev
export const dev = {
  _value: DevMode.ignoreAll,

  get mode() {
    return this._value
  },

  set mode(value) {
    if (Object.values(DevMode).includes(value)) {
      this._value = value
    }
  }

}

export const Providers = {
  google: 0,
}

export const AuthState = {
  authed: 0,
  chooseUsername: 1,
  none: 2,
}

// global variables
let gCurrentUser
let gUsername
// let gResolver
// let gVerificationId

export const loggedInUser = () => {
  return new Observable(observer => {

    firebase.auth().onAuthStateChanged(async user => {
      gCurrentUser = user
      console.log('auth state changed', user)

      if (user && Boolean(user.multiFactor?.enrolledFactors?.length > 0)) {
          console.log('AuthState: multi factor authed')
          const username = await getLoggedInUser()
          observer.next({ state: username ? AuthState.authed : AuthState.chooseUsername, username })
        } else {
          console.log('AuthState: no auth')
        observer.next({ state: AuthState.none, username: null })
      }
    })

    loginStatusEmitter.on(loginStatusEvent, async data => {
      console.log('listener received data', data)
      observer.next(data)
    })
  })
}

const getLoggedInUser = async () => {
  const token = await getToken()
  if (!token) {
    return null
  }

  const response = await fetch(host + '/loggedInUser', {
    headers: {
      Authorization: token
    }
  })

  return await response.text()
}

export const getToken = async () => {
  const user = gCurrentUser
  if (user) {
    return await user.getIdToken()
  }
  return null
}

export const signUp = (provider, username) => {
  if (!username) {
    return 'No username given'
  }
  gUsername = username
  return login(provider, username)
}

export const login = (provider) => {
  switch (provider) {
    case Providers.google:
      return thirdPartyAuth(new firebase.auth.GoogleAuthProvider())
    default:
      return 'Unsupported login provider'
  }
}

export const logout = async () => {
  gUsername = null
  const token = await getToken()
  if (!token) {
    return
  }

  const response = await fetch(host + '/logout', {
    method: 'GET',
    headers: {
      'Content-Type': 'application/json',
      Authorization: token
    },
  })

  const body = await response.text()

  firebase.auth().signOut()
  return body
}

// non exports

const thirdPartyAuth = async (provider) => {
  try {
    await firebase.auth().signInWithPopup(provider)
    
    let phoneNumber
    if(gUsername) {
      console.log('Enrolling new user...', gUsername)
      if (dev.mode !== DevMode.off) {
        phoneNumber = testPhoneNr
      } else {
        phoneNumber = prompt('')
      }

      if (!phoneNumber) {
        return 'Login attempt aborted'
      }
      console.log('using phone nr', phoneNumber)
    
        if (gUsername) {
          // new account
          return smsVerification({
            phoneNumber,
            session: await gCurrentUser.multiFactor.getSession()
          })
        } else {
          gCurrentUser.delete()
          return 'There is no account tied to this email' // Do you wish to create an account instead?
        }
    } else {
      // client tried to log in (and was verified through thirdPartyAuth) but no user was found in the db
      firebase.auth().signOut()
      return 'No user found'
    }

  } catch (err) {
    if (err.code === 'auth/multi-factor-auth-required') {

      console.log('auth user found')

      if (!gUsername) {
        console.log('Signing in to existing account...')
      }

      // login to existing account
      return smsVerification({
        multiFactorHint: err.resolver.hints[0],
        session: err.resolver.session
      }, err.resolver)

    } else {
      console.error('login error 1')
      console.error(err)
      return err.message
    }
  }
}


const resetRecaptcha = () => {
  const container = document.querySelector('#' + recaptchaContainer)
  while (container.lastChild) {
    container.lastChild.remove()
  }
}


const smsVerification = async (phoneInfoOptions, resolver) => {
  try {
    if (dev.mode !== DevMode.off) {
      firebase.auth().settings.appVerificationDisabledForTesting = true
      console.log('circumventing reCaptcha...')
    } else {
      console.log('presenting reCaptcha...')
    }

    const appVerifier = new firebase.auth.RecaptchaVerifier(recaptchaContainer)

    // presents recaptcha, then sends text
    const phoneAuthProvider = new firebase.auth.PhoneAuthProvider()
    const verificationId = await phoneAuthProvider.verifyPhoneNumber(phoneInfoOptions, appVerifier)
    console.log('reCaptcha solved')
    resetRecaptcha()
   
    console.log('text sent, prompting for code')

    let verificationCode
    if(dev.mode === DevMode.ignoreAll) {
      verificationCode = testVerificationCode
    } else {
      verificationCode = prompt('Please enter the verification code that was sent to your phone')
    }

    const credentials = firebase.auth.PhoneAuthProvider.credential(verificationId, verificationCode)
    console.log('verification code sent')

    const multiFactorAssertion = firebase.auth.PhoneMultiFactorGenerator.assertion(credentials)
    console.log('assertion done')


    if (!resolver) { // no resolver => is enrollment
      await gCurrentUser.multiFactor.enroll(multiFactorAssertion, 'User phone number');
      console.log('enrolled user')

      return createUser(gUsername)

    } else {
      await resolver.resolveSignIn(multiFactorAssertion)
      if (gUsername) {
        // auth user was enrolled, but no db user was created yet
        return createUser(gUsername)
      }
      return authUser()
    }
 
  } catch (err) {
    console.log('sms verification error', err)
    return err.message
  }
}

export const createUser = async (username) => {
  gUsername = null
  console.log(`createUser(${username})`)
  const token = await getToken()
  if (!token) {
    return `Error: Can't create user. You are not authenticated`
  }

  const response = await fetch(host + '/signup', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      Authorization: token,
    },
    body: JSON.stringify({ userId: username })
  })

  console.log('auth ' + (response.ok ? 'successful' : 'failed'))
  
  const serverMsg = await response.text()
  if (response.ok) {
    loginStatusEmitter.emit(loginStatusEvent, { state: AuthState.authed, username: serverMsg })
    return 'You are now signed in as ' + serverMsg
  } else if(response.status === 409) {
    // user exists with the given credentials
    console.log('no action')
  } else {
    await firebase.auth().signOut()
  }
  console.log('returning server msg', serverMsg)
  return serverMsg;
}

const authUser = async () => {
  gUsername = null
  const token = await getToken()
  if (!token) {
    console.log('not logged in')
    return 'You are not authenticated'
  }

  const response = await fetch(host + '/signin', {
    method: 'GET',
    headers: {
      Authorization: token
    }
  })

  console.log('auth ' + (response.ok ? 'successful' : 'failed'))

  const serverMsg = await response.text()
  if (response.ok) {
    loginStatusEmitter.emit(loginStatusEvent, { state: AuthState.authed, username: serverMsg })
    return 'You are now signed in as ' + serverMsg
  } else {
    firebase.auth().signOut()
    loginStatusEmitter.emit(loginStatusEvent, { state: AuthState.none })
    console.log('server error on login', serverMsg)
    return serverMsg
  }
}


// Email Test. not done

const createEmailUser = async () => {
  try {
    const credentials = await firebase.auth().createUserWithEmailAndPassword('iambumpfel@gmail.com', 'testar')

    console.log('email sent')
    console.log('credentials', credentials)
  } catch (err) {
    console.error('login error')
    console.error(err)
  }
}

const sendEmailLink = async (provider) => {
  try {
    const url = host + '/aValidEmailLink'
    await firebase.auth().sendSignInLinkToEmail('iambumpfel@gmail.com', { url, handleCodeInApp: true })
    console.log('email sent')
  } catch (err) {
    console.error('login error')
    console.error(err)
  }
}