import React from 'react';
import { motion } from 'framer-motion';
import { Brain, Clock, Moon, Users } from 'lucide-react';
import { problems } from '../data/content';

const iconComponents = {
  brain: Brain,
  clock: Clock,
  moon: Moon,
  users: Users,
};

const ProblemSection: React.FC = () => {
  return (
    <section id="problem" className="section bg-gray-50">
      <div className="container-custom">
        <div className="section-title">
          <motion.h2
            initial={{ opacity: 0, y: 20 }}
            whileInView={{ opacity: 1, y: 0 }}
            viewport={{ once: true, margin: "-100px" }}
            transition={{ duration: 0.6 }}
          >
            The Problem
          </motion.h2>
          <motion.p
            initial={{ opacity: 0, y: 20 }}
            whileInView={{ opacity: 1, y: 0 }}
            viewport={{ once: true, margin: "-100px" }}
            transition={{ duration: 0.6, delay: 0.1 }}
          >
            Digital devices are designed to capture and keep our attention
          </motion.p>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
          {problems.map((problem, index) => {
            const IconComponent = iconComponents[problem.icon as keyof typeof iconComponents];
            
            return (
              <motion.div
                key={problem.id}
                className="bg-white rounded-lg p-6 shadow-subtle hover:shadow-subtle-lg transition-shadow"
                initial={{ opacity: 0, y: 20 }}
                whileInView={{ opacity: 1, y: 0 }}
                viewport={{ once: true, margin: "-100px" }}
                transition={{ duration: 0.5, delay: index * 0.1 }}
                whileHover={{ y: -5 }}
              >
                <div className="w-12 h-12 bg-primary-100 rounded-full flex items-center justify-center text-primary-600 mb-4">
                  {IconComponent && <IconComponent size={24} />}
                </div>
                <h4 className="text-lg font-semibold mb-2">{problem.title}</h4>
                <p className="text-gray-600">{problem.description}</p>
              </motion.div>
            );
          })}
        </div>
        
        <motion.div 
          className="mt-12 text-center bg-gray-100 p-6 rounded-lg"
          initial={{ opacity: 0, y: 20 }}
          whileInView={{ opacity: 1, y: 0 }}
          viewport={{ once: true, margin: "-100px" }}
          transition={{ duration: 0.6, delay: 0.5 }}
        >
          <p className="text-gray-700 italic">
            "The average person checks their phone 96 times a day — once every 10 minutes."
          </p>
          <p className="text-gray-500 text-sm mt-2">— Digital wellness research, 2023</p>
        </motion.div>
      </div>
    </section>
  );
};

export default ProblemSection;