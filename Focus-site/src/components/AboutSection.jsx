import React from 'react';
import { motion } from 'framer-motion';
import { Brain } from 'lucide-react';

const AboutSection = () => {
  return (
    <section id="about" className="section bg-white">
      <div className="container-custom">
        <div className="section-title">
          <motion.h2
            initial={{ opacity: 0, y: 20 }}
            whileInView={{ opacity: 1, y: 0 }}
            viewport={{ once: true, margin: "-100px" }}
            transition={{ duration: 0.6 }}
          >
            What is Focus?
          </motion.h2>
          <motion.p
            initial={{ opacity: 0, y: 20 }}
            whileInView={{ opacity: 1, y: 0 }}
            viewport={{ once: true, margin: "-100px" }}
            transition={{ duration: 0.6, delay: 0.1 }}
          >
            A powerful tool to help you break free from digital distractions
          </motion.p>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-2 gap-12 items-center">
          <motion.div
            initial={{ opacity: 0, x: -30 }}
            whileInView={{ opacity: 1, x: 0 }}
            viewport={{ once: true, margin: "-100px" }}
            transition={{ duration: 0.6 }}
          >
            <div className="aspect-square max-w-md mx-auto relative">
              <div className="absolute inset-0 bg-gradient-to-br from-primary-100 to-primary-50 rounded-full"></div>
              <div className="absolute inset-0 flex items-center justify-center">
                <motion.div 
                  className="w-32 h-32 bg-primary-600 rounded-full flex items-center justify-center text-white"
                  animate={{ scale: [1, 1.05, 1] }}
                  transition={{ repeat: Infinity, duration: 4 }}
                >
                  <Brain size={64} />
                </motion.div>
              </div>
            </div>
          </motion.div>

          <motion.div
            initial={{ opacity: 0, x: 30 }}
            whileInView={{ opacity: 1, x: 0 }}
            viewport={{ once: true, margin: "-100px" }}
            transition={{ duration: 0.6, delay: 0.2 }}
          >
            <h3 className="text-2xl font-semibold mb-4 text-gray-800">
              Digital Wellness, Reimagined
            </h3>
            <p className="text-gray-600 mb-6">
              Focus is an Android app designed to help you regain control of your digital life. In a world of constant notifications and endless scrolling, Focus helps you establish healthier technology habits.
            </p>
            <p className="text-gray-600 mb-6">
              Our app intelligently blocks distracting content, filters out adult material, and gives you control over which apps can interrupt your focus time. It's not about completely disconnecting—it's about creating a healthier relationship with technology.
            </p>
            <div className="flex flex-col gap-4">
              <div className="flex items-center gap-3">
                <span className="w-2 h-2 bg-primary-500 rounded-full"></span>
                <p className="text-gray-700">Monitor and understand your digital habits</p>
              </div>
              <div className="flex items-center gap-3">
                <span className="w-2 h-2 bg-primary-500 rounded-full"></span>
                <p className="text-gray-700">Block distractions during important work</p>
              </div>
              <div className="flex items-center gap-3">
                <span className="w-2 h-2 bg-primary-500 rounded-full"></span>
                <p className="text-gray-700">Create healthier digital boundaries</p>
              </div>
            </div>
          </motion.div>
        </div>
      </div>
    </section>
  );
};

export default AboutSection;