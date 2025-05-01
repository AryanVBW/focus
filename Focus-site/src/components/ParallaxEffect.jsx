import { jsx as _jsx, jsxs as _jsxs } from "react/jsx-runtime";
import { useEffect, useState } from 'react';

const ParallaxEffect = () => {
  const [scrollY, setScrollY] = useState(0);
  
  useEffect(() => {
    const handleScroll = () => {
      setScrollY(window.scrollY);
    };
    
    // Add scroll event listener
    window.addEventListener('scroll', handleScroll, { passive: true });
    
    // Clean up
    return () => {
      window.removeEventListener('scroll', handleScroll);
    };
  }, []);
  
  // Calculate parallax transforms based on scroll position
  const calculateTranslate = (factor) => {
    return `translateY(${scrollY * factor}px)`;
  };
  
  return (
    <div className="fixed inset-0 -z-20 overflow-hidden pointer-events-none">
      {/* Slow-moving background elements */}
      <div 
        className="absolute top-20 left-20 w-64 h-64 rounded-full bg-primary-300/10 dark:bg-primary-800/5"
        style={{ transform: calculateTranslate(0.03) }}
      />
      <div 
        className="absolute top-[25%] right-[10%] w-80 h-80 rounded-full bg-secondary-200/10 dark:bg-secondary-900/5"
        style={{ transform: calculateTranslate(0.05) }}
      />
      
      {/* Medium-speed elements */}
      <div 
        className="absolute bottom-[30%] left-[5%] w-48 h-48 rounded-full bg-purple-300/15 dark:bg-purple-800/10"
        style={{ transform: calculateTranslate(0.08) }}
      />
      <div 
        className="absolute top-[60%] right-[20%] w-56 h-56 rounded-full bg-blue-300/10 dark:bg-blue-900/5"
        style={{ transform: calculateTranslate(0.07) }}
      />
      
      {/* Fast-moving elements */}
      <div 
        className="absolute bottom-[10%] right-[30%] w-32 h-32 rounded-full bg-indigo-300/20 dark:bg-indigo-800/10 blur-sm"
        style={{ transform: calculateTranslate(0.1) }}
      />
      <div 
        className="absolute top-[40%] left-[25%] w-24 h-24 rounded-full bg-violet-400/15 dark:bg-violet-800/10 blur-sm"
        style={{ transform: calculateTranslate(0.12) }}
      />
    </div>
  );
};

export default ParallaxEffect;
