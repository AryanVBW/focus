import React from 'react';
import { motion } from 'framer-motion';

// Import mockup images
import firstOpenImg from '../assets/mockups/1st_when_open.png';
import trackReportImg from '../assets/mockups/alltrack_report_homepage.png';
import permissionImg from '../assets/mockups/permision.png';
import settingsImg from '../assets/mockups/settings.png';
import statisticsImg from '../assets/mockups/statics.png';

const AppOverview = () => {
  const features = [
    {
      id: 'first-open',
      title: 'Clean & Intuitive Interface',
      description: 'Focus greets you with a simple, distraction-free interface designed to help you concentrate on what matters most.',
      image: firstOpenImg,
    },
    {
      id: 'tracking',
      title: 'Comprehensive Activity Tracking',
      description: 'Monitor which apps are consuming your time and get detailed breakdowns of your digital habits.',
      image: trackReportImg,
    },
    {
      id: 'statistics',
      title: 'In-depth Usage Statistics',
      description: 'View colorful charts and meaningful statistics that help you understand your productivity patterns.',
      image: statisticsImg,
    },
    {
      id: 'permissions',
      title: 'Transparent Permissions',
      description: 'Focus only asks for the permissions it needs to help you stay productive, with clear explanations for each request.',
      image: permissionImg,
    },
    {
      id: 'settings',
      title: 'Customizable Settings',
      description: 'Tailor the app to your specific needs with flexible configuration options that adapt to your workflow.',
      image: settingsImg,
    },
  ];

  return (
    <section id="app-overview" className="section bg-white dark:bg-slate-900 py-20">
      <div className="container-custom">
        <div className="section-title text-center mb-16">
          <motion.h2 
            className="text-3xl md:text-4xl font-bold mb-4 text-gray-900 dark:text-white"
            initial={{ opacity: 0, y: 20 }}
            whileInView={{ opacity: 1, y: 0 }}
            viewport={{ once: true }}
            transition={{ duration: 0.6 }}
          >
            App Overview
          </motion.h2>
          <motion.p 
            className="text-xl text-gray-600 dark:text-gray-300 max-w-3xl mx-auto"
            initial={{ opacity: 0, y: 20 }}
            whileInView={{ opacity: 1, y: 0 }}
            viewport={{ once: true }}
            transition={{ duration: 0.6, delay: 0.1 }}
          >
            Discover how Focus helps you regain control of your digital life with these powerful features
          </motion.p>
        </div>

        <div className="space-y-32">
          {features.map((feature, index) => (
            <motion.div 
              key={feature.id}
              className={`flex flex-col ${index % 2 !== 0 ? 'md:flex-row-reverse' : 'md:flex-row'} items-center gap-8 md:gap-16`}
              initial={{ opacity: 0, y: 50 }}
              whileInView={{ opacity: 1, y: 0 }}
              viewport={{ once: true, margin: "-100px" }}
              transition={{ duration: 0.8, delay: 0.2 }}
            >
              {/* Phone Mockup */}
              <div className="w-full md:w-1/2 relative">
                <div className="phone-mockup relative mx-auto max-w-[280px]">
                  {/* Screen content - directly showing the mockup image which already has a phone frame */}
                  <img 
                    src={feature.image} 
                    alt={feature.title} 
                    className="w-full shadow-xl rounded-lg"
                  />
                  
                  {/* Subtle shadow effect */}
                  <div className="absolute -bottom-4 inset-x-0 h-4 bg-gradient-to-t from-transparent to-gray-200 dark:to-slate-800 opacity-30 blur-md z-0"></div>
                </div>
              </div>

              {/* Feature Text */}
              <div className="w-full md:w-1/2">
                <h3 className="text-2xl md:text-3xl font-bold mb-4 text-gray-900 dark:text-white">
                  {feature.title}
                </h3>
                <p className="text-lg text-gray-600 dark:text-gray-300 mb-6">
                  {feature.description}
                </p>
                <div className="w-16 h-1 bg-primary-600 rounded-full"></div>
              </div>
            </motion.div>
          ))}
        </div>

        <motion.div 
          className="text-center mt-32"
          initial={{ opacity: 0, y: 20 }}
          whileInView={{ opacity: 1, y: 0 }}
          viewport={{ once: true }}
          transition={{ duration: 0.6 }}
        >
          <h3 className="text-2xl md:text-3xl font-bold mb-6 text-gray-900 dark:text-white">
            Ready to Transform Your Digital Habits?
          </h3>
          <a 
            href="#download" 
            className="btn-primary inline-block px-8 py-4 rounded-full text-white font-medium transition-transform hover:scale-105"
          >
            Download Focus Today
          </a>
        </motion.div>
      </div>
    </section>
  );
};

export default AppOverview;
