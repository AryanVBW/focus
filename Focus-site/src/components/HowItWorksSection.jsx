import { jsx as _jsx, jsxs as _jsxs } from "react/jsx-runtime";
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
    return (_jsx("section", { id: "how-it-works", className: "section bg-white", children: _jsxs("div", { className: "container-custom", children: [_jsxs("div", { className: "section-title", children: [_jsx(motion.h2, { initial: { opacity: 0, y: 20 }, whileInView: { opacity: 1, y: 0 }, viewport: { once: true, margin: "-100px" }, transition: { duration: 0.6 }, children: "How It Works" }), _jsx(motion.p, { initial: { opacity: 0, y: 20 }, whileInView: { opacity: 1, y: 0 }, viewport: { once: true, margin: "-100px" }, transition: { duration: 0.6, delay: 0.1 }, children: "A simple yet powerful approach to digital wellness" })] }), _jsxs("div", { className: "relative", children: [_jsx("div", { className: "absolute left-4 md:left-1/2 top-0 bottom-0 w-px bg-gray-200 md:-ml-px hidden sm:block" }), _jsx("div", { className: "space-y-12", children: howItWorksSteps.map((step, index) => {
                                const IconComponent = iconComponents[step.icon];
                                const isEven = index % 2 === 0;
                                return (_jsx("div", { className: "relative", children: _jsxs(motion.div, { className: `flex flex-col sm:flex-row items-center ${isEven ? 'md:flex-row-reverse' : ''}`, initial: { opacity: 0, y: 20 }, whileInView: { opacity: 1, y: 0 }, viewport: { once: true, margin: "-100px" }, transition: { duration: 0.6, delay: index * 0.1 }, children: [_jsx("div", { className: "sm:absolute sm:left-4 md:left-1/2 sm:transform sm:-translate-x-1/2 z-10", children: _jsx("div", { className: `w-8 h-8 rounded-full border-4 border-white ${isEven ? 'bg-secondary-500' : 'bg-primary-500'} shadow-md flex items-center justify-center text-white`, children: _jsx("span", { className: "text-xs font-bold", children: index + 1 }) }) }), _jsx("div", { className: `sm:w-1/2 ${isEven ? 'md:pr-16 sm:pl-12' : 'md:pl-16 sm:pr-12'} sm:pt-0 pt-4 pl-12 sm:pl-0`, children: _jsx("div", { className: `bg-white p-6 rounded-lg shadow-subtle ${isEven ? 'border-l-4 border-secondary-500' : 'border-l-4 border-primary-500'}`, children: _jsxs("div", { className: "flex items-start", children: [_jsx("div", { className: `w-10 h-10 rounded-lg mr-4 flex items-center justify-center ${isEven ? 'bg-secondary-100 text-secondary-600' : 'bg-primary-100 text-primary-600'}`, children: IconComponent && _jsx(IconComponent, { size: 20 }) }), _jsxs("div", { children: [_jsx("h4", { className: "text-lg font-semibold mb-2", children: step.title }), _jsx("p", { className: "text-gray-600", children: step.description })] })] }) }) })] }) }, step.id));
                            }) })] })] }) }));
};
export default HowItWorksSection;
