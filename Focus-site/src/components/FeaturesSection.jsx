import { useState } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import { 
    BellOff, 
    EyeOff, 
    Shield, 
    Smartphone, 
    ChevronDown,
    CheckCircle,
    ArrowRight,
    Zap
} from 'lucide-react';
import { features } from '../data/content';
import { useMediaQuery } from '../hooks/usePerformance';
import { useAnnouncement } from '../utils/accessibility';

const iconComponents = {
    shield: Shield,
    'eye-off': EyeOff,
    smartphone: Smartphone,
    'bell-off': BellOff,
};

const FeaturesSection = () => {
    const [activeFeature, setActiveFeature] = useState(null);
    const [hoveredFeature, setHoveredFeature] = useState(null);
    const isMobile = useMediaQuery('(max-width: 768px)');
    const announce = useAnnouncement();

    const toggleFeature = (id) => {
        const newState = activeFeature === id ? null : id;
        setActiveFeature(newState);
        
        if (newState) {
            const feature = features.find(f => f.id === id);
            announce(`Expanded details for ${feature?.title}`, 'polite');
        } else {
            announce('Collapsed feature details', 'polite');
        }
    };

    const getFeatureDetails = (featureId) => {
        const details = {
            'content-blocking': {
                steps: [
                    'Analyzes web content in real-time',
                    'Recognizes distracting content patterns',
                    'Blocks or redirects based on your preferences'
                ],
                benefits: ['Improved focus', 'Reduced distractions', 'Better productivity']
            },
            'adult-filter': {
                steps: [
                    'Uses advanced content recognition technology',
                    'Blocks explicit images and text',
                    'Custom filtering levels available'
                ],
                benefits: ['Safe browsing', 'Family-friendly', 'Customizable protection']
            },
            'app-management': {
                steps: [
                    'Categorizes apps by productivity level',
                    'Set allowed apps during focus time',
                    'Create schedules for different activities'
                ],
                benefits: ['Time management', 'Habit formation', 'Productivity boost']
            },
            'notification-control': {
                steps: [
                    'Configure priority notifications',
                    'Batch non-urgent alerts for later',
                    'Silent hours scheduling'
                ],
                benefits: ['Reduced interruptions', 'Better focus', 'Stress reduction']
            }
        };
        return details[featureId] || { steps: [], benefits: [] };
    };

    return (
        <section 
            id="features" 
            className="section bg-gradient-to-b from-gray-50 to-white dark:from-gray-900 dark:to-gray-800 relative overflow-hidden"
            aria-label="Key features section"
        >
            {/* Background decoration */}
            <div className="absolute inset-0 overflow-hidden">
                <div className="absolute top-0 left-0 w-64 h-64 bg-primary-500/5 rounded-full blur-3xl" />
                <div className="absolute bottom-0 right-0 w-96 h-96 bg-accent-500/5 rounded-full blur-3xl" />
            </div>

            <div className="container-custom relative z-10">
                {/* Section header */}
                <div className="section-title text-center mb-12 lg:mb-16">
                    <motion.div
                        initial={{ opacity: 0, y: 20 }}
                        whileInView={{ opacity: 1, y: 0 }}
                        viewport={{ once: true, margin: "-100px" }}
                        transition={{ duration: 0.6 }}
                        className="inline-flex items-center gap-2 bg-primary-500/10 text-primary-600 px-4 py-2 rounded-full text-sm font-medium mb-4"
                    >
                        <Zap className="w-4 h-4" />
                        <span>Powerful Features</span>
                    </motion.div>

                    <motion.h2
                        initial={{ opacity: 0, y: 20 }}
                        whileInView={{ opacity: 1, y: 0 }}
                        viewport={{ once: true, margin: "-100px" }}
                        transition={{ duration: 0.6, delay: 0.1 }}
                        className="text-3xl md:text-4xl lg:text-5xl font-bold text-gray-900 dark:text-gray-100 mb-4"
                    >
                        Everything you need to
                        <span className="block text-transparent bg-clip-text bg-gradient-to-r from-primary-600 to-accent-600">
                            stay focused
                        </span>
                    </motion.h2>

                    <motion.p
                        initial={{ opacity: 0, y: 20 }}
                        whileInView={{ opacity: 1, y: 0 }}
                        viewport={{ once: true, margin: "-100px" }}
                        transition={{ duration: 0.6, delay: 0.2 }}
                        className="text-lg lg:text-xl text-gray-600 dark:text-gray-300 max-w-3xl mx-auto"
                    >
                        Powerful tools designed to help you reclaim your time and attention
                    </motion.p>
                </div>

                {/* Features grid */}
                <div className="grid grid-cols-1 md:grid-cols-2 gap-6 lg:gap-8">
                    {features.map((feature, index) => {
                        const IconComponent = iconComponents[feature.icon];
                        const isActive = activeFeature === feature.id;
                        const isHovered = hoveredFeature === feature.id;
                        const details = getFeatureDetails(feature.id);

                        return (
                            <motion.div
                                key={feature.id}
                                className={`
                                    relative group cursor-pointer transition-all duration-300
                                    ${isActive ? 'md:col-span-2' : ''}
                                `}
                                initial={{ opacity: 0, y: 20 }}
                                whileInView={{ opacity: 1, y: 0 }}
                                viewport={{ once: true, margin: "-100px" }}
                                transition={{ duration: 0.5, delay: index * 0.1 }}
                                onHoverStart={() => setHoveredFeature(feature.id)}
                                onHoverEnd={() => setHoveredFeature(null)}
                            >
                                <div
                                    className={`
                                        relative overflow-hidden rounded-2xl p-6 lg:p-8 h-full
                                        bg-white dark:bg-gray-800 
                                        border border-gray-200 dark:border-gray-700
                                        shadow-sm hover:shadow-xl
                                        transition-all duration-300
                                        ${isActive ? 'ring-2 ring-primary-500 shadow-xl' : ''}
                                        ${isHovered ? 'transform scale-[1.02]' : ''}
                                    `}
                                    onClick={() => toggleFeature(feature.id)}
                                >
                                    {/* Background gradient */}
                                    <div className="absolute inset-0 bg-gradient-to-br from-transparent via-transparent to-primary-50/50 dark:to-primary-900/20 opacity-0 group-hover:opacity-100 transition-opacity duration-300" />

                                    {/* Content */}
                                    <div className="relative z-10">
                                        {/* Icon and header */}
                                        <div className="flex items-start justify-between mb-4">
                                            <div className="flex items-center gap-4">
                                                <div className={`
                                                    w-12 h-12 lg:w-16 lg:h-16 rounded-xl flex items-center justify-center
                                                    ${feature.color || 'bg-primary-100 text-primary-600 dark:bg-primary-900 dark:text-primary-400'}
                                                    group-hover:scale-110 transition-transform duration-300
                                                `}>
                                                    {IconComponent && (
                                                        <IconComponent size={isMobile ? 24 : 28} />
                                                    )}
                                                </div>
                                                <div>
                                                    <h3 className="text-xl lg:text-2xl font-bold text-gray-900 dark:text-gray-100 mb-1">
                                                        {feature.title}
                                                    </h3>
                                                    <p className="text-gray-600 dark:text-gray-300 text-sm lg:text-base">
                                                        {feature.description}
                                                    </p>
                                                </div>
                                            </div>

                                            {/* Expand button */}
                                            <motion.button
                                                className={`
                                                    flex items-center gap-2 px-3 py-2 rounded-lg text-sm font-medium
                                                    transition-all duration-300
                                                    ${isActive 
                                                        ? 'bg-primary-600 text-white' 
                                                        : 'bg-gray-100 dark:bg-gray-700 text-gray-600 dark:text-gray-300 hover:bg-primary-50 dark:hover:bg-primary-900'
                                                    }
                                                `}
                                                animate={{ rotate: isActive ? 180 : 0 }}
                                                transition={{ duration: 0.3 }}
                                                aria-label={isActive ? 'Collapse details' : 'Expand details'}
                                            >
                                                <span className="hidden sm:inline">
                                                    {isActive ? 'Less' : 'More'}
                                                </span>
                                                <ChevronDown className="w-4 h-4" />
                                            </motion.button>
                                        </div>

                                        {/* Quick benefits preview */}
                                        {!isActive && (
                                            <div className="flex flex-wrap gap-2 mb-4">
                                                {details.benefits.slice(0, 2).map((benefit, i) => (
                                                    <span
                                                        key={i}
                                                        className="inline-flex items-center gap-1 px-3 py-1 bg-green-100 dark:bg-green-900 text-green-800 dark:text-green-200 rounded-full text-xs font-medium"
                                                    >
                                                        <CheckCircle className="w-3 h-3" />
                                                        {benefit}
                                                    </span>
                                                ))}
                                            </div>
                                        )}

                                        {/* Expand/collapse content */}
                                        <AnimatePresence>
                                            {isActive && (
                                                <motion.div
                                                    initial={{ height: 0, opacity: 0 }}
                                                    animate={{ height: 'auto', opacity: 1 }}
                                                    exit={{ height: 0, opacity: 0 }}
                                                    transition={{ duration: 0.3 }}
                                                    className="overflow-hidden"
                                                >
                                                    <div className="pt-4 border-t border-gray-200 dark:border-gray-700">
                                                        <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
                                                            {/* How it works */}
                                                            <div>
                                                                <h4 className="font-semibold text-gray-900 dark:text-gray-100 mb-3 flex items-center gap-2">
                                                                    <ArrowRight className="w-4 h-4 text-primary-600" />
                                                                    How it works
                                                                </h4>
                                                                <ul className="space-y-2">
                                                                    {details.steps.map((step, i) => (
                                                                        <li key={i} className="flex items-start gap-3 text-gray-600 dark:text-gray-300">
                                                                            <div className="w-6 h-6 bg-primary-100 dark:bg-primary-900 text-primary-600 dark:text-primary-400 rounded-full flex items-center justify-center text-xs font-bold mt-0.5 flex-shrink-0">
                                                                                {i + 1}
                                                                            </div>
                                                                            <span className="text-sm lg:text-base">{step}</span>
                                                                        </li>
                                                                    ))}
                                                                </ul>
                                                            </div>

                                                            {/* Benefits */}
                                                            <div>
                                                                <h4 className="font-semibold text-gray-900 dark:text-gray-100 mb-3 flex items-center gap-2">
                                                                    <CheckCircle className="w-4 h-4 text-green-600" />
                                                                    Key benefits
                                                                </h4>
                                                                <div className="flex flex-wrap gap-2">
                                                                    {details.benefits.map((benefit, i) => (
                                                                        <span
                                                                            key={i}
                                                                            className="inline-flex items-center gap-1 px-3 py-2 bg-green-100 dark:bg-green-900 text-green-800 dark:text-green-200 rounded-lg text-sm font-medium"
                                                                        >
                                                                            <CheckCircle className="w-3 h-3" />
                                                                            {benefit}
                                                                        </span>
                                                                    ))}
                                                                </div>
                                                            </div>
                                                        </div>
                                                    </div>
                                                </motion.div>
                                            )}
                                        </AnimatePresence>
                                    </div>
                                </div>
                            </motion.div>
                        );
                    })}
                </div>

                {/* Call to action */}
                <motion.div
                    initial={{ opacity: 0, y: 20 }}
                    whileInView={{ opacity: 1, y: 0 }}
                    viewport={{ once: true, margin: "-100px" }}
                    transition={{ duration: 0.6, delay: 0.5 }}
                    className="text-center mt-12 lg:mt-16"
                >
                    <div className="bg-gradient-to-r from-primary-600 to-accent-600 text-white rounded-2xl p-8 lg:p-12 max-w-4xl mx-auto">
                        <h3 className="text-2xl lg:text-3xl font-bold mb-4">
                            Ready to transform your digital habits?
                        </h3>
                        <p className="text-primary-100 text-lg mb-6 max-w-2xl mx-auto">
                            Join thousands of users who have already taken control of their screen time and improved their productivity.
                        </p>
                        <motion.button
                            onClick={() => document.getElementById('download')?.scrollIntoView({ behavior: 'smooth' })}
                            className="bg-white text-primary-600 hover:bg-gray-100 px-8 py-4 rounded-full font-semibold text-lg flex items-center gap-3 mx-auto transition-all duration-300 shadow-lg hover:shadow-xl"
                            whileHover={{ scale: 1.05 }}
                            whileTap={{ scale: 0.95 }}
                        >
                            <span>Get Started Today</span>
                            <ArrowRight className="w-5 h-5" />
                        </motion.button>
                    </div>
                </motion.div>
            </div>
        </section>
    );
};
export default FeaturesSection;
