import baseUrl from './baseUrl';

export async function getProject(userId, projectId) {
  const fetchData = await baseUrl
    .get(`/projects/${userId}/${projectId}`, { headers: { Authorization: '' } })
    .then((response) => {
      return response;
    });
  return fetchData;
}

export async function createProject(project) {
  const sendData = await baseUrl
    .post('/projects/new', project, { headers: { Authorization: '' } })
    .then((response) => console.log(response));
  return sendData;
}

export async function updateProject(project) {
  const updateData = await baseUrl
    .put(`/projects/${project.owner}/${project.title}`, project, {
      headers: { Authorization: '' },
    })
    .then((response) => {
      console.log(response);
      return response;
    });
  return updateData;
}
