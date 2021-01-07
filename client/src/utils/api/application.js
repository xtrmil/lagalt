import { getToken } from '../Auth';
import baseUrl from './baseUrl';

export async function createApplication(owner, title, motivation, userId) {
  const sendData = await baseUrl
    .post(
      `/projects/${owner}/${title}/applications`,
      { motivation },
      { headers: { Authorization: await getToken() } },
    )
    .then((response) => {
      return response.data;
    });
  return sendData;
}
