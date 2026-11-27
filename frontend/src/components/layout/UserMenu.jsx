import { useEffect, useRef, useState } from 'react';
import { useAuth } from '../../context/AuthContext';
import { initials } from '../../utils/format';

export default function UserMenu() {
  const { user, logout } = useAuth();
  const [open, setOpen] = useState(false);
  const rootRef = useRef(null);
  const triggerRef = useRef(null);

  useEffect(() => {
    if (!open) return undefined;
    const handlePointerDown = (event) => {
      if (!rootRef.current?.contains(event.target)) setOpen(false);
    };
    const handleKeyDown = (event) => {
      if (event.key === 'Escape') {
        setOpen(false);
        triggerRef.current?.focus();
      }
    };
    document.addEventListener('pointerdown', handlePointerDown);
    document.addEventListener('keydown', handleKeyDown);
    return () => {
      document.removeEventListener('pointerdown', handlePointerDown);
      document.removeEventListener('keydown', handleKeyDown);
    };
  }, [open]);

  if (!user) return null;

  return (
    <div className="user-menu" ref={rootRef}>
      <button
        ref={triggerRef}
        type="button"
        className="user-menu__trigger"
        aria-expanded={open}
        aria-controls="user-menu-panel"
        onClick={() => setOpen((value) => !value)}
      >
        <span className="avatar" aria-hidden="true">
          {initials(user.fullName)}
        </span>
        <span className="user-menu__name">{user.fullName}</span>
      </button>
      {open && (
        <div id="user-menu-panel" className="user-menu__panel">
          <p className="user-menu__who">{user.fullName}</p>
          <p className="user-menu__email">{user.email}</p>
          <button type="button" className="user-menu__item" onClick={logout}>
            Sign out
          </button>
        </div>
      )}
    </div>
  );
}