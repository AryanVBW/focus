import { jsx as _jsx, jsxs as _jsxs, Fragment as _Fragment } from "react/jsx-runtime";
import { motion } from 'framer-motion';
import { Activity, Zap } from 'lucide-react';
import { solutions } from '../data/content';
const iconComponents = {
    activity: Activity,
    zap: Zap,
};
const SolutionSection = () => {
    return (_jsx("section", { id: "solution", className: "section bg-white", children: _jsxs("div", { className: "container-custom", children: [_jsxs("div", { className: "section-title", children: [_jsx(motion.h2, { initial: { opacity: 0, y: 20 }, whileInView: { opacity: 1, y: 0 }, viewport: { once: true, margin: "-100px" }, transition: { duration: 0.6 }, children: "Our Solution" }), _jsx(motion.p, { initial: { opacity: 0, y: 20 }, whileInView: { opacity: 1, y: 0 }, viewport: { once: true, margin: "-100px" }, transition: { duration: 0.6, delay: 0.1 }, children: "Focus combines monitoring and blocking to help you build better digital habits" })] }), _jsx("div", { className: "grid grid-cols-1 md:grid-cols-2 gap-8", children: solutions.map((solution, index) => {
                        const IconComponent = iconComponents[solution.icon];
                        const isNormalMode = solution.mode === 'normal';
                        return (_jsxs(motion.div, { 
                              className: `${isNormalMode
                                ? 'glass-light border border-blue-200/30 dark:border-blue-500/20'
                                : 'glass-card border border-purple-200/40 dark:border-purple-500/30'} p-6 md:p-8 rounded-xl`, 
                              initial: { opacity: 0, y: 20 }, 
                              whileInView: { opacity: 1, y: 0 }, 
                              viewport: { once: true, margin: "-100px" }, 
                              transition: { duration: 0.6, delay: index * 0.2 }, 
                              children: [
                                _jsx("div", { 
                                  className: `w-16 h-16 rounded-full flex items-center justify-center mb-6 ${
                                    isNormalMode ? 'bg-secondary-100 text-secondary-600' : 'bg-primary-100 text-primary-600'
                                  }`, 
                                  children: IconComponent && _jsx(IconComponent, { size: 32 }) 
                                }), 
                                _jsx("h3", { 
                                  className: `text-2xl font-semibold mb-4 ${
                                    isNormalMode ? 'text-secondary-700 dark:text-secondary-300' : 'text-primary-700 dark:text-primary-300'
                                  }`, 
                                  children: solution.title 
                                }), 
                                _jsx("p", { 
                                  className: "text-gray-700 dark:text-gray-300 mb-6", 
                                  children: solution.description 
                                }), 
                                _jsx("div", { className: "space-y-4 mt-2", children: isNormalMode ? (_jsxs(_Fragment, { children: [_jsxs("div", { className: "flex items-center gap-3", children: [_jsx("div", { className: "w-5 h-5 rounded-full bg-secondary-200 dark:bg-secondary-800/70 flex items-center justify-center shadow-sm", children: _jsx("div", { className: "w-2 h-2 rounded-full bg-secondary-500 dark:bg-secondary-300" }) }), _jsx("span", { className: "text-gray-700 dark:text-gray-300", children: "Track app usage and patterns" })] }), _jsxs("div", { className: "flex items-center gap-3", children: [_jsx("div", { className: "w-5 h-5 rounded-full bg-secondary-200 dark:bg-secondary-800/70 flex items-center justify-center shadow-sm", children: _jsx("div", { className: "w-2 h-2 rounded-full bg-secondary-500 dark:bg-secondary-300" }) }), _jsx("span", { className: "text-gray-700 dark:text-gray-300", children: "Set daily time limits for apps" })] }), _jsxs("div", { className: "flex items-center gap-3", children: [_jsx("div", { className: "w-5 h-5 rounded-full bg-secondary-200 dark:bg-secondary-800/70 flex items-center justify-center shadow-sm", children: _jsx("div", { className: "w-2 h-2 rounded-full bg-secondary-500 dark:bg-secondary-300" }) }), _jsx("span", { className: "text-gray-700 dark:text-gray-300", children: "Get usage insights and recommendations" })] })] })) : (_jsxs(_Fragment, { children: [_jsxs("div", { className: "flex items-center gap-3", children: [_jsx("div", { className: "w-5 h-5 rounded-full bg-primary-200 dark:bg-primary-800/70 flex items-center justify-center shadow-sm", children: _jsx("div", { className: "w-2 h-2 rounded-full bg-primary-500 dark:bg-primary-300" }) }), _jsx("span", { className: "text-gray-700 dark:text-gray-300", children: "Block distracting apps and websites" })] }), _jsxs("div", { className: "flex items-center gap-3", children: [_jsx("div", { className: "w-5 h-5 rounded-full bg-primary-200 dark:bg-primary-800/70 flex items-center justify-center shadow-sm", children: _jsx("div", { className: "w-2 h-2 rounded-full bg-primary-500 dark:bg-primary-300" }) }), _jsx("span", { className: "text-gray-700 dark:text-gray-300", children: "Filter inappropriate content" })] }), _jsxs("div", { className: "flex items-center gap-3", children: [_jsx("div", { className: "w-5 h-5 rounded-full bg-primary-200 dark:bg-primary-800/70 flex items-center justify-center shadow-sm", children: _jsx("div", { className: "w-2 h-2 rounded-full bg-primary-500 dark:bg-primary-300" }) }), _jsx("span", { className: "text-gray-700 dark:text-gray-300", children: "Silence non-essential notifications" })] })] })) })] }, solution.id));
                    }) })] }) }));
};
export default SolutionSection;
