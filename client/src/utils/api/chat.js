import baseUrl from './baseUrl';
import * as Auth from '../Auth';

export const getDBPath = async (owner, title) => {
  return (
    await baseUrl.get(`/projects/${owner}/${title}/chat/`, {
      headers: { Authorization: await Auth.getToken() },
    })
  ).data.data;
};

export const newChatMessage = async (text, owner, title) => {
  return await baseUrl.post(
    `/projects/${owner}/${title}/chat`,
    { text },
    { headers: { Authorization: await Auth.getToken() } },
  );
};
