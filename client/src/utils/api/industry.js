import baseUrl from './baseUrl';

export async function getAllIndustries() {
  const fetchIndustries = await baseUrl.get('/available/industries').then((response) => {
    return response.data;
  });
  return fetchIndustries;
}

export async function getTagsByIndustry(industry) {
  const fetchTagsByIndustry = await baseUrl.get('/available/tags/' + industry).then((response) => {
    return response.data;
  });
  return fetchTagsByIndustry;
}
