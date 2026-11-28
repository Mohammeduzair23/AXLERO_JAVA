import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import StateMessage from '../ui/StateMessage';
import DashboardFormModal from './DashboardFormModal';
import useAsync from '../../hooks/useAsync';
import { createDashboard, listDashboards } from '../../api/dashboardApi';
import { formatDate } from '../../utils/format';

/** The dashboards of one workspace. Only editors and owners get the "New dashboard" button. */
export default function DashboardsPanel({ workspaceId, canEdit }) {
  const navigate = useNavigate();
  const { data: dashboards, error, loading, reload } = useAsync(() => listDashboards(workspaceId), [workspaceId]);
  const [creating, setCreating] = useState(false);

  async function handleCreate(values) {
    const dashboard = await createDashboard(workspaceId, values);
    navigate(`/workspaces/${workspaceId}/dashboards/${dashboard.id}`);
  }

  const isEmpty = dashboards?.length === 0;

  return (
    <section aria-labelledby="dashboards-title">
      <div className="section-head">
        <h2 id="dashboards-title" className="section-title">
          Dashboards
          {dashboards && <span className="panel__count">{dashboards.length}</span>}
        </h2>
        {canEdit && dashboards?.length > 0 && (
          <button type="button" className="btn btn--primary btn--sm btn--inline" onClick={() => setCreating(true)}>
            New dashboard
          </button>
        )}
      </div>

      {loading && !dashboards && (
        <ul className="ws-grid" aria-label="Loading dashboards" role="status">
          {[0, 1].map((index) => (
            <li key={index} className="ws-card ws-card--skeleton" aria-hidden="true" />
          ))}
        </ul>
      )}

      {error && (
        <StateMessage
          tone="error"
          title="Couldn’t load the dashboards"
          text={error.message}
          actionLabel="Try again"
          onAction={reload}
        />
      )}

      {isEmpty && (
        <StateMessage
          title="No dashboards yet"
          text={
            canEdit
              ? 'A dashboard groups the charts and numbers your team looks at together.'
              : 'An editor or the owner of this workspace can create one.'
          }
          actionLabel={canEdit ? 'Create a dashboard' : undefined}
          onAction={() => setCreating(true)}
        />
      )}

      {dashboards?.length > 0 && (
        <ul className="ws-grid">
          {dashboards.map((dashboard) => (
            <li key={dashboard.id}>
              <Link to={`/workspaces/${workspaceId}/dashboards/${dashboard.id}`} className="ws-card">
                <span className="ws-card__top">
                  <span className="ws-card__name">{dashboard.name}</span>
                  {dashboard.description && <span className="ws-card__desc">{dashboard.description}</span>}
                </span>
                <span className="ws-card__foot">
                  <span>Updated {formatDate(dashboard.updatedAt ?? dashboard.createdAt)}</span>
                </span>
              </Link>
            </li>
          ))}
        </ul>
      )}

      <DashboardFormModal
        open={creating}
        onClose={() => setCreating(false)}
        title="New dashboard"
        submitLabel="Create dashboard"
        onSubmit={handleCreate}
      />
    </section>
  );
}