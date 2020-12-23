import baseUrl from './baseUrl';

export async function createApplication(projectId, motivation, userId) {
  const sendData = await baseUrl
    .post(
      `/projects/${projectId}/application`,
      { motivation },
      { headers: { Authorization: userId } },
    )
    .then((response) => {
      return response.data;
    });
  return sendData;
}
