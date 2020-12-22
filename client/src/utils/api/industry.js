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

export async function getAllTags() {
  const fetchData = await baseUrl.get('/available/tags/').then((response) => {
    return response.data;
  });
  return fetchData;
}
