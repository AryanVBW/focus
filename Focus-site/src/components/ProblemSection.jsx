import { jsx as _jsx, jsxs as _jsxs } from "react/jsx-runtime";
import { motion } from 'framer-motion';
import { Brain, Clock, Moon, Users } from 'lucide-react';
import { problems } from '../data/content';
const iconComponents = {
    brain: Brain,
    clock: Clock,
    moon: Moon,
    users: Users,
};
const ProblemSection = () => {
    return (_jsx("section", { id: "problem", className: "section bg-gray-50", children: _jsxs("div", { className: "container-custom", children: [_jsxs("div", { className: "section-title", children: [_jsx(motion.h2, { initial: { opacity: 0, y: 20 }, whileInView: { opacity: 1, y: 0 }, viewport: { once: true, margin: "-100px" }, transition: { duration: 0.6 }, children: "The Problem" }), _jsx(motion.p, { initial: { opacity: 0, y: 20 }, whileInView: { opacity: 1, y: 0 }, viewport: { once: true, margin: "-100px" }, transition: { duration: 0.6, delay: 0.1 }, children: "Digital devices are designed to capture and keep our attention" })] }), _jsx("div", { className: "grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6", children: problems.map((problem, index) => {
                        const IconComponent = iconComponents[problem.icon];
                        return (_jsxs(motion.div, { className: "glass-card hover:shadow-lg transition-all", initial: { opacity: 0, y: 20 }, whileInView: { opacity: 1, y: 0 }, viewport: { once: true, margin: "-100px" }, transition: { duration: 0.5, delay: index * 0.1 }, whileHover: { y: -5, scale: 1.02 }, children: [_jsx("div", { className: "w-12 h-12 bg-primary-100 dark:bg-primary-900/40 rounded-full flex items-center justify-center text-primary-600 dark:text-primary-400 mb-4", children: IconComponent && _jsx(IconComponent, { size: 24 }) }), _jsx("h4", { className: "text-lg font-semibold mb-2", children: problem.title }), _jsx("p", { className: "text-gray-600 dark:text-gray-300", children: problem.description })] }, problem.id));
                    }) }), _jsxs(motion.div, { className: "mt-12 text-center glass-light p-6 rounded-lg", initial: { opacity: 0, y: 20 }, whileInView: { opacity: 1, y: 0 }, viewport: { once: true, margin: "-100px" }, transition: { duration: 0.6, delay: 0.5 }, children: [_jsx("p", { className: "text-gray-700 italic", children: "\"The average person checks their phone 96 times a day \u2014 once every 10 minutes.\"" }), _jsx("p", { className: "text-gray-500 text-sm mt-2", children: "\u2014 Digital wellness research, 2023" })] })] }) }));
};
export default ProblemSection;
