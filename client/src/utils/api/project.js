import baseUrl from './baseUrl';

export async function getProject(projectId) {
  const fetchData = await baseUrl
    .get(`/projects/${projectId}`, { headers: { userId: 'xtrmil' } })
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
