import React from 'react';
import { motion } from 'framer-motion';
import { BarChart2, Bell, Eye, Layers } from 'lucide-react';
import { permissions } from '../data/content';

const iconComponents = {
  eye: Eye,
  'bar-chart-2': BarChart2,
  bell: Bell,
  layers: Layers,
};

const PrivacySection: React.FC = () => {
  return (
    <section id="privacy" className="section bg-gray-50">
      <div className="container-custom">
        <div className="section-title">
          <motion.h2
            initial={{ opacity: 0, y: 20 }}
            whileInView={{ opacity: 1, y: 0 }}
            viewport={{ once: true, margin: "-100px" }}
            transition={{ duration: 0.6 }}
          >
            Privacy & Permissions
          </motion.h2>
          <motion.p
            initial={{ opacity: 0, y: 20 }}
            whileInView={{ opacity: 1, y: 0 }}
            viewport={{ once: true, margin: "-100px" }}
            transition={{ duration: 0.6, delay: 0.1 }}
          >
            We value your privacy and only request necessary permissions
          </motion.p>
        </div>

        <motion.div
          className="bg-white rounded-xl p-8 mb-12 shadow-subtle"
          initial={{ opacity: 0, y: 20 }}
          whileInView={{ opacity: 1, y: 0 }}
          viewport={{ once: true, margin: "-100px" }}
          transition={{ duration: 0.6 }}
        >
          <div className="flex flex-col md:flex-row items-center mb-8">
            <div className="w-16 h-16 rounded-full bg-primary-100 flex items-center justify-center text-primary-600 mb-4 md:mb-0 md:mr-6">
              <svg xmlns="http://www.w3.org/2000/svg" className="h-8 w-8" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 15v2m-6 4h12a2 2 0 002-2v-6a2 2 0 00-2-2H6a2 2 0 00-2 2v6a2 2 0 002 2zm10-10V7a4 4 0 00-8 0v4h8z" />
              </svg>
            </div>
            <div>
              <h3 className="text-xl md:text-2xl font-semibold mb-2 text-center md:text-left">Our Privacy Commitment</h3>
              <p className="text-gray-600 text-center md:text-left">
                Focus is built with your privacy in mind. We only request the permissions needed to provide our core functionality. 
                All content monitoring happens directly on your device - we never see, store, or transmit your personal data.
              </p>
            </div>
          </div>
          
          <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
            {permissions.map((permission, index) => {
              const IconComponent = iconComponents[permission.icon as keyof typeof iconComponents];
              
              return (
                <motion.div
                  key={permission.id}
                  className="flex"
                  initial={{ opacity: 0, y: 20 }}
                  whileInView={{ opacity: 1, y: 0 }}
                  viewport={{ once: true, margin: "-100px" }}
                  transition={{ duration: 0.5, delay: 0.2 + index * 0.1 }}
                >
                  <div className="w-12 h-12 rounded-lg bg-gray-100 flex items-center justify-center text-gray-700 mr-4 flex-shrink-0">
                    {IconComponent && <IconComponent size={20} />}
                  </div>
                  <div>
                    <h4 className="font-medium mb-1">{permission.title}</h4>
                    <p className="text-sm text-gray-600">{permission.description}</p>
                  </div>
                </motion.div>
              );
            })}
          </div>
        </motion.div>
        
        <div className="bg-gray-100 rounded-lg p-6 text-center">
          <p className="text-gray-700 text-sm">
            By using Focus, you agree to our <a href="#" className="text-primary-600 hover:underline">Privacy Policy</a> and <a href="#" className="text-primary-600 hover:underline">Terms of Service</a>.
          </p>
        </div>
      </div>
    </section>
  );
};

export default PrivacySection;