import React, { useState } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import { BellOff, EyeOff, Shield, Smartphone } from 'lucide-react';
import { features } from '../data/content';

const iconComponents = {
  shield: Shield,
  'eye-off': EyeOff,
  smartphone: Smartphone,
  'bell-off': BellOff,
};

const FeaturesSection: React.FC = () => {
  const [activeFeature, setActiveFeature] = useState<string | null>(null);

  const toggleFeature = (id: string) => {
    if (activeFeature === id) {
      setActiveFeature(null);
    } else {
      setActiveFeature(id);
    }
  };

  return (
    <section id="features" className="section bg-gray-50">
      <div className="container-custom">
        <div className="section-title">
          <motion.h2
            initial={{ opacity: 0, y: 20 }}
            whileInView={{ opacity: 1, y: 0 }}
            viewport={{ once: true, margin: "-100px" }}
            transition={{ duration: 0.6 }}
          >
            Key Features
          </motion.h2>
          <motion.p
            initial={{ opacity: 0, y: 20 }}
            whileInView={{ opacity: 1, y: 0 }}
            viewport={{ once: true, margin: "-100px" }}
            transition={{ duration: 0.6, delay: 0.1 }}
          >
            Powerful tools to help you focus and stay productive
          </motion.p>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
          {features.map((feature, index) => {
            const IconComponent = iconComponents[feature.icon as keyof typeof iconComponents];
            const isActive = activeFeature === feature.id;
            
            return (
              <motion.div
                key={feature.id}
                className={`bg-white rounded-xl overflow-hidden shadow-subtle ${
                  isActive ? 'ring-2 ring-primary-500' : 'hover:shadow-subtle-lg'
                } transition-all cursor-pointer`}
                initial={{ opacity: 0, y: 20 }}
                whileInView={{ opacity: 1, y: 0 }}
                viewport={{ once: true, margin: "-100px" }}
                transition={{ duration: 0.5, delay: index * 0.1 }}
                onClick={() => toggleFeature(feature.id)}
              >
                <div className="p-6">
                  <div className={`w-14 h-14 ${feature.color || 'bg-primary-100 text-primary-600'} rounded-full flex items-center justify-center mb-4`}>
                    {IconComponent && <IconComponent size={28} />}
                  </div>
                  
                  <h4 className="text-xl font-semibold mb-2">{feature.title}</h4>
                  <p className="text-gray-600 mb-4">{feature.description}</p>
                  
                  <button 
                    className={`text-sm font-medium ${
                      isActive ? 'text-primary-700' : 'text-primary-600'
                    } flex items-center`}
                  >
                    {isActive ? 'Show less' : 'Learn more'}
                    <svg 
                      className={`ml-1 w-4 h-4 transition-transform ${isActive ? 'rotate-180' : ''}`} 
                      xmlns="http://www.w3.org/2000/svg" 
                      viewBox="0 0 20 20" 
                      fill="currentColor"
                    >
                      <path fillRule="evenodd" d="M5.293 7.293a1 1 0 011.414 0L10 10.586l3.293-3.293a1 1 0 111.414 1.414l-4 4a1 1 0 01-1.414 0l-4-4a1 1 0 010-1.414z" clipRule="evenodd" />
                    </svg>
                  </button>
                </div>
                
                <AnimatePresence>
                  {isActive && (
                    <motion.div
                      initial={{ height: 0, opacity: 0 }}
                      animate={{ height: 'auto', opacity: 1 }}
                      exit={{ height: 0, opacity: 0 }}
                      transition={{ duration: 0.3 }}
                      className="px-6 pb-6 bg-gray-50"
                    >
                      <div className="pt-4 border-t border-gray-100">
                        <h5 className="font-medium mb-2">How it works:</h5>
                        <ul className="space-y-2 text-gray-600">
                          {feature.id === 'content-blocking' && (
                            <>
                              <li className="flex items-start">
                                <span className="text-primary-600 mr-2">•</span>
                                <span>Analyzes web content in real-time</span>
                              </li>
                              <li className="flex items-start">
                                <span className="text-primary-600 mr-2">•</span>
                                <span>Recognizes distracting content patterns</span>
                              </li>
                              <li className="flex items-start">
                                <span className="text-primary-600 mr-2">•</span>
                                <span>Blocks or redirects based on your preferences</span>
                              </li>
                            </>
                          )}
                          
                          {feature.id === 'adult-filter' && (
                            <>
                              <li className="flex items-start">
                                <span className="text-primary-600 mr-2">•</span>
                                <span>Uses advanced content recognition technology</span>
                              </li>
                              <li className="flex items-start">
                                <span className="text-primary-600 mr-2">•</span>
                                <span>Blocks explicit images and text</span>
                              </li>
                              <li className="flex items-start">
                                <span className="text-primary-600 mr-2">•</span>
                                <span>Custom filtering levels available</span>
                              </li>
                            </>
                          )}
                          
                          {feature.id === 'app-management' && (
                            <>
                              <li className="flex items-start">
                                <span className="text-primary-600 mr-2">•</span>
                                <span>Categorizes apps by productivity level</span>
                              </li>
                              <li className="flex items-start">
                                <span className="text-primary-600 mr-2">•</span>
                                <span>Set allowed apps during focus time</span>
                              </li>
                              <li className="flex items-start">
                                <span className="text-primary-600 mr-2">•</span>
                                <span>Create schedules for different activities</span>
                              </li>
                            </>
                          )}
                          
                          {feature.id === 'notification-control' && (
                            <>
                              <li className="flex items-start">
                                <span className="text-primary-600 mr-2">•</span>
                                <span>Configure priority notifications</span>
                              </li>
                              <li className="flex items-start">
                                <span className="text-primary-600 mr-2">•</span>
                                <span>Batch non-urgent alerts for later</span>
                              </li>
                              <li className="flex items-start">
                                <span className="text-primary-600 mr-2">•</span>
                                <span>Silent hours scheduling</span>
                              </li>
                            </>
                          )}
                        </ul>
                      </div>
                    </motion.div>
                  )}
                </AnimatePresence>
              </motion.div>
            );
          })}
        </div>
      </div>
    </section>
  );
};

export default FeaturesSection;