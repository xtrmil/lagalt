import baseUrl from './baseUrl';

export async function getProject(projectId) {
  const fetchData = await baseUrl
    .get(`/projects/${projectId}`, { headers: { userId: 'xtrmil' } })
    .then((response) => {
      return response;
    });
  return fetchData;
}
