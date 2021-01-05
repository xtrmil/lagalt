import { getToken } from '../Auth';
import baseUrl from './baseUrl';

export async function getProject(userId, projectId) {
  const fetchData = await baseUrl
    .get(`/projects/${userId}/${projectId}`, { headers: { Authorization: await getToken() } })
    .then((response) => {
      return response;
    });
  return fetchData;
}

export async function createProject(project) {
  const sendData = await baseUrl
    .post('/projects/new', project, { headers: { Authorization: await getToken() } })
    .then((response) => console.log(response));
  return sendData;
}

export async function updateProject(project, owner, title) {
  const updateData = await baseUrl
    .put(`/projects/${owner}/${title}`, project, {
      headers: { Authorization: await getToken() },
    })
    .then((response) => {
      console.log(response);
      return response;
    });
  return updateData;
}
