export const mapOptions = (array, data) => {
  for (const [key, value] of Object.entries(data)) {
    const option = { value: key, label: value };
    array.push(option);
  }
  return array;
};
