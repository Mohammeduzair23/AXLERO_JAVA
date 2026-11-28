import client from './client';

const base = (workspaceId) => `/api/workspaces/${workspaceId}/dashboards`;

export const listDashboards = (workspaceId) => client.get(base(workspaceId)).then((res) => res.data);
export const createDashboard = (workspaceId, values) =>
  client.post(base(workspaceId), values).then((res) => res.data);
export const getDashboard = (workspaceId, id) => client.get(`${base(workspaceId)}/${id}`).then((res) => res.data);
export const updateDashboard = (workspaceId, id, values) =>
  client.put(`${base(workspaceId)}/${id}`, values).then((res) => res.data);
export const deleteDashboard = (workspaceId, id) => client.delete(`${base(workspaceId)}/${id}`);

export const listWidgets = (workspaceId, dashboardId) =>
  client.get(`${base(workspaceId)}/${dashboardId}/widgets`).then((res) => res.data);