import client from './client';

export const loginRequest = (email, password) =>
  client.post('/api/auth/login', { email, password }).then((res) => res.data);

export const registerRequest = ({ fullName, email, password }) =>
  client.post('/api/auth/register', { fullName, email, password }).then((res) => res.data);

export const fetchCurrentUser = () => client.get('/api/auth/me').then((res) => res.data);