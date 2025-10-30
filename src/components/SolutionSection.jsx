import { useState } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import { 
    Activity, 
    Zap, 
    CheckCircle, 
    ArrowRight, 
    Lightbulb,
    Target,
    BarChart3,
    Shield,
    Play,
    Pause
} from 'lucide-react';
import { solutions } from '../data/content';
import { useMediaQuery } from '../hooks/usePerformance';
import { useAnnouncement } from '../utils/accessibility';

const iconComponents = {
    activity: Activity,
    zap: Zap,
};

const SolutionSection = () => {
    const [activeMode, setActiveMode] = useState('normal');
    const isMobile = useMediaQuery('(max-width: 768px)');
    const announce = useAnnouncement();

    const toggleMode = (mode) => {
        setActiveMode(mode);
        const solution = solutions.find(s => s.mode === mode);
        announce(`Switched to ${solution?.title} mode`, 'polite');
    };

    const modeFeatures = {
        normal: [
            { icon: BarChart3, title: 'Usage Analytics', desc: 'Track app usage and patterns' },
            { icon: Target, title: 'Smart Limits', desc: 'Set daily time limits for apps' },
            { icon: Lightbulb, title: 'Insights', desc: 'Get usage insights and recommendations' }
        ],
        focus: [
            { icon: Shield, title: 'Content Blocking', desc: 'Block distracting apps and websites' },
            { icon: Zap, title: 'Content Filtering', desc: 'Filter inappropriate content' },
            { icon: Activity, title: 'Notification Control', desc: 'Silence non-essential notifications' }
        ]
    };

    return (
        <section 
            id="solution" 
            className="section bg-gradient-to-b from-white to-green-50 dark:from-gray-900 dark:to-green-900/10 relative overflow-hidden"
            aria-label="Our solution to digital wellness"
        >
            {/* Background decoration */}
            <div className="absolute inset-0 overflow-hidden">
                <div className="absolute top-0 left-0 w-64 h-64 bg-green-500/5 rounded-full blur-3xl" />
                <div className="absolute bottom-0 right-0 w-96 h-96 bg-blue-500/5 rounded-full blur-3xl" />
            </div>

            <div className="container-custom relative z-10">
                {/* Section header */}
                <div className="section-title text-center mb-12 lg:mb-16">
                    <motion.div
                        initial={{ opacity: 0, y: 20 }}
                        whileInView={{ opacity: 1, y: 0 }}
                        viewport={{ once: true, margin: "-100px" }}
                        transition={{ duration: 0.6 }}
                        className="inline-flex items-center gap-2 bg-green-500/10 text-green-600 dark:text-green-400 px-4 py-2 rounded-full text-sm font-medium mb-4"
                    >
                        <Lightbulb className="w-4 h-4" />
                        <span>Our Solution</span>
                    </motion.div>

                    <motion.h2
                        initial={{ opacity: 0, y: 20 }}
                        whileInView={{ opacity: 1, y: 0 }}
                        viewport={{ once: true, margin: "-100px" }}
                        transition={{ duration: 0.6, delay: 0.1 }}
                        className="text-3xl md:text-4xl lg:text-5xl font-bold text-gray-900 dark:text-gray-100 mb-4"
                    >
                        Two powerful modes for
                        <span className="block text-transparent bg-clip-text bg-gradient-to-r from-green-600 to-blue-600">
                            digital wellness
                        </span>
                    </motion.h2>

                    <motion.p
                        initial={{ opacity: 0, y: 20 }}
                        whileInView={{ opacity: 1, y: 0 }}
                        viewport={{ once: true, margin: "-100px" }}
                        transition={{ duration: 0.6, delay: 0.2 }}
                        className="text-lg lg:text-xl text-gray-600 dark:text-gray-300 max-w-3xl mx-auto"
                    >
                        Focus combines monitoring and blocking to help you build better digital habits, 
                        with flexible modes that adapt to your needs.
                    </motion.p>
                </div>

                {/* Mode toggle */}
                <motion.div
                    initial={{ opacity: 0, y: 20 }}
                    whileInView={{ opacity: 1, y: 0 }}
                    viewport={{ once: true, margin: "-100px" }}
                    transition={{ duration: 0.6, delay: 0.3 }}
                    className="flex justify-center mb-12"
                >
                    <div className="bg-white dark:bg-gray-800 rounded-2xl p-2 shadow-lg border border-gray-200 dark:border-gray-700">
                        <div className="flex gap-2">
                            {solutions.map((solution) => (
                                <button
                                    key={solution.mode}
                                    onClick={() => toggleMode(solution.mode)}
                                    className={`
                                        px-6 py-3 rounded-xl font-semibold transition-all duration-300 flex items-center gap-2
                                        ${activeMode === solution.mode
                                            ? solution.mode === 'normal'
                                                ? 'bg-secondary-600 text-white shadow-lg'
                                                : 'bg-primary-600 text-white shadow-lg'
                                            : 'text-gray-600 dark:text-gray-400 hover:bg-gray-100 dark:hover:bg-gray-700'
                                        }
                                    `}
                                    aria-pressed={activeMode === solution.mode}
                                >
                                    {solution.mode === 'normal' ? (
                                        <Activity className="w-5 h-5" />
                                    ) : (
                                        <Zap className="w-5 h-5" />
                                    )}
                                    <span>{solution.title}</span>
                                </button>
                            ))}
                        </div>
                    </div>
                </motion.div>

                {/* Mode content */}
                <AnimatePresence mode="wait">
                    <motion.div
                        key={activeMode}
                        initial={{ opacity: 0, y: 20 }}
                        animate={{ opacity: 1, y: 0 }}
                        exit={{ opacity: 0, y: -20 }}
                        transition={{ duration: 0.4 }}
                        className="grid grid-cols-1 lg:grid-cols-2 gap-8 lg:gap-12 items-center mb-16"
                    >
                        {/* Mode description */}
                        <div className="order-2 lg:order-1">
                            <div className={`
                                inline-flex items-center gap-2 px-4 py-2 rounded-full text-sm font-medium mb-6
                                ${activeMode === 'normal' 
                                    ? 'bg-secondary-100 dark:bg-secondary-900 text-secondary-700 dark:text-secondary-300'
                                    : 'bg-primary-100 dark:bg-primary-900 text-primary-700 dark:text-primary-300'
                                }
                            `}>
                                {activeMode === 'normal' ? <Activity className="w-4 h-4" /> : <Zap className="w-4 h-4" />}
                                <span>{activeMode === 'normal' ? 'Monitor & Analyze' : 'Block & Filter'}</span>
                            </div>

                            <h3 className="text-2xl lg:text-3xl font-bold text-gray-900 dark:text-gray-100 mb-4">
                                {solutions.find(s => s.mode === activeMode)?.title}
                            </h3>

                            <p className="text-lg text-gray-600 dark:text-gray-300 mb-8 leading-relaxed">
                                {solutions.find(s => s.mode === activeMode)?.description}
                            </p>

                            {/* Features list */}
                            <div className="space-y-4">
                                {modeFeatures[activeMode].map((feature, index) => (
                                    <motion.div
                                        key={feature.title}
                                        initial={{ opacity: 0, x: -20 }}
                                        animate={{ opacity: 1, x: 0 }}
                                        transition={{ duration: 0.3, delay: index * 0.1 }}
                                        className="flex items-start gap-4 p-4 bg-white/50 dark:bg-gray-800/50 backdrop-blur-sm rounded-xl border border-gray-200/50 dark:border-gray-700/50"
                                    >
                                        <div className={`
                                            w-10 h-10 rounded-lg flex items-center justify-center flex-shrink-0
                                            ${activeMode === 'normal'
                                                ? 'bg-secondary-100 dark:bg-secondary-900 text-secondary-600 dark:text-secondary-400'
                                                : 'bg-primary-100 dark:bg-primary-900 text-primary-600 dark:text-primary-400'
                                            }
                                        `}>
                                            <feature.icon className="w-5 h-5" />
                                        </div>
                                        <div>
                                            <h4 className="font-semibold text-gray-900 dark:text-gray-100 mb-1">
                                                {feature.title}
                                            </h4>
                                            <p className="text-gray-600 dark:text-gray-300 text-sm">
                                                {feature.desc}
                                            </p>
                                        </div>
                                    </motion.div>
                                ))}
                            </div>
                        </div>

                        {/* Visual representation */}
                        <div className="order-1 lg:order-2 flex justify-center">
                            <div className="relative">
                                <motion.div
                                    initial={{ scale: 0.8, opacity: 0 }}
                                    animate={{ scale: 1, opacity: 1 }}
                                    transition={{ duration: 0.5 }}
                                    className={`
                                        w-64 h-80 rounded-[32px] border-4 overflow-hidden shadow-2xl
                                        ${activeMode === 'normal'
                                            ? 'border-secondary-300 bg-secondary-50 dark:bg-secondary-900'
                                            : 'border-primary-300 bg-primary-50 dark:bg-primary-900'
                                        }
                                    `}
                                >
                                    {/* Phone screen mockup */}
                                    <div className="h-full flex flex-col">
                                        {/* Status bar */}
                                        <div className={`
                                            h-16 flex items-center justify-center
                                            ${activeMode === 'normal'
                                                ? 'bg-secondary-100 dark:bg-secondary-800'
                                                : 'bg-primary-100 dark:bg-primary-800'
                                            }
                                        `}>
                                            <div className="text-xs font-medium text-gray-700 dark:text-gray-300">
                                                {activeMode === 'normal' ? 'Monitor Mode' : 'Focus Mode'}
                                            </div>
                                        </div>

                                        {/* Content area */}
                                        <div className="flex-1 p-4">
                                            {activeMode === 'normal' ? (
                                                <div className="space-y-3">
                                                    <div className="bg-white dark:bg-gray-700 rounded-lg p-3">
                                                        <div className="flex justify-between items-center mb-2">
                                                            <span className="text-sm font-medium">Social Media</span>
                                                            <span className="text-secondary-600 dark:text-secondary-400 text-sm">2h 30m</span>
                                                        </div>
                                                        <div className="w-full bg-gray-200 dark:bg-gray-600 rounded-full h-2">
                                                            <div className="bg-secondary-500 h-2 rounded-full" style={{ width: '75%' }} />
                                                        </div>
                                                    </div>
                                                    <div className="bg-white dark:bg-gray-700 rounded-lg p-3">
                                                        <div className="flex justify-between items-center mb-2">
                                                            <span className="text-sm font-medium">Entertainment</span>
                                                            <span className="text-secondary-600 dark:text-secondary-400 text-sm">1h 15m</span>
                                                        </div>
                                                        <div className="w-full bg-gray-200 dark:bg-gray-600 rounded-full h-2">
                                                            <div className="bg-secondary-500 h-2 rounded-full" style={{ width: '40%' }} />
                                                        </div>
                                                    </div>
                                                </div>
                                            ) : (
                                                <div className="space-y-3">
                                                    <div className="bg-white dark:bg-gray-700 rounded-lg p-3 flex items-center gap-3">
                                                        <Shield className="w-5 h-5 text-primary-600 dark:text-primary-400" />
                                                        <div>
                                                            <div className="text-sm font-medium">Apps Blocked</div>
                                                            <div className="text-xs text-gray-500">12 distracting apps</div>
                                                        </div>
                                                    </div>
                                                    <div className="bg-white dark:bg-gray-700 rounded-lg p-3 flex items-center gap-3">
                                                        <CheckCircle className="w-5 h-5 text-green-600" />
                                                        <div>
                                                            <div className="text-sm font-medium">Focus Session</div>
                                                            <div className="text-xs text-gray-500">45 minutes active</div>
                                                        </div>
                                                    </div>
                                                </div>
                                            )}
                                        </div>
                                    </div>
                                </motion.div>

                                {/* Floating indicators */}
                                <motion.div
                                    initial={{ opacity: 0, scale: 0.8 }}
                                    animate={{ opacity: 1, scale: 1 }}
                                    transition={{ duration: 0.5, delay: 0.3 }}
                                    className={`
                                        absolute -top-4 -right-4 px-3 py-2 rounded-lg text-xs font-medium shadow-lg
                                        ${activeMode === 'normal'
                                            ? 'bg-secondary-600 text-white'
                                            : 'bg-primary-600 text-white'
                                        }
                                    `}
                                >
                                    {activeMode === 'normal' ? '📊 Tracking' : '🔒 Protected'}
                                </motion.div>
                            </div>
                        </div>
                    </motion.div>
                </AnimatePresence>

                {/* Call to action */}
                <motion.div
                    initial={{ opacity: 0, y: 20 }}
                    whileInView={{ opacity: 1, y: 0 }}
                    viewport={{ once: true, margin: "-100px" }}
                    transition={{ duration: 0.6, delay: 0.4 }}
                    className="text-center"
                >
                    <div className="bg-gradient-to-r from-green-600 to-blue-600 text-white rounded-2xl p-8 lg:p-12 max-w-4xl mx-auto">
                        <h3 className="text-2xl lg:text-3xl font-bold mb-4">
                            Ready to take control?
                        </h3>
                        <p className="text-green-100 text-lg mb-6 max-w-2xl mx-auto">
                            Switch between modes anytime. Start with monitoring to understand your habits, 
                            then activate focus mode when you need to eliminate distractions.
                        </p>
                        <motion.button
                            onClick={() => document.getElementById('features')?.scrollIntoView({ behavior: 'smooth' })}
                            className="bg-white text-green-600 hover:bg-gray-100 px-8 py-4 rounded-full font-semibold text-lg flex items-center gap-3 mx-auto transition-all duration-300 shadow-lg hover:shadow-xl"
                            whileHover={{ scale: 1.05 }}
                            whileTap={{ scale: 0.95 }}
                        >
                            <span>Explore Features</span>
                            <ArrowRight className="w-5 h-5" />
                        </motion.button>
                    </div>
                </motion.div>
            </div>
        </section>
    );
};
export default SolutionSection;
