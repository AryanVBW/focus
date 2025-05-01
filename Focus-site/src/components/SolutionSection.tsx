import React from 'react';
import { motion } from 'framer-motion';
import { Activity, Zap } from 'lucide-react';
import { solutions } from '../data/content';

const iconComponents = {
  activity: Activity,
  zap: Zap,
};

const SolutionSection: React.FC = () => {
  return (
    <section id="solution" className="section bg-white">
      <div className="container-custom">
        <div className="section-title">
          <motion.h2
            initial={{ opacity: 0, y: 20 }}
            whileInView={{ opacity: 1, y: 0 }}
            viewport={{ once: true, margin: "-100px" }}
            transition={{ duration: 0.6 }}
          >
            Our Solution
          </motion.h2>
          <motion.p
            initial={{ opacity: 0, y: 20 }}
            whileInView={{ opacity: 1, y: 0 }}
            viewport={{ once: true, margin: "-100px" }}
            transition={{ duration: 0.6, delay: 0.1 }}
          >
            Focus combines monitoring and blocking to help you build better digital habits
          </motion.p>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-2 gap-8">
          {solutions.map((solution, index) => {
            const IconComponent = iconComponents[solution.icon as keyof typeof iconComponents];
            const isNormalMode = solution.mode === 'normal';
            
            return (
              <motion.div
                key={solution.id}
                className={`rounded-xl p-8 ${
                  isNormalMode 
                    ? 'bg-gradient-to-br from-blue-50 to-secondary-50 border border-secondary-100' 
                    : 'bg-gradient-to-br from-purple-50 to-primary-50 border border-primary-100'
                }`}
                initial={{ opacity: 0, y: 20 }}
                whileInView={{ opacity: 1, y: 0 }}
                viewport={{ once: true, margin: "-100px" }}
                transition={{ duration: 0.6, delay: index * 0.2 }}
              >
                <div className={`w-16 h-16 rounded-full flex items-center justify-center mb-6 ${
                  isNormalMode ? 'bg-secondary-100 text-secondary-600' : 'bg-primary-100 text-primary-600'
                }`}>
                  {IconComponent && <IconComponent size={32} />}
                </div>
                
                <h3 className={`text-2xl font-semibold mb-4 ${
                  isNormalMode ? 'text-secondary-700' : 'text-primary-700'
                }`}>
                  {solution.title}
                </h3>
                
                <p className="text-gray-700 mb-6">{solution.description}</p>
                
                <div className="space-y-3">
                  {isNormalMode ? (
                    <>
                      <div className="flex items-center gap-2">
                        <div className="w-5 h-5 rounded-full bg-secondary-200 flex items-center justify-center">
                          <div className="w-2 h-2 rounded-full bg-secondary-500"></div>
                        </div>
                        <span className="text-gray-700">Track app usage and patterns</span>
                      </div>
                      <div className="flex items-center gap-2">
                        <div className="w-5 h-5 rounded-full bg-secondary-200 flex items-center justify-center">
                          <div className="w-2 h-2 rounded-full bg-secondary-500"></div>
                        </div>
                        <span className="text-gray-700">Set daily time limits for apps</span>
                      </div>
                      <div className="flex items-center gap-2">
                        <div className="w-5 h-5 rounded-full bg-secondary-200 flex items-center justify-center">
                          <div className="w-2 h-2 rounded-full bg-secondary-500"></div>
                        </div>
                        <span className="text-gray-700">Get usage insights and recommendations</span>
                      </div>
                    </>
                  ) : (
                    <>
                      <div className="flex items-center gap-2">
                        <div className="w-5 h-5 rounded-full bg-primary-200 flex items-center justify-center">
                          <div className="w-2 h-2 rounded-full bg-primary-500"></div>
                        </div>
                        <span className="text-gray-700">Block distracting apps and websites</span>
                      </div>
                      <div className="flex items-center gap-2">
                        <div className="w-5 h-5 rounded-full bg-primary-200 flex items-center justify-center">
                          <div className="w-2 h-2 rounded-full bg-primary-500"></div>
                        </div>
                        <span className="text-gray-700">Filter inappropriate content</span>
                      </div>
                      <div className="flex items-center gap-2">
                        <div className="w-5 h-5 rounded-full bg-primary-200 flex items-center justify-center">
                          <div className="w-2 h-2 rounded-full bg-primary-500"></div>
                        </div>
                        <span className="text-gray-700">Silence non-essential notifications</span>
                      </div>
                    </>
                  )}
                </div>
              </motion.div>
            );
          })}
        </div>
      </div>
    </section>
  );
};

export default SolutionSection;