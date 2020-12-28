import firebase from 'firebase/app';
import 'firebase/firestore';
import { Observable } from 'rxjs';
import * as chatAPI from './api/chat';

const firebaseConfig = {
  apiKey: 'AIzaSyAVWbW60FddjaHMM6HKERGtLeKR7-t_CQ8',
  authDomain: 'experis-lagalt.firebaseapp.com',
  databaseURL: 'https://experis-lagalt.firebaseio.com',
  projectId: 'experis-lagalt',
  storageBucket: 'experis-lagalt.appspot.com',
  messagingSenderId: '98680547683',
  appId: '1:98680547683:web:2a69748de6a75ab653cb56',
  measurementId: 'G-Q9DHZ24SGL',
};

if (!firebase.apps.find((app) => app.name === 'chat')) {
  firebase.initializeApp(firebaseConfig, 'chat');
}

const db = firebase.firestore();
let cachedDbPath;

export const chatData = (owner, title) => {
  return new Observable(async (observer) => {
    if (!cachedDbPath) {
      cachedDbPath = chatAPI.getDBPath(owner, title);
    }
    db.collection(await cachedDbPath)
      .orderBy('timestamp')
      .limitToLast(10)
      .onSnapshot((collection) => {
        const arr = collection.docChanges().map((change) => ({
          id: change.doc.id,
          timestamp: change.doc.data().timestamp,
          user: change.doc.data().user,
          text: change.doc.data().text,
        }));
        observer.next(arr);
      });
  });
};

export const sendMsg = async (text, owner, title) => {
  chatAPI.newChatMessage(text, owner, title);
};
