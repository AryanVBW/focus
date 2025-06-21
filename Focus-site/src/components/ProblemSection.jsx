import { motion } from 'framer-motion';
import { Brain, Clock, Moon, Users, AlertTriangle, TrendingUp } from 'lucide-react';
import { problems } from '../data/content';
import { useMediaQuery } from '../hooks/usePerformance';

const iconComponents = {
    brain: Brain,
    clock: Clock,
    moon: Moon,
    users: Users,
};

const ProblemSection = () => {
    const isMobile = useMediaQuery('(max-width: 768px)');

    const stats = [
        { value: '96', label: 'Phone checks per day', icon: TrendingUp },
        { value: '3h', label: 'Average screen time', icon: Clock },
        { value: '75%', label: 'Feel phone anxiety', icon: AlertTriangle },
        { value: '40%', label: 'Check before bed', icon: Moon }
    ];

    return (
        <section 
            id="problem" 
            className="section bg-gradient-to-b from-red-50 to-orange-50 dark:from-red-900/10 dark:to-orange-900/10 relative overflow-hidden"
            aria-label="Digital wellness problems"
        >
            {/* Background decoration */}
            <div className="absolute inset-0 overflow-hidden">
                <div className="absolute top-0 right-0 w-64 h-64 bg-red-500/5 rounded-full blur-3xl" />
                <div className="absolute bottom-0 left-0 w-96 h-96 bg-orange-500/5 rounded-full blur-3xl" />
            </div>

            <div className="container-custom relative z-10">
                {/* Section header */}
                <div className="section-title text-center mb-12 lg:mb-16">
                    <motion.div
                        initial={{ opacity: 0, y: 20 }}
                        whileInView={{ opacity: 1, y: 0 }}
                        viewport={{ once: true, margin: "-100px" }}
                        transition={{ duration: 0.6 }}
                        className="inline-flex items-center gap-2 bg-red-500/10 text-red-600 dark:text-red-400 px-4 py-2 rounded-full text-sm font-medium mb-4"
                    >
                        <AlertTriangle className="w-4 h-4" />
                        <span>The Challenge</span>
                    </motion.div>

                    <motion.h2
                        initial={{ opacity: 0, y: 20 }}
                        whileInView={{ opacity: 1, y: 0 }}
                        viewport={{ once: true, margin: "-100px" }}
                        transition={{ duration: 0.6, delay: 0.1 }}
                        className="text-3xl md:text-4xl lg:text-5xl font-bold text-gray-900 dark:text-gray-100 mb-4"
                    >
                        Our devices are designed to
                        <span className="block text-transparent bg-clip-text bg-gradient-to-r from-red-600 to-orange-600">
                            capture our attention
                        </span>
                    </motion.h2>

                    <motion.p
                        initial={{ opacity: 0, y: 20 }}
                        whileInView={{ opacity: 1, y: 0 }}
                        viewport={{ once: true, margin: "-100px" }}
                        transition={{ duration: 0.6, delay: 0.2 }}
                        className="text-lg lg:text-xl text-gray-600 dark:text-gray-300 max-w-3xl mx-auto"
                    >
                        Technology companies spend billions perfecting algorithms to keep us scrolling, 
                        clicking, and coming back for more.
                    </motion.p>
                </div>

                {/* Statistics */}
                <div className="grid grid-cols-2 md:grid-cols-4 gap-4 lg:gap-8 mb-12 lg:mb-16">
                    {stats.map((stat, index) => (
                        <motion.div
                            key={stat.label}
                            initial={{ opacity: 0, y: 20 }}
                            whileInView={{ opacity: 1, y: 0 }}
                            viewport={{ once: true, margin: "-100px" }}
                            transition={{ duration: 0.5, delay: index * 0.1 }}
                            className="text-center"
                        >
                            <div className="bg-white dark:bg-gray-800 rounded-2xl p-4 lg:p-6 shadow-sm border border-gray-200 dark:border-gray-700 hover:shadow-md transition-all duration-300">
                                <div className="inline-flex items-center justify-center w-12 h-12 bg-red-100 dark:bg-red-900 text-red-600 dark:text-red-400 rounded-xl mb-3">
                                    <stat.icon className="w-6 h-6" />
                                </div>
                                <div className="text-2xl lg:text-3xl font-bold text-gray-900 dark:text-white mb-1">
                                    {stat.value}
                                </div>
                                <div className="text-sm lg:text-base text-gray-600 dark:text-gray-400">
                                    {stat.label}
                                </div>
                            </div>
                        </motion.div>
                    ))}
                </div>

                {/* Problems grid */}
                <div className="grid grid-cols-1 md:grid-cols-2 gap-6 lg:gap-8 mb-12 lg:mb-16">
                    {problems.map((problem, index) => {
                        const IconComponent = iconComponents[problem.icon];
                        return (
                            <motion.div
                                key={problem.id}
                                className="group relative"
                                initial={{ opacity: 0, y: 20 }}
                                whileInView={{ opacity: 1, y: 0 }}
                                viewport={{ once: true, margin: "-100px" }}
                                transition={{ duration: 0.5, delay: index * 0.1 }}
                                whileHover={{ y: -8 }}
                            >
                                <div className="bg-white dark:bg-gray-800 rounded-2xl p-6 lg:p-8 shadow-sm border border-gray-200 dark:border-gray-700 hover:shadow-xl transition-all duration-300 h-full">
                                    {/* Background gradient */}
                                    <div className="absolute inset-0 bg-gradient-to-br from-red-500/5 to-orange-500/5 rounded-2xl opacity-0 group-hover:opacity-100 transition-opacity duration-300" />
                                    
                                    <div className="relative z-10">
                                        <div className="flex items-start gap-4 mb-4">
                                            <div className="w-14 h-14 bg-red-100 dark:bg-red-900 rounded-xl flex items-center justify-center text-red-600 dark:text-red-400 group-hover:scale-110 transition-transform duration-300 flex-shrink-0">
                                                {IconComponent && <IconComponent size={24} />}
                                            </div>
                                            <div>
                                                <h3 className="text-xl lg:text-2xl font-bold text-gray-900 dark:text-gray-100 mb-2">
                                                    {problem.title}
                                                </h3>
                                                <p className="text-gray-600 dark:text-gray-300 leading-relaxed">
                                                    {problem.description}
                                                </p>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </motion.div>
                        );
                    })}
                </div>

                {/* Quote section */}
                <motion.div
                    className="bg-gradient-to-r from-red-500/10 to-orange-500/10 backdrop-blur-sm rounded-2xl p-8 lg:p-12 text-center max-w-4xl mx-auto border border-red-200/20 dark:border-red-500/20"
                    initial={{ opacity: 0, y: 20 }}
                    whileInView={{ opacity: 1, y: 0 }}
                    viewport={{ once: true, margin: "-100px" }}
                    transition={{ duration: 0.6, delay: 0.5 }}
                >
                    <div className="mb-6">
                        <div className="inline-flex items-center justify-center w-16 h-16 bg-red-100 dark:bg-red-900 text-red-600 dark:text-red-400 rounded-full mb-6">
                            <AlertTriangle className="w-8 h-8" />
                        </div>
                    </div>
                    
                    <blockquote className="text-xl lg:text-2xl font-medium text-gray-900 dark:text-gray-100 italic mb-4">
                        "The average person checks their phone 96 times a day — once every 10 minutes."
                    </blockquote>
                    
                    <cite className="text-gray-600 dark:text-gray-400 text-sm lg:text-base">
                        — Digital Wellness Research, 2023
                    </cite>

                    <div className="mt-8 grid grid-cols-1 sm:grid-cols-3 gap-4 text-center">
                        <div className="bg-white/50 dark:bg-gray-800/50 backdrop-blur-sm rounded-xl p-4 border border-white/20">
                            <div className="text-2xl font-bold text-red-600 dark:text-red-400 mb-1">85%</div>
                            <div className="text-sm text-gray-600 dark:text-gray-400">Experience FOMO</div>
                        </div>
                        <div className="bg-white/50 dark:bg-gray-800/50 backdrop-blur-sm rounded-xl p-4 border border-white/20">
                            <div className="text-2xl font-bold text-orange-600 dark:text-orange-400 mb-1">60%</div>
                            <div className="text-sm text-gray-600 dark:text-gray-400">Feel overwhelmed</div>
                        </div>
                        <div className="bg-white/50 dark:bg-gray-800/50 backdrop-blur-sm rounded-xl p-4 border border-white/20">
                            <div className="text-2xl font-bold text-red-600 dark:text-red-400 mb-1">45%</div>
                            <div className="text-sm text-gray-600 dark:text-gray-400">Sleep disruption</div>
                        </div>
                    </div>
                </motion.div>
            </div>
        </section>
    );
};
export default ProblemSection;
