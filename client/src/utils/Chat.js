import firebase from 'firebase/app';
import 'firebase/firestore';
import { Observable } from 'rxjs';

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

// TODO endpoint för att få kollektion/path för project chat
// /api/v1/project/:owner/:projectName/chat/default
const dbPath = 'projects/m8RlRYZGMl7ckpgCd5Q9/chat/default/default/';

const getProjectChat = () => db.collection(dbPath);

let length = 0;

export const chatData = () => {
  return new Observable((observer) => {
    getProjectChat()
      .orderBy('timestamp')
      .onSnapshot((collection) => {
        length = collection.docs.length;
        let arr = [];

        arr = collection.docChanges().map((change) => ({
          id: change.doc.id,
          timestamp: change.doc.data().timestamp,
          user: change.doc.data().user,
          text: change.doc.data().text,
        }));
        observer.next(arr);
      });
  });
};

export const sendMsg = (text) => {
  // TODO server-side endpoint istället?
  getProjectChat()
    .doc(length + '')
    .set({ text, user: 'some_user', timestamp: Date.now() });
};

// export const getInitial = async () => {
//   const collection = await db.collection(dbPath).get();
//   return collection.docs.map((doc) => doc.data());
// };
