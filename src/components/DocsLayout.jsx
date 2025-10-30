import { source } from '../lib/source';

export default function Layout({ children }) {
  return (
    <div className="min-h-screen bg-white dark:bg-gray-900">
      {/* Header */}
      <header className="border-b border-gray-200 dark:border-gray-700 bg-white dark:bg-gray-900">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="flex justify-between items-center h-16">
            <div className="flex items-center">
              <h1 className="text-xl font-semibold text-gray-900 dark:text-white">
                Focus Documentation
              </h1>
            </div>
            <div className="flex items-center space-x-4">
              <a
                href="#"
                onClick={(e) => {
                  e.preventDefault();
                  window.location.hash = '';
                }}
                className="text-sm text-gray-600 dark:text-gray-300 hover:text-gray-900 dark:hover:text-white"
              >
                Back to Main Site
              </a>
            </div>
          </div>
        </div>
      </header>

      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        <div className="flex gap-8">
          {/* Sidebar */}
          <aside className="w-64 flex-shrink-0">
            <nav className="space-y-2">
              {source.pageTree.map((page) => (
                <a
                  key={page.url}
                  href={`#documentation${page.url === '/index' ? '' : page.url}`}
                  className="block px-3 py-2 text-sm text-gray-700 dark:text-gray-300 hover:text-gray-900 dark:hover:text-white hover:bg-gray-100 dark:hover:bg-gray-800 rounded-md"
                >
                  {page.name}
                </a>
              ))}
            </nav>
          </aside>

          {/* Main content */}
          <main className="flex-1 max-w-none">
            <div className="prose prose-gray dark:prose-invert max-w-none">
              {children}
            </div>
          </main>
        </div>
      </div>
    </div>
  );
}
