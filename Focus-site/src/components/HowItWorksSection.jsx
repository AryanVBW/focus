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

const HowItWorksSection = () => {
    return (
        <section id="how-it-works" className="section bg-white">
            <div className="container-custom">
                {/* Section Title */}
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

                {/* Steps Container */}
                <div className="relative max-w-4xl mx-auto">
                    {/* Desktop Timeline Line */}
                    <div className="absolute left-1/2 top-0 bottom-0 w-px bg-gradient-to-b from-primary-200 via-secondary-200 to-primary-200 transform -translate-x-1/2 hidden md:block" />
                    
                    {/* Mobile Timeline Line */}
                    <div className="absolute left-6 top-16 bottom-0 w-px bg-gradient-to-b from-primary-200 via-secondary-200 to-primary-200 md:hidden" />

                    {/* Steps */}
                    <div className="space-y-8 md:space-y-16">
                        {howItWorksSteps.map((step, index) => {
                            const IconComponent = iconComponents[step.icon];
                            const isEven = index % 2 === 0;
                            const stepNumber = index + 1;

                            return (
                                <motion.div
                                    key={step.id}
                                    className="relative"
                                    initial={{ opacity: 0, y: 30 }}
                                    whileInView={{ opacity: 1, y: 0 }}
                                    viewport={{ once: true, margin: "-50px" }}
                                    transition={{ duration: 0.6, delay: index * 0.15 }}
                                >
                                    {/* Desktop Layout */}
                                    <div className={`hidden md:flex items-center ${isEven ? 'flex-row-reverse' : ''}`}>
                                        {/* Content Card */}
                                        <div className="w-5/12">
                                            <div className={`glass-card backdrop-blur-sm border-l-4 ${
                                                isEven ? 'border-secondary-500' : 'border-primary-500'
                                            } hover:shadow-xl transition-all duration-300 group`}>
                                                <div className="flex items-start">
                                                    <div className={`w-12 h-12 rounded-xl mr-4 flex items-center justify-center ${
                                                        isEven 
                                                            ? 'bg-secondary-100/80 dark:bg-secondary-900/50 text-secondary-600 dark:text-secondary-400 group-hover:bg-secondary-200/80' 
                                                            : 'bg-primary-100/80 dark:bg-primary-900/50 text-primary-600 dark:text-primary-400 group-hover:bg-primary-200/80'
                                                    } transition-colors duration-300`}>
                                                        {IconComponent && <IconComponent size={22} />}
                                                    </div>
                                                    <div className="flex-1">
                                                        <h4 className="text-xl font-semibold mb-3 text-gray-900 dark:text-gray-100">
                                                            {step.title}
                                                        </h4>
                                                        <p className="text-gray-600 dark:text-gray-300 leading-relaxed">
                                                            {step.description}
                                                        </p>
                                                    </div>
                                                </div>
                                            </div>
                                        </div>

                                        {/* Center Timeline Dot */}
                                        <div className="flex-1 flex justify-center">
                                            <div className="relative">
                                                <div className={`w-12 h-12 rounded-full border-4 border-white ${
                                                    isEven ? 'bg-secondary-500' : 'bg-primary-500'
                                                } shadow-lg flex items-center justify-center text-white font-bold text-lg z-10 relative`}>
                                                    {stepNumber}
                                                </div>
                                                {/* Pulse animation */}
                                                <div className={`absolute inset-0 w-12 h-12 rounded-full ${
                                                    isEven ? 'bg-secondary-400' : 'bg-primary-400'
                                                } opacity-30 animate-ping`} />
                                            </div>
                                        </div>

                                        {/* Spacer */}
                                        <div className="w-5/12" />
                                    </div>

                                    {/* Mobile Layout */}
                                    <div className="md:hidden flex items-start">
                                        {/* Timeline Dot */}
                                        <div className="relative mr-6 mt-2">
                                            <div className={`w-10 h-10 rounded-full border-3 border-white ${
                                                isEven ? 'bg-secondary-500' : 'bg-primary-500'
                                            } shadow-lg flex items-center justify-center text-white font-bold text-sm z-10 relative`}>
                                                {stepNumber}
                                            </div>
                                            {/* Pulse animation for mobile */}
                                            <div className={`absolute inset-0 w-10 h-10 rounded-full ${
                                                isEven ? 'bg-secondary-400' : 'bg-primary-400'
                                            } opacity-30 animate-ping`} />
                                        </div>

                                        {/* Content Card */}
                                        <div className="flex-1">
                                            <div className={`glass-card backdrop-blur-sm border-l-4 ${
                                                isEven ? 'border-secondary-500' : 'border-primary-500'
                                            } hover:shadow-xl transition-all duration-300 group`}>
                                                <div className="flex items-start">
                                                    <div className={`w-10 h-10 rounded-lg mr-3 flex items-center justify-center ${
                                                        isEven 
                                                            ? 'bg-secondary-100/80 dark:bg-secondary-900/50 text-secondary-600 dark:text-secondary-400 group-hover:bg-secondary-200/80' 
                                                            : 'bg-primary-100/80 dark:bg-primary-900/50 text-primary-600 dark:text-primary-400 group-hover:bg-primary-200/80'
                                                    } transition-colors duration-300`}>
                                                        {IconComponent && <IconComponent size={18} />}
                                                    </div>
                                                    <div className="flex-1">
                                                        <h4 className="text-lg font-semibold mb-2 text-gray-900 dark:text-gray-100">
                                                            {step.title}
                                                        </h4>
                                                        <p className="text-gray-600 dark:text-gray-300 text-sm leading-relaxed">
                                                            {step.description}
                                                        </p>
                                                    </div>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                </motion.div>
                            );
                        })}
                    </div>
                </div>
            </div>
        </section>
    );
};

export default HowItWorksSection;
