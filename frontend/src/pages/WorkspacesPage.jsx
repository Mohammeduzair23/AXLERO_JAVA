import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import StateMessage from '../components/ui/StateMessage';
import RoleBadge from '../components/ui/RoleBadge';
import WorkspaceNameModal from '../components/workspaces/WorkspaceNameModal';
import useAsync from '../hooks/useAsync';
import useDocumentTitle from '../hooks/useDocumentTitle';
import { createWorkspace, listWorkspaces } from '../api/workspaceApi';
import { formatDate } from '../utils/format';

export default function WorkspacesPage() {
  useDocumentTitle('Workspaces');
  const navigate = useNavigate();
  const { data: workspaces, error, loading, reload } = useAsync(listWorkspaces, []);
  const [creating, setCreating] = useState(false);

  async function handleCreate(name) {
    const workspace = await createWorkspace(name);
    navigate(`/workspaces/${workspace.id}`);
  }

  const isEmpty = workspaces?.length === 0;

  return (
    <>
      <header className="page-head">
        <div>
          <h1 className="page-title">Workspaces</h1>
          <p className="page-sub">Each workspace keeps its dashboards and sales data private to its members.</p>
        </div>
        {workspaces?.length > 0 && (
          <div className="page-actions">
            <button type="button" className="btn btn--primary btn--inline" onClick={() => setCreating(true)}>
              New workspace
            </button>
          </div>
        )}
      </header>

      {loading && !workspaces && (
        <ul className="ws-grid" aria-label="Loading workspaces" role="status">
          {[0, 1, 2].map((index) => (
            <li key={index} className="ws-card ws-card--skeleton" aria-hidden="true" />
          ))}
        </ul>
      )}

      {error && (
        <StateMessage
          tone="error"
          title="Couldn’t load your workspaces"
          text={error.message}
          actionLabel="Try again"
          onAction={reload}
        />
      )}

      {isEmpty && (
        <StateMessage
          title="No workspaces yet"
          text="Create a workspace to start building dashboards, or ask a teammate to add you to theirs."
          actionLabel="Create a workspace"
          onAction={() => setCreating(true)}
        />
      )}

      {workspaces?.length > 0 && (
        <ul className="ws-grid">
          {workspaces.map((workspace) => (
            <li key={workspace.id}>
              <Link to={`/workspaces/${workspace.id}`} className="ws-card">
                <span className="ws-card__name">{workspace.name}</span>
                <span className="ws-card__foot">
                  <RoleBadge role={workspace.role} />
                  <span>Created {formatDate(workspace.createdAt)}</span>
                </span>
              </Link>
            </li>
          ))}
        </ul>
      )}

      <WorkspaceNameModal
        open={creating}
        onClose={() => setCreating(false)}
        title="New workspace"
        submitLabel="Create workspace"
        onSubmit={handleCreate}
      />
    </>
  );
}