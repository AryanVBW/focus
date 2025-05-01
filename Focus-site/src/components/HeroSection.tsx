import React from 'react';
import { motion } from 'framer-motion';
import { GithubIcon } from 'lucide-react';
import googlePlayIcon from '../assets/google-play-icon.svg';

const HeroSection: React.FC = () => {
  return (
    <section id="home" className="relative min-h-screen flex items-center">
      {/* Background gradient */}
      <div className="absolute inset-0 bg-gradient-to-b from-primary-900 to-secondary-800 z-0"></div>
      
      {/* Animated patterns */}
      <div className="absolute inset-0 overflow-hidden z-0">
        <div className="absolute inset-0 opacity-10">
          <svg className="w-full h-full" xmlns="http://www.w3.org/2000/svg">
            <defs>
              <pattern id="pattern-circles" x="0" y="0" width="50" height="50" patternUnits="userSpaceOnUse" patternContentUnits="userSpaceOnUse">
                <circle id="pattern-circle" cx="10" cy="10" r="2" fill="#ffffff"></circle>
              </pattern>
            </defs>
            <rect id="rect" x="0" y="0" width="100%" height="100%" fill="url(#pattern-circles)"></rect>
          </svg>
        </div>
      </div>

      <div className="container-custom relative z-10 pt-20 pb-24 md:pt-0">
        <div className="grid grid-cols-1 md:grid-cols-2 gap-12 items-center">
          <motion.div
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ duration: 0.8 }}
            className="text-center md:text-left"
          >
            <h1 className="text-white font-bold mb-6">
              Regain control of
              <span className="block text-primary-300">your digital life.</span>
            </h1>
            <p className="text-gray-200 text-lg mb-8 max-w-lg mx-auto md:mx-0">
              Focus is the smart content blocker that helps you stay productive, 
              reduce screen time, and build healthier digital habits.
            </p>

            <div className="flex flex-col sm:flex-row gap-4 justify-center md:justify-start">
              <motion.a
                href="#download"
                className="button-primary bg-white text-primary-600 hover:bg-gray-100"
                whileHover={{ scale: 1.05 }}
                whileTap={{ scale: 0.95 }}
              >
                <img src={googlePlayIcon} alt="Google Play" className="mr-2 h-6 w-6" />
                Download on Play Store
              </motion.a>
              <motion.a
                href="https://github.com/AryanVBW/focus/releases"
                target="_blank"
                rel="noopener noreferrer"
                className="button-secondary bg-transparent text-white border-white hover:bg-white/10"
                whileHover={{ scale: 1.05 }}
                whileTap={{ scale: 0.95 }}
              >
                <GithubIcon className="mr-2 h-5 w-5" />
                Get Latest on GitHub
              </motion.a>
            </div>
          </motion.div>

          <motion.div
            className="hidden md:block"
            initial={{ opacity: 0, scale: 0.8 }}
            animate={{ opacity: 1, scale: 1 }}
            transition={{ duration: 0.8, delay: 0.2 }}
          >
            <div className="relative mx-auto max-w-sm">
              {/* Phone mockup */}
              <div className="relative rounded-[40px] border-8 border-gray-800 overflow-hidden shadow-2xl bg-gray-800 aspect-[9/16] w-full animate-float">
                {/* App screenshot placeholder */}
                <div className="absolute inset-0 bg-primary-700">
                  <div className="absolute top-0 left-0 right-0 h-24 bg-primary-800 flex items-end justify-center pb-4">
                    <div className="w-32 h-6 bg-primary-700 rounded-lg"></div>
                    <div className="absolute right-4 bottom-4 w-10 h-10 bg-primary-600 rounded-full flex items-center justify-center">
                      <div className="w-5 h-5 bg-white rounded-full"></div>
                    </div>
                  </div>
                  <div className="absolute top-24 inset-x-0 bottom-0 p-4 grid grid-cols-2 gap-4">
                    {[...Array(6)].map((_, i) => (
                      <div key={i} className="bg-primary-600/50 rounded-lg p-4 flex items-center justify-center">
                        <div className="w-10 h-10 bg-white/20 rounded-lg"></div>
                      </div>
                    ))}
                  </div>
                </div>
              </div>
              
              {/* Decorative elements */}
              <div className="absolute -bottom-6 -left-6 w-24 h-24 bg-accent-500 rounded-full opacity-70 blur-xl"></div>
              <div className="absolute -top-10 -right-10 w-32 h-32 bg-primary-300 rounded-full opacity-70 blur-xl"></div>
            </div>
          </motion.div>
        </div>
      </div>
    </section>
  );
};

export default HeroSection;