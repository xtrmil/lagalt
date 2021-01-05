import firebase from 'firebase/app';
import 'firebase/firestore';
import { iif, Observable } from 'rxjs';
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
let unsubs = {};

const getDbPath = (owner, title) => {
  if (
    !cached.dbPath ||
    (owner != null && owner != cached.owner) ||
    (title != null && title != cached.title)
  ) {
    cached.owner = owner;
    cached.title = title;
    cached.dbPath = chatAPI.getDbPath(owner, title);
  }
  return cached.dbPath;
};

export const shouldTriggerUpdate = (owner, title) => {
  return owner != cached.owner || title != cached.title;
};

export const unsubscribeAll = () => {
  for (const key in unsubs) {
    unsubs[key]();
  }
};

export const chatData = async (owner, title) => {
  const dbPath = await getDbPath(owner, title);
  unsubscribeAll();

  if (!(await checkAccess(dbPath))) {
    throw new Error();
  }

  return new Observable((observer) => {
    unsubs.chat = db
      .collection(dbPath)
      .orderBy('timestamp')
      .limitToLast(fetchLimit)
      .onSnapshot((collection) => {
        const arr = [];
        collection.docChanges().forEach((change) => {
          const docData = change.doc.data();
          if (change.type === 'added') {
            arr.push(new ChatMessage(docData.timestamp, docData.user, docData.text));
          }
        });
        observer.next(arr);
      });
  });
};

const checkAccess = async (dbPath) => {
  return db
    .collection(dbPath)
    .get()
    .then(() => true)
    .catch((err) => false);
};

export const getEarlierMessages = async (lastMsg) => {
  const response = await db
    .collection(await getDbPath())
    .orderBy('timestamp')
    .endBefore(lastMsg.timestamp)
    .limitToLast(fetchLimit)
    .get();
  return response.docs.map((doc) => {
    const docData = doc.data();
    return new ChatMessage(docData.timestamp, docData.user, docData.text);
  });
};

export const sendMsg = async (text, owner, title) => {
  chatAPI.newChatMessage(text, owner, title);
};

class ChatMessage {
  constructor(timestamp, user, text) {
    this.timestamp = timestamp;
    this.user = user;
    this.text = text;
  }
}
