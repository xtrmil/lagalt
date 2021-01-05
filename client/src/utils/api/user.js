import baseUrl from './baseUrl';
import { getToken } from '../Auth';
export async function getUserByUserId() {
  const fetchData = await baseUrl
    .get('/profile', { headers: { Authorization: await getToken() } })
    .then((response) => {
      return response.data;
    });
  return fetchData;
}

export async function editUserProfile(profile) {
  const sendData = await baseUrl
    .put('/profile', profile, { headers: { Authorization: await getToken() } })
    .then((response) => {
      return response.data;
    });
  return sendData;
}
