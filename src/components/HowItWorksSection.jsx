import { useState } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import { 
    AlertCircle, 
    Layers, 
    Search, 
    Shield, 
    PlayCircle,
    CheckCircle,
    ArrowRight,
    Smartphone,
    Settings,
    BarChart3
} from 'lucide-react';
import { howItWorksSteps } from '../data/content';
import { useMediaQuery } from '../hooks/usePerformance';
import { useAnnouncement } from '../utils/accessibility';

const iconComponents = {
    search: Search,
    layers: Layers,
    shield: Shield,
    'alert-circle': AlertCircle,
};

const HowItWorksSection = () => {
    const [activeStep, setActiveStep] = useState(0);
    const [isPlaying, setIsPlaying] = useState(false);
    const isMobile = useMediaQuery('(max-width: 768px)');
    const announce = useAnnouncement();

    const handleStepClick = (stepIndex) => {
        setActiveStep(stepIndex);
        announce(`Selected step ${stepIndex + 1}: ${howItWorksSteps[stepIndex].title}`, 'polite');
    };

    const playAnimation = () => {
        setIsPlaying(true);
        announce('Playing how it works animation', 'polite');
        
        let currentStep = 0;
        const interval = setInterval(() => {
            currentStep = (currentStep + 1) % howItWorksSteps.length;
            setActiveStep(currentStep);
            
            if (currentStep === 0) {
                setIsPlaying(false);
                clearInterval(interval);
            }
        }, 2000);
    };

    return (
        <section 
            id="how-it-works" 
            className="section bg-gradient-to-b from-white to-blue-50 dark:from-gray-900 dark:to-blue-900/10 relative overflow-hidden"
            aria-label="How Focus works"
        >
            {/* Background decoration */}
            <div className="absolute inset-0 overflow-hidden">
                <div className="absolute top-0 right-0 w-64 h-64 bg-blue-500/5 rounded-full blur-3xl" />
                <div className="absolute bottom-0 left-0 w-96 h-96 bg-purple-500/5 rounded-full blur-3xl" />
            </div>

            <div className="container-custom relative z-10">
                {/* Section header */}
                <div className="section-title text-center mb-12 lg:mb-16">
                    <motion.div
                        initial={{ opacity: 0, y: 20 }}
                        whileInView={{ opacity: 1, y: 0 }}
                        viewport={{ once: true, margin: "-100px" }}
                        transition={{ duration: 0.6 }}
                        className="inline-flex items-center gap-2 bg-blue-500/10 text-blue-600 dark:text-blue-400 px-4 py-2 rounded-full text-sm font-medium mb-4"
                    >
                        <Settings className="w-4 h-4" />
                        <span>Simple Process</span>
                    </motion.div>

                    <motion.h2
                        initial={{ opacity: 0, y: 20 }}
                        whileInView={{ opacity: 1, y: 0 }}
                        viewport={{ once: true, margin: "-100px" }}
                        transition={{ duration: 0.6, delay: 0.1 }}
                        className="text-3xl md:text-4xl lg:text-5xl font-bold text-gray-900 dark:text-gray-100 mb-4"
                    >
                        How Focus works its
                        <span className="block text-transparent bg-clip-text bg-gradient-to-r from-blue-600 to-purple-600">
                            digital magic
                        </span>
                    </motion.h2>

                    <motion.p
                        initial={{ opacity: 0, y: 20 }}
                        whileInView={{ opacity: 1, y: 0 }}
                        viewport={{ once: true, margin: "-100px" }}
                        transition={{ duration: 0.6, delay: 0.2 }}
                        className="text-lg lg:text-xl text-gray-600 dark:text-gray-300 max-w-3xl mx-auto mb-8"
                    >
                        A simple yet powerful approach to digital wellness that works in the background 
                        to help you stay focused and productive.
                    </motion.p>

                    {/* Play animation button */}
                    <motion.button
                        onClick={playAnimation}
                        disabled={isPlaying}
                        className="group inline-flex items-center gap-3 bg-blue-600 hover:bg-blue-700 disabled:bg-blue-400 text-white px-6 py-3 rounded-full font-semibold transition-all duration-300 shadow-lg hover:shadow-xl"
                        whileHover={{ scale: 1.05 }}
                        whileTap={{ scale: 0.95 }}
                        initial={{ opacity: 0, y: 20 }}
                        whileInView={{ opacity: 1, y: 0 }}
                        viewport={{ once: true, margin: "-100px" }}
                        transition={{ duration: 0.6, delay: 0.3 }}
                    >
                        <PlayCircle className="w-5 h-5 group-hover:scale-110 transition-transform" />
                        <span>{isPlaying ? 'Playing...' : 'Watch How It Works'}</span>
                    </motion.button>
                </div>

                <div className="grid grid-cols-1 lg:grid-cols-2 gap-12 lg:gap-16 items-start">
                    {/* Steps list */}
                    <div className="order-2 lg:order-1">
                        <div className="space-y-4">
                            {howItWorksSteps.map((step, index) => {
                                const IconComponent = iconComponents[step.icon];
                                const isActive = activeStep === index;
                                const isCompleted = activeStep > index;

                                return (
                                    <motion.div
                                        key={step.id}
                                        initial={{ opacity: 0, x: -20 }}
                                        whileInView={{ opacity: 1, x: 0 }}
                                        viewport={{ once: true, margin: "-100px" }}
                                        transition={{ duration: 0.5, delay: index * 0.1 }}
                                        className="relative"
                                    >
                                        <button
                                            onClick={() => handleStepClick(index)}
                                            className={`
                                                w-full text-left p-6 rounded-2xl border-2 transition-all duration-300 group
                                                ${isActive 
                                                    ? 'border-blue-500 bg-blue-50 dark:bg-blue-900/20 shadow-lg' 
                                                    : isCompleted
                                                        ? 'border-green-500 bg-green-50 dark:bg-green-900/20'
                                                        : 'border-gray-200 dark:border-gray-700 bg-white dark:bg-gray-800 hover:border-blue-300 hover:shadow-md'
                                                }
                                            `}
                                        >
                                            <div className="flex items-start gap-4">
                                                {/* Step indicator */}
                                                <div className="flex-shrink-0">
                                                    <div className={`
                                                        w-12 h-12 rounded-xl flex items-center justify-center transition-all duration-300
                                                        ${isActive
                                                            ? 'bg-blue-600 text-white shadow-lg scale-110'
                                                            : isCompleted
                                                                ? 'bg-green-600 text-white'
                                                                : 'bg-gray-100 dark:bg-gray-700 text-gray-600 dark:text-gray-400 group-hover:bg-blue-100 group-hover:text-blue-600'
                                                        }
                                                    `}>
                                                        {isCompleted ? (
                                                            <CheckCircle className="w-6 h-6" />
                                                        ) : IconComponent ? (
                                                            <IconComponent className="w-6 h-6" />
                                                        ) : (
                                                            <span className="font-bold">{index + 1}</span>
                                                        )}
                                                    </div>
                                                </div>

                                                {/* Content */}
                                                <div className="flex-1">
                                                    <div className="flex items-center gap-2 mb-2">
                                                        <h3 className={`
                                                            text-lg lg:text-xl font-bold transition-colors duration-300
                                                            ${isActive 
                                                                ? 'text-blue-900 dark:text-blue-100' 
                                                                : isCompleted
                                                                    ? 'text-green-900 dark:text-green-100'
                                                                    : 'text-gray-900 dark:text-gray-100'
                                                            }
                                                        `}>
                                                            {step.title}
                                                        </h3>
                                                        <span className={`
                                                            px-2 py-1 text-xs font-medium rounded-full
                                                            ${isActive 
                                                                ? 'bg-blue-200 text-blue-800 dark:bg-blue-800 dark:text-blue-200' 
                                                                : isCompleted
                                                                    ? 'bg-green-200 text-green-800 dark:bg-green-800 dark:text-green-200'
                                                                    : 'bg-gray-200 text-gray-600 dark:bg-gray-700 dark:text-gray-400'
                                                            }
                                                        `}>
                                                            Step {index + 1}
                                                        </span>
                                                    </div>
                                                    <p className={`
                                                        text-sm lg:text-base leading-relaxed transition-colors duration-300
                                                        ${isActive 
                                                            ? 'text-blue-800 dark:text-blue-200' 
                                                            : isCompleted
                                                                ? 'text-green-800 dark:text-green-200'
                                                                : 'text-gray-600 dark:text-gray-300'
                                                        }
                                                    `}>
                                                        {step.description}
                                                    </p>
                                                </div>

                                                {/* Arrow indicator */}
                                                <div className={`
                                                    flex-shrink-0 transition-all duration-300
                                                    ${isActive ? 'opacity-100 translate-x-0' : 'opacity-0 translate-x-2'}
                                                `}>
                                                    <ArrowRight className="w-5 h-5 text-blue-600" />
                                                </div>
                                            </div>
                                        </button>

                                        {/* Connection line */}
                                        {index < howItWorksSteps.length - 1 && (
                                            <div className="absolute left-6 top-[78px] w-px h-4 bg-gradient-to-b from-gray-300 to-transparent dark:from-gray-600" />
                                        )}
                                    </motion.div>
                                );
                            })}
                        </div>
                    </div>

                    {/* Visual demonstration */}
                    <div className="order-1 lg:order-2 sticky top-8">
                        <motion.div
                            initial={{ opacity: 0, scale: 0.9 }}
                            whileInView={{ opacity: 1, scale: 1 }}
                            viewport={{ once: true, margin: "-100px" }}
                            transition={{ duration: 0.6 }}
                            className="relative"
                        >
                            {/* Phone mockup */}
                            <div className="relative w-64 mx-auto">
                                <div className="bg-gray-900 rounded-[2.5rem] p-2 shadow-2xl">
                                    <div className="bg-white dark:bg-gray-800 rounded-[2rem] overflow-hidden h-[500px]">
                                        {/* Status bar */}
                                        <div className="bg-gray-100 dark:bg-gray-700 h-12 flex items-center justify-center">
                                            <div className="text-xs font-medium text-gray-600 dark:text-gray-300">
                                                Focus App
                                            </div>
                                        </div>

                                        {/* Content area */}
                                        <div className="p-4 h-full">
                                            <AnimatePresence mode="wait">
                                                <motion.div
                                                    key={activeStep}
                                                    initial={{ opacity: 0, y: 20 }}
                                                    animate={{ opacity: 1, y: 0 }}
                                                    exit={{ opacity: 0, y: -20 }}
                                                    transition={{ duration: 0.3 }}
                                                    className="h-full flex flex-col"
                                                >
                                                    {/* Dynamic content based on active step */}
                                                    {activeStep === 0 && (
                                                        <div className="space-y-4">
                                                            <div className="text-center mb-6">
                                                                <Search className="w-12 h-12 text-blue-600 mx-auto mb-2" />
                                                                <h4 className="font-semibold text-gray-900 dark:text-gray-100">
                                                                    Scanning Content
                                                                </h4>
                                                            </div>
                                                            <div className="space-y-3">
                                                                {[1, 2, 3].map((i) => (
                                                                    <div key={i} className="bg-blue-50 dark:bg-blue-900/20 rounded-lg p-3 flex items-center gap-3">
                                                                        <motion.div
                                                                            animate={{ rotate: 360 }}
                                                                            transition={{ duration: 2, repeat: Infinity, ease: "linear" }}
                                                                            className="w-4 h-4 border-2 border-blue-600 border-t-transparent rounded-full"
                                                                        />
                                                                        <span className="text-sm text-gray-700 dark:text-gray-300">
                                                                            Analyzing page {i}...
                                                                        </span>
                                                                    </div>
                                                                ))}
                                                            </div>
                                                        </div>
                                                    )}

                                                    {activeStep === 1 && (
                                                        <div className="space-y-4">
                                                            <div className="text-center mb-6">
                                                                <Layers className="w-12 h-12 text-purple-600 mx-auto mb-2" />
                                                                <h4 className="font-semibold text-gray-900 dark:text-gray-100">
                                                                    AI Processing
                                                                </h4>
                                                            </div>
                                                            <div className="space-y-3">
                                                                <div className="bg-purple-50 dark:bg-purple-900/20 rounded-lg p-3">
                                                                    <div className="flex justify-between items-center mb-2">
                                                                        <span className="text-sm font-medium">Content Analysis</span>
                                                                        <span className="text-purple-600 text-sm">95%</span>
                                                                    </div>
                                                                    <div className="w-full bg-purple-200 dark:bg-purple-800 rounded-full h-2">
                                                                        <motion.div
                                                                            className="bg-purple-600 h-2 rounded-full"
                                                                            initial={{ width: 0 }}
                                                                            animate={{ width: '95%' }}
                                                                            transition={{ duration: 1.5 }}
                                                                        />
                                                                    </div>
                                                                </div>
                                                                <div className="bg-green-50 dark:bg-green-900/20 rounded-lg p-3 flex items-center gap-3">
                                                                    <CheckCircle className="w-5 h-5 text-green-600" />
                                                                    <span className="text-sm text-gray-700 dark:text-gray-300">
                                                                        Pattern recognized
                                                                    </span>
                                                                </div>
                                                            </div>
                                                        </div>
                                                    )}

                                                    {activeStep === 2 && (
                                                        <div className="space-y-4">
                                                            <div className="text-center mb-6">
                                                                <Shield className="w-12 h-12 text-green-600 mx-auto mb-2" />
                                                                <h4 className="font-semibold text-gray-900 dark:text-gray-100">
                                                                    Content Blocked
                                                                </h4>
                                                            </div>
                                                            <div className="bg-green-50 dark:bg-green-900/20 rounded-lg p-4 text-center">
                                                                <Shield className="w-8 h-8 text-green-600 mx-auto mb-2" />
                                                                <p className="text-green-800 dark:text-green-200 font-medium">
                                                                    Distracting content blocked
                                                                </p>
                                                                <p className="text-green-600 dark:text-green-400 text-sm mt-1">
                                                                    Stay focused on what matters
                                                                </p>
                                                            </div>
                                                        </div>
                                                    )}

                                                    {activeStep === 3 && (
                                                        <div className="space-y-4">
                                                            <div className="text-center mb-6">
                                                                <BarChart3 className="w-12 h-12 text-orange-600 mx-auto mb-2" />
                                                                <h4 className="font-semibold text-gray-900 dark:text-gray-100">
                                                                    Usage Analytics
                                                                </h4>
                                                            </div>
                                                            <div className="space-y-3">
                                                                <div className="bg-orange-50 dark:bg-orange-900/20 rounded-lg p-3">
                                                                    <div className="flex justify-between items-center mb-2">
                                                                        <span className="text-sm font-medium">Time Saved</span>
                                                                        <span className="text-orange-600 font-bold">2h 30m</span>
                                                                    </div>
                                                                    <div className="flex justify-between items-center">
                                                                        <span className="text-sm font-medium">Distractions Blocked</span>
                                                                        <span className="text-orange-600 font-bold">47</span>
                                                                    </div>
                                                                </div>
                                                                <div className="bg-blue-50 dark:bg-blue-900/20 rounded-lg p-3 flex items-center gap-3">
                                                                    <CheckCircle className="w-5 h-5 text-blue-600" />
                                                                    <span className="text-sm text-gray-700 dark:text-gray-300">
                                                                        Productive day achieved!
                                                                    </span>
                                                                </div>
                                                            </div>
                                                        </div>
                                                    )}
                                                </motion.div>
                                            </AnimatePresence>
                                        </div>
                                    </div>
                                </div>

                                {/* Floating indicators */}
                                <motion.div
                                    initial={{ opacity: 0, scale: 0.8 }}
                                    animate={{ opacity: 1, scale: 1 }}
                                    className="absolute -top-4 -right-4 bg-green-500 text-white px-3 py-2 rounded-lg text-xs font-medium shadow-lg"
                                >
                                    🛡️ Protected
                                </motion.div>

                                <motion.div
                                    initial={{ opacity: 0, scale: 0.8 }}
                                    animate={{ opacity: 1, scale: 1 }}
                                    transition={{ delay: 0.2 }}
                                    className="absolute -bottom-4 -left-4 bg-blue-500 text-white px-3 py-2 rounded-lg text-xs font-medium shadow-lg"
                                >
                                    📊 Analyzing
                                </motion.div>
                            </div>
                        </motion.div>
                    </div>
                </div>
            </div>
        </section>
    );
};

export default HowItWorksSection;
