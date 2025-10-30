import { useState, useEffect } from 'react';
import DocsLayout from './DocsLayout';
import DocsPage from './DocsPage';
import { source } from '../lib/source';

export default function DocumentationRouter() {
  const [currentSlug, setCurrentSlug] = useState([]);

  useEffect(() => {
    // Parse the current URL to get the documentation slug
    const parseSlug = () => {
      const hash = window.location.hash.replace('#', '');
      if (hash.startsWith('documentation')) {
        const parts = hash.split('/').slice(1); // Remove 'documentation' part
        setCurrentSlug(parts.length > 0 ? parts : []);
      }
    };

    parseSlug();

    // Listen for hash changes
    const handleHashChange = () => {
      parseSlug();
    };

    window.addEventListener('hashchange', handleHashChange);
    return () => window.removeEventListener('hashchange', handleHashChange);
  }, []);

  // If no specific page is requested, show the index page
  const slug = currentSlug.length > 0 ? currentSlug : ['index'];

  return (
    <DocsLayout>
      <DocsPage slug={slug} />
    </DocsLayout>
  );
}
