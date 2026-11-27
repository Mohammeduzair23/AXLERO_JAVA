import { useState } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import StateMessage from '../components/ui/StateMessage';
import RoleBadge from '../components/ui/RoleBadge';
import WorkspaceNameModal from '../components/workspaces/WorkspaceNameModal';
import DeleteWorkspaceModal from '../components/workspaces/DeleteWorkspaceModal';
import MembersPanel from '../components/workspaces/MembersPanel';
import useAsync from '../hooks/useAsync';
import useDocumentTitle from '../hooks/useDocumentTitle';
import { deleteWorkspace, getWorkspace, renameWorkspace } from '../api/workspaceApi';
import { formatDate } from '../utils/format';
import { hasRole } from '../utils/permissions';

export default function WorkspacePage() {
  const { workspaceId } = useParams();
  // `key` makes React start with a clean state when you move from one workspace to another.
  return <WorkspaceView key={workspaceId} workspaceId={workspaceId} />;
}

function WorkspaceView({ workspaceId }) {
  const navigate = useNavigate();
  const { data: workspace, error, loading, reload } = useAsync(() => getWorkspace(workspaceId), [workspaceId]);
  const [dialog, setDialog] = useState(null); // 'rename' | 'delete' | null
  useDocumentTitle(workspace?.name ?? 'Workspace');

  if (loading && !workspace) {
    return (
      <p className="loading-line" role="status">
        Loading workspace…
      </p>
    );
  }

  if (error) {
    const notFound = error.status === 404;
    return (
      <StateMessage
        tone="error"
        title={notFound ? 'Workspace not found' : 'Couldn’t load this workspace'}
        text={notFound ? 'It may have been deleted, or you may not be a member of it.' : error.message}
        actionLabel={notFound ? undefined : 'Try again'}
        onAction={reload}
      >
        <Link to="/" className="btn btn--secondary btn--inline">
          Back to workspaces
        </Link>
      </StateMessage>
    );
  }

  const isOwner = hasRole(workspace.role, 'OWNER');

  async function handleRename(name) {
    await renameWorkspace(workspaceId, name);
    setDialog(null);
    reload();
  }

  async function handleDelete() {
    await deleteWorkspace(workspaceId);
    navigate('/', { replace: true });
  }

  return (
    <>
      <nav className="crumbs" aria-label="Breadcrumb">
        <Link to="/">Workspaces</Link>
        <span className="crumbs__sep" aria-hidden="true">
          /
        </span>
        <span aria-current="page">{workspace.name}</span>
      </nav>

      <header className="page-head">
        <div>
          <div className="page-title-row">
            <h1 className="page-title">{workspace.name}</h1>
            <RoleBadge role={workspace.role} />
          </div>
          <p className="page-sub">Created {formatDate(workspace.createdAt)}</p>
        </div>
        {isOwner && (
          <div className="page-actions">
            <button type="button" className="btn btn--secondary btn--sm btn--inline" onClick={() => setDialog('rename')}>
              Rename
            </button>
            <button type="button" className="btn btn--outline-danger btn--sm btn--inline" onClick={() => setDialog('delete')}>
              Delete workspace
            </button>
          </div>
        )}
      </header>

      <MembersPanel workspaceId={workspaceId} isOwner={isOwner} />

      <WorkspaceNameModal
        open={dialog === 'rename'}
        onClose={() => setDialog(null)}
        title="Rename workspace"
        submitLabel="Save name"
        initialName={workspace.name}
        onSubmit={handleRename}
      />
      <DeleteWorkspaceModal
        open={dialog === 'delete'}
        onClose={() => setDialog(null)}
        workspaceName={workspace.name}
        onConfirm={handleDelete}
      />
    </>
  );
}