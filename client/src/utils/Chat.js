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

const fetchLimit = 10;
const db = firebase.firestore();
let cached = {};
let unsub;

const getDbPath = (owner, title) => {
  if (!cached.dbPath) {
    cached.dbPath = chatAPI.getDbPath(owner, title);
  }
  return cached.dbPath;
};

export const chatData = async (owner, title) => {
  const dbPath = await getDbPath(owner, title);

  if (unsub) {
    unsub();
  }
  return new Observable((observer) => {
    unsub = db
      .collection(dbPath)
      .orderBy('timestamp')
      .limitToLast(fetchLimit)
      .onSnapshot((collection) => {
        const arr = [];
        collection.docChanges().forEach((change) => {
          const docData = change.doc.data();
          if (change.type === 'added') {
            arr.push(new ChatMessage(change.doc.id, docData.timestamp, docData.user, docData.text));
          }
        });
        observer.next(arr);
      });
  });
};

export const getEarlierMessages = async (lastMsg) => {
  const response = await db
    .collection(await getDbPath())
    .orderBy('timestamp')
    .endBefore(lastMsg.timestamp)
    .limitToLast(fetchLimit)
    .get();
  return response.docs.map(
    (doc) => new ChatMessage(doc.id, doc.data().timestamp, doc.data().user, doc.data().text),
  );
};

export const sendMsg = async (text, owner, title) => {
  chatAPI.newChatMessage(text, owner, title);
};

class ChatMessage {
  constructor(id, timestamp, user, text) {
    this.id = id;
    this.timestamp = timestamp;
    this.user = user;
    this.text = text;
  }
}
