import baseUrl from './baseUrl';

export async function getUserByUserId(userId) {
  const fetchData = await baseUrl
    .get('/profile', { headers: { Authorization: userId } })
    .then((response) => {
      return response.data;
    });
  return fetchData;
}
