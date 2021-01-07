import firebase from 'firebase/app';
import 'firebase/auth';
import { Observable } from 'rxjs';
import { EventEmitter } from 'events';
import { baseURLString } from './api/baseUrl';

const loginStatusEmitter = new EventEmitter();
const loginStatusEvent = 'change';

const host = baseURLString;

const firebaseConfig = {
  apiKey: 'AIzaSyBvGQf08nkANAW5Vb1BNq6P0FUS-a2GsNw',
  authDomain: 'experis-lagalt.firebaseapp.com',
  databaseURL: 'https://experis-lagalt.firebaseio.com',
  projectId: 'experis-lagalt',
  storageBucket: 'experis-lagalt.appspot.com',
  messagingSenderId: '98680547683',
  appId: '1:98680547683:web:9fc1bf29f760458553cb56',
  measurementId: 'G-XL0C77WZWN',
};

if (firebase.apps.length === 0) {
  firebase.initializeApp(firebaseConfig);
}

export const DevMode = {
  ignoreAll: '0', // circumvents reCaptcha, sending real texts, and verification code
  verify: '1', // circumvents reCaptcha & sending real texts, but prompts for verification code
  off: '2', // off. to avoid sending real texts, use testPhoneNr below
};

// Dev Settings
const testPhoneNr = '+46700000000';
const testVerificationCode = '150803';

const recaptchaContainer = 'authContainer'; // id of html element where the reCaptcha auth is placed

// dev
export const dev = {
  _value: DevMode.off,

  get mode() {
    return this._value;
  },

  set mode(value) {
    if (Object.values(DevMode).includes(value)) {
      this._value = value;
    }
  },
};

export const Providers = {
  google: 0,
};

export const AuthState = {
  authed: 0,
  chooseUsername: 1,
  none: 2,
};

// global variables
let gUsername;

export const loggedInUser = () => {
  return new Observable((observer) => {
    const unsubscribeFromAuthChanges = firebase.auth().onAuthStateChanged(async (user) => {
      if (user && Boolean(user.multiFactor?.enrolledFactors?.length > 0)) {
        const username = await getLoggedInUser();
        observer.next({ state: username ? AuthState.authed : AuthState.chooseUsername, username });
      } else {
        observer.next({ state: AuthState.none, username: null });
      }
      unsubscribeFromAuthChanges(); // use once. unsub when component is initialized
    });

    loginStatusEmitter.on(loginStatusEvent, async (data) => {
      observer.next(data);
    });
  });
};

const getLoggedInUser = async () => {
  const token = await getToken();
  if (!token) {
    return null;
  }

  const response = await fetch(host + '/loggedInUser', {
    headers: {
      Authorization: token,
    },
  });

  return await response.text();
};

export const getToken = async () => {
  const user = firebase.auth().currentUser;
  if (user) {
    return await user.getIdToken();
  }
  return null;
};

export const signUp = (provider, username) => {
  if (!username) {
    return 'No username given';
  }
  gUsername = username;
  return login(provider, username);
};

export const login = (provider) => {
  switch (provider) {
    case Providers.google:
      return thirdPartyAuth(new firebase.auth.GoogleAuthProvider());
    default:
      return 'Unsupported login provider';
  }
};

export const logout = async () => {
  gUsername = null;
  const token = await getToken();
  if (!token) {
    return;
  }

  const response = await fetch(host + '/logout', {
    method: 'GET',
    headers: {
      'Content-Type': 'application/json',
      Authorization: token,
    },
  });

  const serverMsg = JSON.parse(await response.text()).message;

  firebase.auth().signOut();
  loginStatusEmitter.emit(loginStatusEvent, { state: AuthState.none });
  return serverMsg;
};

// non exports

