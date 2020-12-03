import firebase from 'firebase/app';
import 'firebase/auth';
import { Observable } from 'rxjs';

const firebaseConfig = {
  apiKey: "AIzaSyBvGQf08nkANAW5Vb1BNq6P0FUS-a2GsNw",
  authDomain: "experis-lagalt.firebaseapp.com",
  databaseURL: "https://experis-lagalt.firebaseio.com",
  projectId: "experis-lagalt",
  storageBucket: "experis-lagalt.appspot.com",
  messagingSenderId: "98680547683",
  appId: "1:98680547683:web:9fc1bf29f760458553cb56",
  measurementId: "G-XL0C77WZWN"
};

if (firebase.apps.length === 0) {
  firebase.initializeApp(firebaseConfig);
}


const DevMode = {
  ignoreAll: 0,
  ignoreReCaptcha: 1,
  off: 2
}

// Dev Settings
const testPhoneNr = "+46700000000"
const testVerificationCode = "150803"
const devMode = DevMode.ignoreReCaptcha // circumvents recaptcha, sending real texts, and verification code

const recaptchaContainer = 'authContainer' // id of html element where the recaptcha auth is placed


export const loginStatus = () => {
  return new Observable(observer => {
    firebase.auth().onAuthStateChanged(user => {
      
      // console.log('new authState:', user)
      
      observer.next(Boolean(user))
    })
  })
}

export const providers = {
  google: 0,
  gitHub: 1,
  email: 2
}

export const logout = async () => {
  await firebase.auth().signOut()
  return 'You are now logged out'
}

export const login = provider => {
  switch (provider) {
    case providers.google:
      return thirdPartyAuth(new firebase.auth.GoogleAuthProvider())
    case providers.gitHub:
      return thirdPartyAuth(new firebase.auth.GithubAuthProvider())
    case providers.email:
      return createEmailUser()
    default:
      return 'Unsupported login provider'
  }
}

const thirdPartyAuth = async (provider) => {
  try {
    const credentials = await firebase.auth().signInWithPopup(provider)

    console.log('initial verification successful')
    
    let phoneNumber
    if(devMode === DevMode.ignoreAll) {
      phoneNumber = testPhoneNr
    } else {
      phoneNumber = prompt('Please enter your phone number. \n\nProclaimer: Google stores and uses phone numbers to improve spam and abuse prevention across all Google services. Standard rates may apply')
      if(phoneNumber.trim().length === 0) {
        return
      }
    }

    return smsVerification({
      phoneNumber,
      session: await firebase.auth().currentUser.multiFactor.getSession()
    })

  } catch (err) {
    if (err.code === 'auth/multi-factor-auth-required') {

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


const smsVerification = async (phoneInfoOptions, resolver) => {
  console.log(phoneInfoOptions)
  
  const recaptchaConfig = {
    size: 'invisible',
    callback: async (response) => {
      // reCAPTCHA solved, allow signInWithPhoneNumber.
      console.log('reCaptcha solved')
    }
  }

  try {
    if(devMode !== DevMode.off) {
      firebase.auth().settings.appVerificationDisabledForTesting = true
    }

    let appVerifier = new firebase.auth.RecaptchaVerifier(recaptchaContainer, recaptchaConfig)

    console.log('presenting reCaptcha')

    // presents recaptcha, then sends text
    let phoneAuthProvider = new firebase.auth.PhoneAuthProvider()
    const verificationId = await phoneAuthProvider.verifyPhoneNumber(phoneInfoOptions, appVerifier)
    
    console.log('text sent, prompting for code')
    
    let verificationCode
    if(devMode === DevMode.ignoreAll) {
      verificationCode = testVerificationCode
    } else {
      verificationCode = prompt('Please enter the verification that was sent to you')
    }

    // TODO Prompt user to type in verification code from sms
    const credentials = firebase.auth.PhoneAuthProvider.credential(verificationId, verificationCode)
    console.log('verification code sent')

    const multiFactorAssertion = firebase.auth.PhoneMultiFactorGenerator.assertion(credentials)
    console.log('assertion done')

    if(!resolver) { // no resolver => is enrollment
      await firebase.auth().currentUser.multiFactor.enroll(multiFactorAssertion, 'User phone number');
      return 'Enrollment done\nYou are now signed in as ' + firebase.auth().currentUser.displayName

    } else {
      const userCredentials = await resolver.resolveSignIn(multiFactorAssertion)    
      return "You are now signed in as " + userCredentials.additionalUserInfo.profile.name
    }
  } catch (err) {
    console.log('sms verification error', err)
    return err.message
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
    await firebase.auth().sendSignInLinkToEmail('iambumpfel@gmail.com', { url: 'http://localhost:8080/emailLink', handleCodeInApp: true })
    console.log('email sent')
  } catch (err) {
    console.error('login error')
    console.error(err)
  }
}