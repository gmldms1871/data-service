// Placeholder for utils.js
export const getError = (error) => {
  return error.response && error.response.data && error.response.data.message
    ? error.response.data.message
    : error.message
}
