// Mirrors the backend's WorkspaceRole order. The backend enforces every rule; this only decides
// which buttons to show so people are not offered actions that would be refused.
const RANK = { VIEWER: 0, EDITOR: 1, OWNER: 2 };

export const hasRole = (role, minimum) => (RANK[role] ?? -1) >= RANK[minimum];