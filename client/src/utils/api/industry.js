import baseUrl from './baseUrl';

export async function getAllIndustries() {
  const fetchIndustries = await baseUrl.get('/available/industry').then((response) => {
    return response.data;
  });
  return fetchIndustries;
}

export async function getTagsByIndustry(industry) {
  const fetchTagsByIndustry = await baseUrl.get('/available/tag/' + industry).then((response) => {
    return response.data;
  });
  return fetchTagsByIndustry;
}

export async function getAllTags() {
  const fetchData = await baseUrl.get('/available/tag/').then((response) => {
    return response.data;
  });
  return fetchData;
}
