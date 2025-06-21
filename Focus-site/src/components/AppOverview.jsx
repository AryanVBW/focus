import React, { useEffect } from 'react';
import { motion } from 'framer-motion';
import { OptimizedImage, LoadingSkeleton } from './LoadingComponents';
import { useSEO, pageSEO, injectStructuredData } from '../utils/seo';

// Import mockup images
import firstOpenImg from '../assets/mockups/1st_when_open.png';
import trackReportImg from '../assets/mockups/alltrack_report_homepage.png';
import permissionImg from '../assets/mockups/permision.png';
import settingsImg from '../assets/mockups/settings.png';
import statisticsImg from '../assets/mockups/statics.png';

const AppOverview = () => {
  // SEO optimization for this page
  useSEO(pageSEO['app-overview']);
  
  useEffect(() => {
    injectStructuredData('app-overview');
  }, []);

  const features = [
    {
      id: 'first-open',
      title: 'Clean & Intuitive Interface',
      description: 'Focus greets you with a simple, distraction-free interface designed to help you concentrate on what matters most.',
      image: firstOpenImg,
      alt: 'Focus app main interface showing clean and intuitive design'
    },
    {
      id: 'tracking',
      title: 'Comprehensive Activity Tracking',
      description: 'Monitor which apps are consuming your time and get detailed breakdowns of your digital habits.',
      image: trackReportImg,
      alt: 'Focus app activity tracking dashboard with usage statistics'
    },
    {
      id: 'statistics',
      title: 'In-depth Usage Statistics',
      description: 'View colorful charts and meaningful statistics that help you understand your productivity patterns.',
      image: statisticsImg,
      alt: 'Focus app statistics page showing colorful charts and analytics'
    },
    {
      id: 'permissions',
      title: 'Transparent Permissions',
      description: 'Focus only asks for the permissions it needs to help you stay productive, with clear explanations for each request.',
      image: permissionImg,
      alt: 'Focus app permissions screen with clear explanations'
    },
    {
      id: 'settings',
      title: 'Customizable Settings',
      description: 'Tailor the app to your specific needs with flexible configuration options that adapt to your workflow.',
      image: settingsImg,
      alt: 'Focus app settings page with customization options'
    },
  ];

  return (
    <section id="app-overview" className="section bg-white dark:bg-slate-900 py-20">
      <div className="container-custom">
        <header className="section-title text-center mb-16">
          <motion.h1 
            className="text-3xl md:text-4xl font-bold mb-4 text-gray-900 dark:text-white"
            initial={{ opacity: 0, y: 20 }}
            whileInView={{ opacity: 1, y: 0 }}
            viewport={{ once: true }}
            transition={{ duration: 0.6 }}
          >
            App Overview
          </motion.h1>
          <motion.p 
            className="text-xl text-gray-600 dark:text-gray-300 max-w-3xl mx-auto"
            initial={{ opacity: 0, y: 20 }}
            whileInView={{ opacity: 1, y: 0 }}
            viewport={{ once: true }}
            transition={{ duration: 0.6, delay: 0.1 }}
          >
            Discover how Focus helps you regain control of your digital life with these powerful features
          </motion.p>
        </header>

        <div className="space-y-32">
          {features.map((feature, index) => (
            <motion.article 
              key={feature.id}
              className={`flex flex-col ${index % 2 !== 0 ? 'md:flex-row-reverse' : 'md:flex-row'} items-center gap-8 md:gap-16`}
              initial={{ opacity: 0, y: 50 }}
              whileInView={{ opacity: 1, y: 0 }}
              viewport={{ once: true, margin: "-100px" }}
              transition={{ duration: 0.8, delay: 0.2 }}
            >
              {/* Phone Mockup with Optimized Image */}
              <div className="w-full md:w-1/2 relative">
                <div className="phone-mockup relative mx-auto max-w-[280px]">
                  <OptimizedImage 
                    src={feature.image} 
                    alt={feature.alt}
                    className="w-full shadow-xl rounded-lg"
                    placeholder={
                      <LoadingSkeleton 
                        width="full" 
                        height="[500px]" 
                        className="shadow-xl rounded-lg" 
                      />
                    }
                    onLoad={() => {
                      // Announce to screen readers when image loads
                      const announcement = document.createElement('div');
                      announcement.setAttribute('aria-live', 'polite');
                      announcement.className = 'sr-only';
                      announcement.textContent = `${feature.title} screenshot loaded`;
                      document.body.appendChild(announcement);
                      setTimeout(() => document.body.removeChild(announcement), 1000);
                    }}
                  />
                  
                  {/* Subtle shadow effect */}
                  <div className="absolute -bottom-4 inset-x-0 h-4 bg-gradient-to-t from-transparent to-gray-200 dark:to-slate-800 opacity-30 blur-md z-0"></div>
                </div>
              </div>

              {/* Feature Text */}
              <div className="w-full md:w-1/2">
                <h2 className="text-2xl md:text-3xl font-bold mb-4 text-gray-900 dark:text-white">
                  {feature.title}
                </h2>
                <p className="text-lg text-gray-600 dark:text-gray-300 mb-6">
                  {feature.description}
                </p>
                <div className="w-16 h-1 bg-primary-600 rounded-full"></div>
              </div>
            </motion.article>
          ))}
        </div>

        <motion.div 
          className="text-center mt-32"
          initial={{ opacity: 0, y: 20 }}
          whileInView={{ opacity: 1, y: 0 }}
          viewport={{ once: true }}
          transition={{ duration: 0.6 }}
        >
          <h2 className="text-2xl md:text-3xl font-bold mb-6 text-gray-900 dark:text-white">
            Ready to Transform Your Digital Habits?
          </h2>
          <a 
            href="#download" 
            className="btn-primary inline-block px-8 py-4 rounded-full text-white font-medium transition-transform hover:scale-105 focus:outline-none focus:ring-2 focus:ring-primary-400"
            aria-label="Download Focus app to start transforming your digital habits"
          >
            Download Focus Today
          </a>
        </motion.div>
      </div>
    </section>
  );
};

export default AppOverview;
