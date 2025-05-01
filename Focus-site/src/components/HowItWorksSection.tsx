import React from 'react';
import { motion } from 'framer-motion';
import { AlertCircle, Layers, Search, Shield } from 'lucide-react';
import { howItWorksSteps } from '../data/content';

const iconComponents = {
  search: Search,
  layers: Layers,
  shield: Shield,
  'alert-circle': AlertCircle,
};

const HowItWorksSection: React.FC = () => {
  return (
    <section id="how-it-works" className="section bg-white">
      <div className="container-custom">
        <div className="section-title">
          <motion.h2
            initial={{ opacity: 0, y: 20 }}
            whileInView={{ opacity: 1, y: 0 }}
            viewport={{ once: true, margin: "-100px" }}
            transition={{ duration: 0.6 }}
          >
            How It Works
          </motion.h2>
          <motion.p
            initial={{ opacity: 0, y: 20 }}
            whileInView={{ opacity: 1, y: 0 }}
            viewport={{ once: true, margin: "-100px" }}
            transition={{ duration: 0.6, delay: 0.1 }}
          >
            A simple yet powerful approach to digital wellness
          </motion.p>
        </div>

        <div className="relative">
          {/* Timeline line */}
          <div className="absolute left-4 md:left-1/2 top-0 bottom-0 w-px bg-gray-200 md:-ml-px hidden sm:block"></div>
          
          <div className="space-y-12">
            {howItWorksSteps.map((step, index) => {
              const IconComponent = iconComponents[step.icon as keyof typeof iconComponents];
              const isEven = index % 2 === 0;
              
              return (
                <div key={step.id} className="relative">
                  <motion.div
                    className={`flex flex-col sm:flex-row items-center ${
                      isEven ? 'md:flex-row-reverse' : ''
                    }`}
                    initial={{ opacity: 0, y: 20 }}
                    whileInView={{ opacity: 1, y: 0 }}
                    viewport={{ once: true, margin: "-100px" }}
                    transition={{ duration: 0.6, delay: index * 0.1 }}
                  >
                    {/* Timeline node - visible on mobile and as connector on desktop */}
                    <div className="sm:absolute sm:left-4 md:left-1/2 sm:transform sm:-translate-x-1/2 z-10">
                      <div className={`w-8 h-8 rounded-full border-4 border-white ${
                        isEven ? 'bg-secondary-500' : 'bg-primary-500'
                      } shadow-md flex items-center justify-center text-white`}>
                        <span className="text-xs font-bold">{index + 1}</span>
                      </div>
                    </div>
                    
                    {/* Content */}
                    <div className={`sm:w-1/2 ${
                      isEven ? 'md:pr-16 sm:pl-12' : 'md:pl-16 sm:pr-12'
                    } sm:pt-0 pt-4 pl-12 sm:pl-0`}>
                      <div className={`bg-white p-6 rounded-lg shadow-subtle ${
                        isEven ? 'border-l-4 border-secondary-500' : 'border-l-4 border-primary-500'
                      }`}>
                        <div className="flex items-start">
                          <div className={`w-10 h-10 rounded-lg mr-4 flex items-center justify-center ${
                            isEven ? 'bg-secondary-100 text-secondary-600' : 'bg-primary-100 text-primary-600'
                          }`}>
                            {IconComponent && <IconComponent size={20} />}
                          </div>
                          <div>
                            <h4 className="text-lg font-semibold mb-2">{step.title}</h4>
                            <p className="text-gray-600">{step.description}</p>
                          </div>
                        </div>
                      </div>
                    </div>
                  </motion.div>
                </div>
              );
            })}
          </div>
        </div>
      </div>
    </section>
  );
};

export default HowItWorksSection;