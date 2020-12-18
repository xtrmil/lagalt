import baseUrl from './baseUrl';

export async function getProject(userId, projectId) {
  const fetchData = await baseUrl
    .get(`/projects/${userId}/${projectId}`, { headers: { Authorization: '' } })
    .then((response) => {
      return response;
    });
  return fetchData;
}

export async function updateProject(project) {
  const updateData = await baseUrl
    .put(`/projects/${project.projectId}`, project)
    .then((response) => {
      console.log(response);
    });
}
