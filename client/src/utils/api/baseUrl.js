import axios from 'axios';

export let baseURLString;

if (process.env.NODE_ENV === 'production') {
  baseURLString = 'https://lagalt-2020-backend.herokuapp.com/api/v1';
} else {
  baseURLString = 'http://localhost:8080/api/v1';
}

export default axios.create({
  baseURL: baseURLString,
});
