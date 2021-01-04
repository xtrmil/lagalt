import axios from 'axios';

export const baseURLString = 'http://localhost:8080/api/v1';

export default axios.create({
  baseURL: baseURLString,
});
