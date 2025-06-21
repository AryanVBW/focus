import { useEffect, useRef, useState } from 'react';

/**
 * Custom hook for efficient intersection observer
 * Reduces unnecessary re-renders and improves performance
 */
export const useIntersectionObserver = (options = {}) => {
  const [isIntersecting, setIsIntersecting] = useState(false);
  const [hasIntersected, setHasIntersected] = useState(false);
  const targetRef = useRef(null);

  useEffect(() => {
    const observer = new IntersectionObserver(
      ([entry]) => {
        setIsIntersecting(entry.isIntersecting);
        if (entry.isIntersecting && !hasIntersected) {
          setHasIntersected(true);
        }
      },
      {
        threshold: 0.1,
        rootMargin: '-50px',
        ...options,
      }
    );

    const currentTarget = targetRef.current;
    if (currentTarget) {
      observer.observe(currentTarget);
    }

    return () => {
      if (currentTarget) {
        observer.unobserve(currentTarget);
      }
    };
  }, [hasIntersected, options]);

  return { targetRef, isIntersecting, hasIntersected };
};

/**
 * Hook for lazy loading images with performance optimization
 */
export const useLazyImage = (src, placeholder = '') => {
  const [imageSrc, setImageSrc] = useState(placeholder);
  const [imageLoaded, setImageLoaded] = useState(false);
  const { targetRef, hasIntersected } = useIntersectionObserver();

  useEffect(() => {
    if (hasIntersected && src) {
      const img = new Image();
      img.onload = () => {
        setImageSrc(src);
        setImageLoaded(true);
      };
      img.src = src;
    }
  }, [hasIntersected, src]);

  return { targetRef, imageSrc, imageLoaded };
};
