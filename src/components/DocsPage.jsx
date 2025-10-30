import { source } from '../lib/source';

export default function Page({ slug }) {
  const page = source.getPage(slug);

  if (!page) {
    return (
      <div className="bg-white dark:bg-gray-900 rounded-lg shadow-sm border border-gray-200 dark:border-gray-700 p-6">
        <h1 className="text-2xl font-bold text-gray-900 dark:text-white mb-4">Page Not Found</h1>
        <p className="text-gray-600 dark:text-gray-300">The requested documentation page could not be found.</p>
      </div>
    );
  }

  return (
    <div className="bg-white dark:bg-gray-900 rounded-lg shadow-sm border border-gray-200 dark:border-gray-700 p-6">
      <h1 className="text-3xl font-bold text-gray-900 dark:text-white mb-6">{page.data.title}</h1>
      <div
        className="prose prose-gray dark:prose-invert max-w-none"
        dangerouslySetInnerHTML={{ __html: page.data.content }}
      />
    </div>
  );
}
