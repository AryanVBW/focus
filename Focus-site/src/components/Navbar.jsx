import { jsx as _jsx, jsxs as _jsxs } from "react/jsx-runtime";
import { useEffect, useState } from 'react';
import { Link as ScrollLink } from 'react-scroll';
import { motion, useScroll, useTransform } from 'framer-motion';
import { Menu, X } from 'lucide-react';
import { navLinks } from '../data/content';
import logoImage from '../assets/focus.png';
const Navbar = () => {
    const [isOpen, setIsOpen] = useState(false);
    const { scrollYProgress } = useScroll();
    const backgroundColor = useTransform(scrollYProgress, [0, 0.05], ['rgba(255, 255, 255, 0)', 'rgba(255, 255, 255, 0.9)']);
    const boxShadow = useTransform(scrollYProgress, [0, 0.05], ['none', '0 4px 20px rgba(0, 0, 0, 0.05)']);
    const textColor = useTransform(scrollYProgress, [0, 0.05], ['#ffffff', '#1f2937']);
    const backdropFilter = useTransform(scrollYProgress, [0, 0.05], ['none', 'blur(10px)']);
    // Close mobile menu when clicking outside
    useEffect(() => {
        const handleClickOutside = () => {
            if (isOpen)
                setIsOpen(false);
        };
        document.addEventListener('click', handleClickOutside);
        return () => document.removeEventListener('click', handleClickOutside);
    }, [isOpen]);
    return (_jsxs(motion.header, { className: "fixed top-0 left-0 w-full z-50 py-4", style: {
            backgroundColor,
            boxShadow,
            backdropFilter
        }, initial: { y: -100 }, animate: { y: 0 }, transition: { duration: 0.5 }, children: [_jsxs("div", { className: "container-custom mx-auto flex justify-between items-center", children: [_jsxs(ScrollLink, { to: "home", spy: true, smooth: true, duration: 500, className: "flex items-center cursor-pointer", children: [_jsx("img", { src: logoImage, alt: "Focus Logo", className: "mr-3 h-10 w-10 object-contain rounded-full", style: { filter: 'drop-shadow(0 0 2px rgba(124, 58, 237, 0.5))' } }), _jsx(motion.span, { className: "text-xl font-bold", style: { color: textColor }, children: "Focus" })] }), _jsx("nav", { className: "hidden md:flex space-x-8", children: navLinks.map((link) => (_jsx(ScrollLink, { to: link.href, spy: true, smooth: true, offset: -80, duration: 500, className: "cursor-pointer", children: _jsx(motion.span, { className: "relative inline-block font-medium hover:text-primary-600 transition-colors", style: { color: textColor }, whileHover: { y: -2 }, children: link.name }) }, link.href))) }), _jsx("div", { className: "md:hidden", onClick: (e) => {
                            e.stopPropagation();
                            setIsOpen(!isOpen);
                        }, children: _jsx("button", { className: "p-2 focus:outline-none", "aria-label": "Toggle menu", children: isOpen ? (_jsx(X, { className: "w-6 h-6", style: { color: textColor } })) : (_jsx(Menu, { className: "w-6 h-6", style: { color: textColor } })) }) })] }), isOpen && (_jsx(motion.div, { className: "md:hidden absolute top-full left-0 w-full bg-white shadow-lg", initial: { opacity: 0, height: 0 }, animate: { opacity: 1, height: 'auto' }, exit: { opacity: 0, height: 0 }, transition: { duration: 0.2 }, onClick: (e) => e.stopPropagation(), children: _jsx("div", { className: "container-custom py-4 flex flex-col", children: navLinks.map((link) => (_jsx(ScrollLink, { to: link.href, spy: true, smooth: true, offset: -80, duration: 500, className: "py-3 px-4 hover:bg-gray-50 cursor-pointer", onClick: () => setIsOpen(false), children: link.name }, link.href))) }) }))] }));
};
export default Navbar;
