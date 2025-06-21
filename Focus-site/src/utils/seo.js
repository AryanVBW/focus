import { useEffect } from 'react';

/**
 * SEO and meta tag management utilities
 */

// Default meta tags
const defaultMeta = {
  title: 'Focus - Regain control of your digital life',
  description: 'Focus is an Android app that helps you break free from digital distractions with smart content blocking, app management, and digital wellness tools.',
  keywords: 'focus app, digital wellness, content blocking, app management, productivity, android app, digital detox, screen time control',
  author: 'Focus Team',
  viewport: 'width=device-width, initial-scale=1.0',
  robots: 'index, follow',
  language: 'en-US',
  image: '/og-image.jpg',
  url: 'https://focus-app-site.vercel.app',
  type: 'website',
  siteName: 'Focus App'
};

/**
 * Hook for managing SEO meta tags
 */
export const useSEO = (meta = {}) => {
  useEffect(() => {
    const metaData = { ...defaultMeta, ...meta };
    
    // Update document title
    document.title = metaData.title;
    
    // Function to update or create meta tag
    const updateMetaTag = (name, content, property = false) => {
      if (!content) return;
      
      const attribute = property ? 'property' : 'name';
      let tag = document.querySelector(`meta[${attribute}="${name}"]`);
      
      if (!tag) {
        tag = document.createElement('meta');
        tag.setAttribute(attribute, name);
        document.head.appendChild(tag);
      }
      
      tag.setAttribute('content', content);
    };
    
    // Update basic meta tags
    updateMetaTag('description', metaData.description);
    updateMetaTag('keywords', metaData.keywords);
    updateMetaTag('author', metaData.author);
    updateMetaTag('viewport', metaData.viewport);
    updateMetaTag('robots', metaData.robots);
    updateMetaTag('language', metaData.language);
    
    // Update Open Graph tags
    updateMetaTag('og:title', metaData.title, true);
    updateMetaTag('og:description', metaData.description, true);
    updateMetaTag('og:image', metaData.image, true);
    updateMetaTag('og:url', metaData.url, true);
    updateMetaTag('og:type', metaData.type, true);
    updateMetaTag('og:site_name', metaData.siteName, true);
    
    // Update Twitter Card tags
    updateMetaTag('twitter:card', 'summary_large_image');
    updateMetaTag('twitter:title', metaData.title);
    updateMetaTag('twitter:description', metaData.description);
    updateMetaTag('twitter:image', metaData.image);
    
    // Update canonical URL
    let canonicalLink = document.querySelector('link[rel="canonical"]');
    if (!canonicalLink) {
      canonicalLink = document.createElement('link');
      canonicalLink.rel = 'canonical';
      document.head.appendChild(canonicalLink);
    }
    canonicalLink.href = metaData.url;
    
  }, [meta]);
};

/**
 * Page-specific SEO data
 */
export const pageSEO = {
  home: {
    title: 'Focus - Regain control of your digital life',
    description: 'Focus is an Android app that helps you break free from digital distractions with smart content blocking, app management, and digital wellness tools.',
    url: 'https://focus-app-site.vercel.app',
    keywords: 'focus app, digital wellness, content blocking, app management, productivity, android app, digital detox'
  },
  
  'privacy-policy': {
    title: 'Privacy Policy - Focus App',
    description: 'Learn about Focus App\'s privacy practices, data collection policies, and how we protect your personal information.',
    url: 'https://focus-app-site.vercel.app/privacy-policy',
    keywords: 'privacy policy, data protection, focus app privacy, personal information'
  },
  
  'terms-of-service': {
    title: 'Terms of Service - Focus App',
    description: 'Read the terms and conditions for using Focus App, including user responsibilities and service limitations.',
    url: 'https://focus-app-site.vercel.app/terms-of-service',
    keywords: 'terms of service, user agreement, focus app terms, service conditions'
  },
  
  'app-overview': {
    title: 'App Overview - Focus Features and Screenshots',
    description: 'Explore Focus App\'s features with detailed screenshots and learn how the app helps you stay focused and productive.',
    url: 'https://focus-app-site.vercel.app/app-overview',
    keywords: 'focus app features, app screenshots, digital wellness features, productivity tools'
  }
};

/**
 * Structured data for search engines
 */
export const generateStructuredData = (page = 'home') => {
  const baseStructuredData = {
    '@context': 'https://schema.org',
    '@type': 'SoftwareApplication',
    name: 'Focus',
    applicationCategory: 'ProductivityApplication',
    operatingSystem: 'Android',
    description: 'A digital wellness app that helps you regain control of your digital life through smart content blocking and app management.',
    author: {
      '@type': 'Organization',
      name: 'Focus Team'
    },
    offers: {
      '@type': 'Offer',
      price: '0',
      priceCurrency: 'USD'
    },
    aggregateRating: {
      '@type': 'AggregateRating',
      ratingValue: '4.8',
      ratingCount: '127'
    },
    downloadUrl: 'https://github.com/AryanVBW/focus/releases/download/V1/Focus_V.0.1.0.apk',
    screenshot: [
      'https://focus-app-site.vercel.app/assets/mockups/1st_when_open.png',
      'https://focus-app-site.vercel.app/assets/mockups/settings.png',
      'https://focus-app-site.vercel.app/assets/mockups/statics.png'
    ]
  };

  const pageSpecificData = {
    'privacy-policy': {
      '@type': 'WebPage',
      name: 'Privacy Policy - Focus App',
      description: 'Privacy policy for Focus App'
    },
    'terms-of-service': {
      '@type': 'WebPage',
      name: 'Terms of Service - Focus App',
      description: 'Terms of service for Focus App'
    }
  };

  return page === 'home' ? baseStructuredData : { ...baseStructuredData, ...pageSpecificData[page] };
};

/**
 * Function to inject structured data
 */
export const injectStructuredData = (page = 'home') => {
  // Remove existing structured data
  const existingScript = document.querySelector('script[type="application/ld+json"]');
  if (existingScript) {
    existingScript.remove();
  }

  // Add new structured data
  const script = document.createElement('script');
  script.type = 'application/ld+json';
  script.textContent = JSON.stringify(generateStructuredData(page));
  document.head.appendChild(script);
};
