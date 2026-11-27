import { useRef, useState } from 'react';
import TextField from '../auth/TextField';
import SelectField from '../ui/SelectField';
import RoleBadge from '../ui/RoleBadge';
import useAsync from '../../hooks/useAsync';
import { useAuth } from '../../context/AuthContext';
import { addMember, listMembers } from '../../api/workspaceApi';
import { parseApiError } from '../../api/apiError';
import { ROLE_LABELS, initials } from '../../utils/format';
import { validateInvite } from '../../utils/workspaceValidators';
import { focusFirstInvalid } from '../../utils/validators';

const INVITE_ROLES = [
  { value: 'VIEWER', label: 'Viewer: can look' },
  { value: 'EDITOR', label: 'Editor: can change' },
];

export default function MembersPanel({ workspaceId, isOwner }) {
  const { user } = useAuth();
  const { data: members, error, loading, reload } = useAsync(() => listMembers(workspaceId), [workspaceId]);
  const formRef = useRef(null);
  const [values, setValues] = useState({ email: '', role: 'VIEWER' });
  const [errors, setErrors] = useState({});
  const [formError, setFormError] = useState('');
  const [notice, setNotice] = useState('');
  const [submitting, setSubmitting] = useState(false);

  function handleChange(event) {
    const { name, value } = event.target;
    setValues((current) => ({ ...current, [name]: value }));
    if (errors[name]) setErrors((current) => ({ ...current, [name]: undefined }));
  }

  async function handleSubmit(event) {
    event.preventDefault();
    if (submitting) return;
    setFormError('');
    setNotice('');

    const found = validateInvite(values);
    if (Object.keys(found).length > 0) {
      setErrors(found);
      focusFirstInvalid(formRef.current);
      return;
    }

    setErrors({});
    setSubmitting(true);
    try {
      const added = await addMember(workspaceId, { email: values.email.trim(), role: values.role });
      setValues((current) => ({ ...current, email: '' }));
      setNotice(`${added.fullName} was added as ${ROLE_LABELS[added.role].toLowerCase()}.`);
      reload();
    } catch (failure) {
      const parsed = parseApiError(failure);
      if (parsed.status === 404) {
        setErrors({ email: 'No account uses that email yet. Ask them to sign up first.' });
      } else if (parsed.status === 409) {
        setErrors({ email: 'That person is already a member of this workspace.' });
      } else {
        setErrors(parsed.fieldErrors);
        if (Object.keys(parsed.fieldErrors).length === 0) setFormError(parsed.message);
      }
      focusFirstInvalid(formRef.current);
    } finally {
      setSubmitting(false);
    }
  }

  return (
    <section className="panel" aria-labelledby="members-title">
      <div className="panel__head">
        <h2 id="members-title" className="panel__title">
          Members
          {members && <span className="panel__count">{members.length}</span>}
        </h2>
      </div>

      {loading && !members && (
        <p className="panel__note" role="status">
          Loading members…
        </p>
      )}

      {error && (
        <div className="panel__note" role="alert">
          <p>Couldn’t load the members. {error.message}</p>
          <button type="button" className="btn btn--secondary btn--sm btn--inline" onClick={reload}>
            Try again
          </button>
        </div>
      )}

      {members && (
        <ul className="members">
          {members.map((member) => (
            <li key={member.userId ?? member.id ?? member.email} className="member">
              <span className="avatar avatar--lg" aria-hidden="true">
                {initials(member.fullName)}
              </span>
              <div className="member__who">
                <span className="member__name">
                  {member.fullName}
                  {member.email === user?.email && <span className="member__you">(you)</span>}
                </span>
                <span className="member__email">{member.email}</span>
              </div>
              <RoleBadge role={member.role} />
            </li>
          ))}
        </ul>
      )}

      {isOwner ? (
        <form ref={formRef} className="invite" onSubmit={handleSubmit} noValidate>
          {formError && (
            <div className="form-alert invite__alert" role="alert">
              {formError}
            </div>
          )}
          <TextField
            label="Add a person by email"
            name="email"
            type="email"
            autoComplete="off"
            placeholder="name@company.com"
            value={values.email}
            onChange={handleChange}
            error={errors.email}
          />
          <SelectField label="Role" name="role" value={values.role} onChange={handleChange} options={INVITE_ROLES} />
          <div className="field">
            {/* Same structure as the other fields, so the button lines up with the inputs. */}
            <span className="field__label invite__spacer" aria-hidden="true">
              &nbsp;
            </span>
            <button type="submit" className="btn btn--primary btn--inline" disabled={submitting} aria-busy={submitting}>
              {submitting && <span className="btn__spinner" aria-hidden="true" />}
              Add member
            </button>
          </div>
          <p className="invite__notice" role="status">
            {notice}
          </p>
        </form>
      ) : (
        <p className="panel__note panel__note--foot">Only the workspace owner can add people.</p>
      )}
    </section>
  );
}