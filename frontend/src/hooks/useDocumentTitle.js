import { useEffect } from 'react';

export default function useDocumentTitle(title) {
  useEffect(() => {
    document.title = title ? `${title} – Axlero Dashboards` : 'Axlero Dashboards';
  }, [title]);
}