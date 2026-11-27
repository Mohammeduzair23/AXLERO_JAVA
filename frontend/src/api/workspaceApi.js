import client from './client';

const BASE = '/api/workspaces';

export const listWorkspaces = () => client.get(BASE).then((res) => res.data);
export const createWorkspace = (name) => client.post(BASE, { name }).then((res) => res.data);
export const getWorkspace = (id) => client.get(`${BASE}/${id}`).then((res) => res.data);
export const renameWorkspace = (id, name) => client.put(`${BASE}/${id}`, { name }).then((res) => res.data);
export const deleteWorkspace = (id) => client.delete(`${BASE}/${id}`);

export const listMembers = (id) => client.get(`${BASE}/${id}/members`).then((res) => res.data);
export const addMember = (id, { email, role }) =>
  client.post(`${BASE}/${id}/members`, { email, role }).then((res) => res.data);