import axios from 'axios';

const api = axios.create({
  baseURL: 'http://localhost:8080', 
});

api.interceptors.response.use(
  response => response,
  error => {
    const message = error.response?.data?.error || "Erro inesperado no servidor";
    console.error("Erro na API:", message);
    return Promise.reject(message);
  }
);

export default api;