const thirdPartyAuth = async (provider) => {
  try {
    await firebase.auth().signInWithPopup(provider);

    let phoneNumber;
    if (gUsername) {
      if (dev.mode !== DevMode.off) {
        phoneNumber = testPhoneNr;
      } else {
        phoneNumber = prompt(
          'Please enter your phone number to verify your identity' +
            '\n\nProclaimer: Google stores and uses phone numbers to improve spam and abuse prevention across all Google services. Standard rates may apply',
        );
      }

      if (!phoneNumber) {
        return 'Login attempt aborted';
      }

      if (gUsername) {
        // new account
        return smsVerification({
          phoneNumber,
          session: await firebase.auth().currentUser.multiFactor.getSession(),
        });
      } else {
        firebase.auth().currentUser.delete();
        return 'There is no account tied to this email'; // Do you wish to create an account instead?
      }
    } else {
      // client tried to log in (and was verified through thirdPartyAuth) but no user was found in the db
      firebase.auth().signOut();
      return 'No user found';
    }
  } catch (err) {
    if (err.code === 'auth/multi-factor-auth-required') {
      // login to existing account
      return smsVerification(
        {
          multiFactorHint: err.resolver.hints[0],
          session: err.resolver.session,
        },
        err.resolver,
      );
    } else {
      return err.message;
    }
  }
};

const resetRecaptcha = () => {
  const container = document.querySelector('#' + recaptchaContainer);
  while (container.lastChild) {
    container.lastChild.remove();
  }
};

const smsVerification = async (phoneInfoOptions, resolver) => {
  try {
    if (dev.mode !== DevMode.off) {
      firebase.auth().settings.appVerificationDisabledForTesting = true;
    }

    const appVerifier = new firebase.auth.RecaptchaVerifier(recaptchaContainer);

    // presents recaptcha, then sends text
    const phoneAuthProvider = new firebase.auth.PhoneAuthProvider();
    const verificationId = await phoneAuthProvider.verifyPhoneNumber(phoneInfoOptions, appVerifier);

    resetRecaptcha();

    let verified = false;
    let promptMsg = '';
    while (!verified) {
      let verificationCode;
      if (dev.mode === DevMode.ignoreAll) {
        verificationCode = testVerificationCode;
      } else {
        verificationCode = prompt(
          `Please enter the verification code that was sent to your phone${promptMsg}`,
        );
      }
      const credentials = firebase.auth.PhoneAuthProvider.credential(
        verificationId,
        verificationCode,
      );

      const multiFactorAssertion = firebase.auth.PhoneMultiFactorGenerator.assertion(credentials);

      try {
        if (!resolver) {
          // no resolver => is enrollment
          await firebase
            .auth()
            .currentUser.multiFactor.enroll(multiFactorAssertion, 'User phone number');
          // auth user was enrolled, but no db user was created yet
        } else {
          await resolver.resolveSignIn(multiFactorAssertion);
        }
        verified = true;
      } catch (error) {
        promptMsg = '\n\nCode verification failed';
      }
    }

    if (gUsername) {
      return createUser(gUsername);
    } else {
      return authUser();
    }
  } catch (err) {
    if (err.code === 'auth/argument-error') {
      return 'Error: code verification failed';
    }
    return err.message;
  }
};

export const createUser = async (username) => {
  gUsername = null;

  const response = await fetch(host + '/signup', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      Authorization: await getToken(),
    },
    body: JSON.stringify({ username: username }),
  });

  const responseBody = JSON.parse(await response.text());

  if (response.ok) {
    loginStatusEmitter.emit(loginStatusEvent, {
      state: AuthState.authed,
      username: responseBody.data,
    });
    return 'You are now signed in as ' + responseBody.data;
  } else if (response.status === 409 || response.status === 406) {
    // user exists with the given credentials or username is invalid
    loginStatusEmitter.emit(loginStatusEvent, { state: AuthState.chooseUsername });
  } else if (response.status === 403) {
    const answer = window.confirm(responseBody.message + '. Do you want to log in instead?');
    if (answer) {
      return authUser();
    }
    await firebase.auth().signOut();
    return 'Login aborted';
  } else {
    await firebase.auth().signOut();
    loginStatusEmitter.emit(loginStatusEvent, { state: AuthState.none });
  }
  return responseBody.message;
};

const authUser = async () => {
  gUsername = null;

  const response = await fetch(host + '/signin', {
    method: 'GET',
    headers: {
      Authorization: await getToken(),
    },
  });

  const responseBody = JSON.parse(await response.text());

  if (response.ok) {
    loginStatusEmitter.emit(loginStatusEvent, {
      state: AuthState.authed,
      username: responseBody.data,
    });
    return 'You are now signed in as ' + responseBody.data;
  } else {
    firebase.auth().signOut();
    loginStatusEmitter.emit(loginStatusEvent, { state: AuthState.none });
    return responseBody.message;
  }
};
