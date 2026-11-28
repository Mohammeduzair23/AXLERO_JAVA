import { useState } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import StateMessage from '../components/ui/StateMessage';
import ConfirmDeleteModal from '../components/ui/ConfirmDeleteModal';
import DashboardFormModal from '../components/dashboards/DashboardFormModal';
import useAsync from '../hooks/useAsync';
import useDocumentTitle from '../hooks/useDocumentTitle';
import { getWorkspace } from '../api/workspaceApi';
import { deleteDashboard, getDashboard, listWidgets, updateDashboard } from '../api/dashboardApi';
import { formatDate } from '../utils/format';
import { CHART_LABELS, describeWidget } from '../utils/labels';
import { hasRole } from '../utils/permissions';

export default function DashboardPage() {
  const { workspaceId, dashboardId } = useParams();
  // `key` starts a clean state when you move between dashboards.
  return <DashboardView key={`${workspaceId}/${dashboardId}`} workspaceId={workspaceId} dashboardId={dashboardId} />;
}

function DashboardView({ workspaceId, dashboardId }) {
  const navigate = useNavigate();
  const workspace = useAsync(() => getWorkspace(workspaceId), [workspaceId]);
  const dashboard = useAsync(() => getDashboard(workspaceId, dashboardId), [workspaceId, dashboardId]);
  const widgets = useAsync(() => listWidgets(workspaceId, dashboardId), [workspaceId, dashboardId]);
  const [dialog, setDialog] = useState(null); // 'edit' | 'delete' | null
  useDocumentTitle(dashboard.data?.name ?? 'Dashboard');

  const failure = workspace.error ?? dashboard.error;
  if (failure) {
    const notFound = failure.status === 404;
    return (
      <StateMessage
        tone="error"
        title={notFound ? 'Dashboard not found' : 'Couldn’t load this dashboard'}
        text={notFound ? 'It may have been deleted, or you may not have access to it.' : failure.message}
        actionLabel={notFound ? undefined : 'Try again'}
        onAction={() => {
          workspace.reload();
          dashboard.reload();
        }}
      >
        <Link to={notFound ? '/' : `/workspaces/${workspaceId}`} className="btn btn--secondary btn--inline">
          {notFound ? 'Back to workspaces' : 'Back to the workspace'}
        </Link>
      </StateMessage>
    );
  }

  if (!workspace.data || !dashboard.data) {
    return (
      <p className="loading-line" role="status">
        Loading dashboard…
      </p>
    );
  }

  const canEdit = hasRole(workspace.data.role, 'EDITOR');
  const current = dashboard.data;

  async function handleEdit(values) {
    await updateDashboard(workspaceId, dashboardId, values);
    setDialog(null);
    dashboard.reload();
  }

  async function handleDelete() {
    await deleteDashboard(workspaceId, dashboardId);
    navigate(`/workspaces/${workspaceId}`, { replace: true });
  }

  return (
    <>
      <nav className="crumbs" aria-label="Breadcrumb">
        <Link to="/">Workspaces</Link>
        <span className="crumbs__sep" aria-hidden="true">
          /
        </span>
        <Link to={`/workspaces/${workspaceId}`}>{workspace.data.name}</Link>
        <span className="crumbs__sep" aria-hidden="true">
          /
        </span>
        <span aria-current="page">{current.name}</span>
      </nav>

      <header className="page-head">
        <div>
          <h1 className="page-title">{current.name}</h1>
          <p className="page-sub">
            {current.description ? `${current.description} ` : ''}
            <span className="page-meta">Updated {formatDate(current.updatedAt ?? current.createdAt)}.</span>
          </p>
        </div>
        {canEdit && (
          <div className="page-actions">
            <button type="button" className="btn btn--secondary btn--sm btn--inline" onClick={() => setDialog('edit')}>
              Rename
            </button>
            <button
              type="button"
              className="btn btn--outline-danger btn--sm btn--inline"
              onClick={() => setDialog('delete')}
            >
              Delete dashboard
            </button>
          </div>
        )}
      </header>

      <section className="panel" aria-labelledby="widgets-title">
        <div className="panel__head">
          <h2 id="widgets-title" className="panel__title">
            Widgets
            {widgets.data && <span className="panel__count">{widgets.data.length}</span>}
          </h2>
        </div>

        {widgets.loading && !widgets.data && (
          <p className="panel__note" role="status">
            Loading widgets…
          </p>
        )}

        {widgets.error && (
          <div className="panel__note" role="alert">
            <p>Couldn’t load the widgets. {widgets.error.message}</p>
            <button type="button" className="btn btn--secondary btn--sm btn--inline" onClick={widgets.reload}>
              Try again
            </button>
          </div>
        )}

        {widgets.data?.length === 0 && (
          <p className="panel__note">No widgets yet. Widgets show a chart or a number from your sales data.</p>
        )}

        {widgets.data?.length > 0 && (
          <ul className="widget-rows">
            {widgets.data.map((widget) => (
              <li key={widget.id} className="widget-row">
                <div className="widget-row__who">
                  <span className="widget-row__title">{widget.title}</span>
                  <span className="widget-row__meta">{describeWidget(widget)}</span>
                </div>
                <span className="tag">{CHART_LABELS[widget.chartType] ?? widget.chartType}</span>
              </li>
            ))}
          </ul>
        )}
      </section>

      <DashboardFormModal
        open={dialog === 'edit'}
        onClose={() => setDialog(null)}
        title="Rename dashboard"
        submitLabel="Save changes"
        initialValues={{ name: current.name, description: current.description ?? '' }}
        onSubmit={handleEdit}
      />
      <ConfirmDeleteModal
        open={dialog === 'delete'}
        onClose={() => setDialog(null)}
        title="Delete this dashboard?"
        text={`“${current.name}” and all of its widgets will be removed permanently. This can’t be undone.`}
        confirmLabel="Delete dashboard"
        onConfirm={handleDelete}
      />
    </>
  );
}