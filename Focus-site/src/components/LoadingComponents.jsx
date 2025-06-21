import React, { Suspense } from 'react';
import { motion } from 'framer-motion';

/**
 * Loading spinner component
 */
const LoadingSpinner = ({ size = 'md', color = 'primary' }) => {
  const sizeClasses = {
    sm: 'w-4 h-4',
    md: 'w-8 h-8',
    lg: 'w-12 h-12',
    xl: 'w-16 h-16'
  };

  const colorClasses = {
    primary: 'border-primary-600',
    white: 'border-white',
    gray: 'border-gray-600'
  };

  return (
    <div 
      className={`${sizeClasses[size]} border-2 ${colorClasses[color]} border-t-transparent rounded-full animate-spin`}
      role="status"
      aria-label="Loading"
    />
  );
};

/**
 * Loading skeleton component
 */
export const LoadingSkeleton = ({ width = 'full', height = '4', className = '' }) => {
  return (
    <div 
      className={`bg-gray-200 dark:bg-gray-700 animate-pulse rounded w-${width} h-${height} ${className}`}
      role="status"
      aria-label="Loading content"
    />
  );
};

/**
 * Error boundary component
 */
class ErrorBoundary extends React.Component {
  constructor(props) {
    super(props);
    this.state = { hasError: false, error: null };
  }

  static getDerivedStateFromError(error) {
    return { hasError: true, error };
  }

  componentDidCatch(error, errorInfo) {
    console.error('Error caught by boundary:', error, errorInfo);
  }

  render() {
    if (this.state.hasError) {
      return (
        <div className="flex flex-col items-center justify-center min-h-[200px] p-8 text-center">
          <div className="text-red-500 text-6xl mb-4">⚠️</div>
          <h2 className="text-xl font-semibold text-gray-900 dark:text-gray-100 mb-2">
            Something went wrong
          </h2>
          <p className="text-gray-600 dark:text-gray-400 mb-4">
            We're sorry, but something unexpected happened. Please refresh the page or try again later.
          </p>
          <button
            onClick={() => window.location.reload()}
            className="button-primary px-6 py-2"
          >
            Refresh Page
          </button>
        </div>
      );
    }

    return this.props.children;
  }
}

/**
 * Lazy loading wrapper with suspense
 */
export const LazySection = ({ 
  children, 
  fallback = <LoadingSpinner size="lg" />,
  minHeight = '200px' 
}) => {
  return (
    <ErrorBoundary>
      <Suspense 
        fallback={
          <div 
            className="flex items-center justify-center"
            style={{ minHeight }}
          >
            {fallback}
          </div>
        }
      >
        {children}
      </Suspense>
    </ErrorBoundary>
  );
};

/**
 * Image component with lazy loading and performance optimization
 */
export const OptimizedImage = ({ 
  src, 
  alt, 
  className = '', 
  placeholder,
  onLoad,
  onError,
  ...props 
}) => {
  const [imageLoaded, setImageLoaded] = React.useState(false);
  const [imageError, setImageError] = React.useState(false);
  
  const handleLoad = () => {
    setImageLoaded(true);
    onLoad && onLoad();
  };
  
  const handleError = () => {
    setImageError(true);
    onError && onError();
  };

  if (imageError) {
    return (
      <div className={`bg-gray-200 dark:bg-gray-700 flex items-center justify-center ${className}`}>
        <span className="text-gray-400 text-sm">Failed to load image</span>
      </div>
    );
  }

  return (
    <div className="relative">
      {!imageLoaded && placeholder && (
        <div className={`absolute inset-0 ${className}`}>
          {placeholder}
        </div>
      )}
      <img
        src={src}
        alt={alt}
        className={`transition-opacity duration-300 ${
          imageLoaded ? 'opacity-100' : 'opacity-0'
        } ${className}`}
        onLoad={handleLoad}
        onError={handleError}
        loading="lazy"
        {...props}
      />
    </div>
  );
};

/**
 * Animated loading state component
 */
export const LoadingState = ({ message = 'Loading...', className = '' }) => {
  return (
    <motion.div 
      className={`flex flex-col items-center justify-center py-12 ${className}`}
      initial={{ opacity: 0 }}
      animate={{ opacity: 1 }}
      exit={{ opacity: 0 }}
    >
      <LoadingSpinner size="lg" />
      <p className="text-gray-600 dark:text-gray-400 mt-4">{message}</p>
    </motion.div>
  );
};

export default LoadingSpinner;
export { ErrorBoundary };
