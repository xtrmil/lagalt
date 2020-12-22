import baseUrl from './baseUrl';

export async function getUserByUserId(userId) {
  const fetchData = await baseUrl
    .get('/profile', { headers: { Authorization: userId } })
    .then((response) => {
      return response.data;
    });
  return fetchData;
}

export async function editUserProfile(profile, userId) {
  const sendData = await baseUrl
    .put('/profile', profile, { headers: { Authorization: userId } })
    .then((response) => {
      return response.data;
    });
  return sendData;
}
