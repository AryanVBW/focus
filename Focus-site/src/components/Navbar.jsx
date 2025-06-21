import { jsx as _jsx, jsxs as _jsxs } from "react/jsx-runtime";
import { useEffect, useState, useRef } from 'react';
import { Link as ScrollLink } from 'react-scroll';
import { motion, useScroll, useTransform, AnimatePresence } from 'framer-motion';
import { Menu, X, Moon, Sun, ChevronDown } from 'lucide-react';
import { navLinks } from '../data/content';
import logoImage from '../assets/focus.png';
import { useTheme } from '../context/ThemeContext';

const Navbar = ({ navigateTo = () => {} }) => {
    // Check if we're on the homepage or an external page
    const [isHomePage, setIsHomePage] = useState(true);
    const [isOpen, setIsOpen] = useState(false);
    const [focusedIndex, setFocusedIndex] = useState(-1);
    const mobileMenuRef = useRef(null);
    const lastFocusedElement = useRef(null);
    
    useEffect(() => {
        // Check the URL to determine if we're on the homepage
        const checkIfHomePage = () => {
            const hash = window.location.hash.replace('#', '');
            const externalPages = ['app-overview', 'privacy-policy', 'terms-of-service'];
            setIsHomePage(!externalPages.includes(hash));
        };
        
        // Initial check
        checkIfHomePage();
        
        // Listen for hash changes
        window.addEventListener('hashchange', checkIfHomePage);
        return () => window.removeEventListener('hashchange', checkIfHomePage);
    }, []);

    const { scrollYProgress } = useScroll();
    const { theme, toggleTheme } = useTheme();

    const lightBgStart = 'rgba(255, 255, 255, 0)';
    const lightBgEnd = 'rgba(255, 255, 255, 0.7)';
    const darkBgStart = 'rgba(2, 6, 23, 0)';
    const darkBgEnd = 'rgba(15, 23, 42, 0.85)';

    const lightShadow = '0 4px 20px rgba(0, 0, 0, 0.05)';
    const darkShadow = '0 4px 20px rgba(0, 0, 0, 0.3), 0 0 8px rgba(124, 58, 237, 0.1)';

    const lightTextStart = '#ffffff';
    const lightTextEnd = '#1f2937';
    const darkTextStart = '#f1f5f9';
    const darkTextEnd = '#e2e8f0';

    const lightBorder = '1px solid rgba(255, 255, 255, 0.3)';
    const darkBorder = '1px solid rgba(124, 58, 237, 0.2)';

    const lightBlur = 'blur(16px)';
    const darkBlur = 'blur(16px)';

    const backgroundColor = useTransform(scrollYProgress, [0, 0.05], theme === 'dark' ? [darkBgStart, darkBgEnd] : [lightBgStart, lightBgEnd]);
    const boxShadow = useTransform(scrollYProgress, [0, 0.05], theme === 'dark' ? [darkShadow, darkShadow] : ['none', lightShadow]);
    const textColor = useTransform(scrollYProgress, [0, 0.05], theme === 'dark' ? [darkTextStart, darkTextEnd] : [lightTextStart, lightTextEnd]);
    const backdropFilter = useTransform(scrollYProgress, [0, 0.05], ['none', theme === 'dark' ? darkBlur : lightBlur]);

    const capsuleMaxWidth = useTransform(scrollYProgress, [0, 0.05], ['1280px', '1152px']);
    const capsuleBorderRadius = useTransform(scrollYProgress, [0, 0.05], ['0px', '9999px']);
    const capsulePaddingY = useTransform(scrollYProgress, [0, 0.05], ['0rem', '0.5rem']);
    const capsuleBorder = useTransform(scrollYProgress, [0, 0.05], ['none', theme === 'dark' ? darkBorder : lightBorder]);

    // Enhanced mobile menu management with keyboard support
    useEffect(() => {
        const handleClickOutside = (event) => {
            if (isOpen && mobileMenuRef.current && !mobileMenuRef.current.contains(event.target)) {
                setIsOpen(false);
                setFocusedIndex(-1);
            }
        };

        const handleKeyDown = (event) => {
            if (!isOpen) return;

            switch (event.key) {
                case 'Escape':
                    setIsOpen(false);
                    setFocusedIndex(-1);
                    if (lastFocusedElement.current) {
                        lastFocusedElement.current.focus();
                    }
                    break;
                case 'ArrowDown':
                    event.preventDefault();
                    setFocusedIndex(prev => (prev + 1) % (navLinks.length + 1)); // +1 for theme toggle
                    break;
                case 'ArrowUp':
                    event.preventDefault();
                    setFocusedIndex(prev => prev <= 0 ? navLinks.length : prev - 1);
                    break;
                case 'Enter':
                case ' ':
                    if (focusedIndex >= 0 && focusedIndex < navLinks.length) {
                        event.preventDefault();
                        const link = navLinks[focusedIndex];
                        handleNavigation(link);
                    } else if (focusedIndex === navLinks.length) {
                        event.preventDefault();
                        toggleTheme();
                    }
                    break;
            }
        };

        document.addEventListener('click', handleClickOutside);
        document.addEventListener('keydown', handleKeyDown);
        
        return () => {
            document.removeEventListener('click', handleClickOutside);
            document.removeEventListener('keydown', handleKeyDown);
        };
    }, [isOpen, focusedIndex, navLinks.length, toggleTheme]);

    // Handle navigation with proper focus management
    const handleNavigation = (link) => {
        if (link.isExternalPage) {
            navigateTo(link.href);
        } else if (isHomePage) {
            const element = document.getElementById(link.href);
            if (element) {
                element.scrollIntoView({ behavior: 'smooth' });
            }
        } else {
            navigateTo('home');
            setTimeout(() => {
                const element = document.getElementById(link.href);
                if (element) {
                    element.scrollIntoView({ behavior: 'smooth' });
                }
            }, 100);
        }
        setIsOpen(false);
        setFocusedIndex(-1);
    };

    const toggleMobileMenu = () => {
        if (!isOpen) {
            lastFocusedElement.current = document.activeElement;
        }
        setIsOpen(!isOpen);
        setFocusedIndex(-1);
    };

    return (
        <motion.header
            className="fixed top-0 left-0 w-full z-50 py-4"
            initial={{ y: -100 }}
            animate={{ y: 0 }}
            transition={{ duration: 0.5 }}
        >
            <motion.div
                className="mx-auto overflow-hidden"
                style={{
                    maxWidth: capsuleMaxWidth,
                    borderRadius: capsuleBorderRadius,
                    paddingTop: capsulePaddingY,
                    paddingBottom: capsulePaddingY,
                    border: capsuleBorder,
                    backgroundColor,
                    boxShadow,
                    backdropFilter
                }}
            >
                <div className="w-full flex justify-between items-center px-4">
                    <ScrollLink
                        to="home"
                        spy={true}
                        smooth={true}
                        duration={500}
                        className="flex items-center cursor-pointer focus:outline-none focus:ring-2 focus:ring-primary-500 rounded-lg p-1"
                        tabIndex={0}
                    >
                        <img src={logoImage} alt="Focus Logo" className="mr-3 h-10 w-10 object-contain rounded-full" style={{ filter: 'drop-shadow(0 0 2px rgba(124, 58, 237, 0.5))' }} />
                        <motion.span className="text-xl font-bold sr-only md:not-sr-only" style={{ color: textColor }}>Focus</motion.span>
                    </ScrollLink>

                    {/* Desktop Navigation */}
                    <nav className="hidden md:flex space-x-8 items-center">
                        {navLinks.map((link) => (
                            link.isExternalPage ? (
                                <a
                                    key={link.href}
                                    href={`#${link.href}`}
                                    onClick={(e) => {
                                        e.preventDefault();
                                        navigateTo(link.href);
                                    }}
                                    className="cursor-pointer focus:outline-none focus:ring-2 focus:ring-primary-500 rounded-lg px-2 py-1"
                                    tabIndex={0}
                                >
                                    <motion.span
                                        className="relative inline-block font-medium hover:text-primary-600 transition-colors"
                                        style={{ color: textColor }}
                                        whileHover={{ y: -2 }}
                                    >
                                        {link.name}
                                    </motion.span>
                                </a>
                            ) : isHomePage ? (
                                <ScrollLink
                                    key={link.href}
                                    to={link.href}
                                    spy={true}
                                    smooth={true}
                                    offset={-80}
                                    duration={500}
                                    className="cursor-pointer focus:outline-none focus:ring-2 focus:ring-primary-500 rounded-lg px-2 py-1"
                                    tabIndex={0}
                                >
                                    <motion.span
                                        className="relative inline-block font-medium hover:text-primary-600 transition-colors"
                                        style={{ color: textColor }}
                                        whileHover={{ y: -2 }}
                                    >
                                        {link.name}
                                    </motion.span>
                                </ScrollLink>
                            ) : (
                                <a
                                    key={link.href}
                                    href="#"
                                    onClick={(e) => {
                                        e.preventDefault();
                                        navigateTo('home');
                                        setTimeout(() => {
                                            const element = document.getElementById(link.href);
                                            if (element) {
                                                element.scrollIntoView({ behavior: 'smooth' });
                                            }
                                        }, 100);
                                    }}
                                    className="cursor-pointer focus:outline-none focus:ring-2 focus:ring-primary-500 rounded-lg px-2 py-1"
                                    tabIndex={0}
                                >
                                    <motion.span
                                        className="relative inline-block font-medium hover:text-primary-600 transition-colors"
                                        style={{ color: textColor }}
                                        whileHover={{ y: -2 }}
                                    >
                                        {link.name}
                                    </motion.span>
                                </a>
                            )
                        ))}
                        
                        {/* Desktop Theme Toggle */}
                        <motion.button
                            className="p-1.5 rounded-full bg-transparent hover:bg-gray-100 dark:hover:bg-slate-800/60 hover:shadow-md dark:hover:shadow-purple-900/20 transition-all relative overflow-hidden focus:outline-none focus:ring-2 focus:ring-primary-500"
                            onClick={(e) => {
                                e.stopPropagation();
                                toggleTheme();
                            }}
                            whileHover={{ scale: 1.05 }}
                            whileTap={{ scale: 0.95 }}
                            aria-label={`Switch to ${theme === 'dark' ? 'light' : 'dark'} mode`}
                            tabIndex={0}
                        >
                            {theme === 'dark' 
                                ? <Sun className="w-5 h-5" style={{ color: textColor }} /> 
                                : <Moon className="w-5 h-5" style={{ color: textColor }} />
                            }
                        </motion.button>
                    </nav>

                    {/* Mobile Menu Button */}
                    <div className="md:hidden">
                        <button
                            onClick={toggleMobileMenu}
                            className="p-2 focus:outline-none focus:ring-2 focus:ring-primary-500 rounded-lg"
                            aria-label={isOpen ? "Close menu" : "Open menu"}
                            aria-expanded={isOpen}
                            tabIndex={0}
                        >
                            {isOpen ? (
                                <X className="w-6 h-6" style={{ color: textColor }} />
                            ) : (
                                <Menu className="w-6 h-6" style={{ color: textColor }} />
                            )}
                        </button>
                    </div>
                </div>
            </motion.div>

            {/* Enhanced Mobile Menu */}
            <AnimatePresence>
                {isOpen && (
                    <motion.div
                        ref={mobileMenuRef}
                        className="md:hidden absolute top-full left-0 w-full bg-white/95 dark:bg-slate-900/95 shadow-lg backdrop-blur-sm dark:shadow-slate-900/50 border-t border-gray-100 dark:border-slate-800"
                        initial={{ opacity: 0, height: 0 }}
                        animate={{ opacity: 1, height: 'auto' }}
                        exit={{ opacity: 0, height: 0 }}
                        transition={{ duration: 0.2 }}
                        role="menu"
                        aria-label="Mobile navigation menu"
                    >
                        <div className="container-custom py-4 flex flex-col">
                            {navLinks.map((link, index) => (
                                <div key={link.href}>
                                    {link.isExternalPage ? (
                                        <a
                                            href={`#${link.href}`}
                                            className={`py-3 px-4 text-gray-700 dark:text-gray-200 hover:bg-gray-50 dark:hover:bg-slate-800/70 hover:text-primary-600 dark:hover:text-primary-400 cursor-pointer block transition-colors focus:outline-none focus:bg-gray-50 dark:focus:bg-slate-800/70 ${
                                                focusedIndex === index ? 'bg-gray-50 dark:bg-slate-800/70' : ''
                                            }`}
                                            onClick={(e) => {
                                                e.preventDefault();
                                                handleNavigation(link);
                                            }}
                                            role="menuitem"
                                            tabIndex={-1}
                                        >
                                            <div className="flex items-center justify-between">
                                                {link.name}
                                                <ChevronDown className="w-4 h-4 rotate-[-90deg]" />
                                            </div>
                                        </a>
                                    ) : (
                                        <button
                                            className={`w-full py-3 px-4 text-left text-gray-700 dark:text-gray-200 hover:bg-gray-50 dark:hover:bg-slate-800/70 hover:text-primary-600 dark:hover:text-primary-400 cursor-pointer transition-colors focus:outline-none focus:bg-gray-50 dark:focus:bg-slate-800/70 ${
                                                focusedIndex === index ? 'bg-gray-50 dark:bg-slate-800/70' : ''
                                            }`}
                                            onClick={() => handleNavigation(link)}
                                            role="menuitem"
                                            tabIndex={-1}
                                        >
                                            {link.name}
                                        </button>
                                    )}
                                </div>
                            ))}
                            
                            {/* Mobile Theme Toggle */}
                            <div className="mt-2 px-4 py-3 border-t border-gray-100 dark:border-slate-800/70 flex items-center justify-between">
                                <span className="text-sm text-gray-600 dark:text-gray-400">
                                    {theme === 'dark' ? "Switch to light mode" : "Switch to dark mode"}
                                </span>
                                <button
                                    className={`p-1.5 rounded-full bg-transparent hover:bg-gray-100 dark:hover:bg-slate-800/60 hover:shadow-md dark:hover:shadow-purple-900/20 transition-all focus:outline-none focus:ring-2 focus:ring-primary-500 ${
                                        focusedIndex === navLinks.length ? 'ring-2 ring-primary-500' : ''
                                    }`}
                                    onClick={(e) => {
                                        e.stopPropagation();
                                        toggleTheme();
                                    }}
                                    aria-label={`Switch to ${theme === 'dark' ? 'light' : 'dark'} mode`}
                                    role="menuitem"
                                    tabIndex={-1}
                                >
                                    {theme === 'dark' 
                                        ? <Sun className="w-5 h-5 text-gray-700 dark:text-gray-200" /> 
                                        : <Moon className="w-5 h-5 text-gray-700 dark:text-gray-200" />
                                    }
                                </button>
                            </div>
                        </div>
                    </motion.div>
                )}
            </AnimatePresence>
        </motion.header>
    );
};

export default Navbar;
