import { jsx as _jsx, jsxs as _jsxs } from "react/jsx-runtime";
import { useEffect, useState } from 'react';
import { Link as ScrollLink } from 'react-scroll';
import { motion, useScroll, useTransform } from 'framer-motion';
import { Menu, X, Moon, Sun } from 'lucide-react';
import { navLinks } from '../data/content';
import logoImage from '../assets/focus.png';
import { useTheme } from '../context/ThemeContext';

const Navbar = ({ navigateTo = () => {} }) => {
    // Check if we're on the homepage or an external page
    const [isHomePage, setIsHomePage] = useState(true);
    
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
    const [isOpen, setIsOpen] = useState(false);
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

    useEffect(() => {
        const handleClickOutside = () => {
            if (isOpen)
                setIsOpen(false);
        };
        document.addEventListener('click', handleClickOutside);
        return () => document.removeEventListener('click', handleClickOutside);
    }, [isOpen]);

    return (
        _jsxs(motion.header, {
            className: "fixed top-0 left-0 w-full z-50 py-4",
            initial: { y: -100 },
            animate: { y: 0 },
            transition: { duration: 0.5 },
            children: [
                _jsx(motion.div, {
                    className: "mx-auto overflow-hidden",
                    style: {
                        maxWidth: capsuleMaxWidth,
                        borderRadius: capsuleBorderRadius,
                        paddingTop: capsulePaddingY,
                        paddingBottom: capsulePaddingY,
                        border: capsuleBorder,
                        backgroundColor,
                        boxShadow,
                        backdropFilter
                    },
                    children: _jsxs("div", {
                        className: "w-full flex justify-between items-center px-4",
                        children: [
                            _jsxs(ScrollLink, {
                                to: "home",
                                spy: true,
                                smooth: true,
                                duration: 500,
                                className: "flex items-center cursor-pointer",
                                children: [
                                    _jsx("img", { src: logoImage, alt: "Focus Logo", className: "mr-3 h-10 w-10 object-contain rounded-full", style: { filter: 'drop-shadow(0 0 2px rgba(124, 58, 237, 0.5))' } }),
                                    _jsx(motion.span, { className: "text-xl font-bold", style: { color: textColor }, children: "Focus" })
                                ]
                            }),
                            _jsxs("nav", {
                                className: "hidden md:flex space-x-8 items-center",
                                children: [
                                    navLinks.map((link) => (
                                        link.isExternalPage ? (
                                            _jsx("a", {
                                                href: `#${link.href}`,
                                                onClick: (e) => {
                                                    e.preventDefault();
                                                    navigateTo(link.href);
                                                },
                                                className: "cursor-pointer",
                                                children: _jsx(motion.span, {
                                                    className: "relative inline-block font-medium hover:text-primary-600 transition-colors",
                                                    style: { color: textColor },
                                                    whileHover: { y: -2 },
                                                    children: link.name
                                                })
                                            }, link.href)
                                        ) : isHomePage ? (
                                            _jsx(ScrollLink, {
                                                to: link.href,
                                                spy: true,
                                                smooth: true,
                                                offset: -80,
                                                duration: 500,
                                                className: "cursor-pointer",
                                                children: _jsx(motion.span, {
                                                    className: "relative inline-block font-medium hover:text-primary-600 transition-colors",
                                                    style: { color: textColor },
                                                    whileHover: { y: -2 },
                                                    children: link.name
                                                })
                                            }, link.href)
                                        ) : (
                                            _jsx("a", {
                                                href: "#",
                                                onClick: (e) => {
                                                    e.preventDefault();
                                                    navigateTo('home');
                                                    // Add a small delay to allow page transition before scrolling
                                                    setTimeout(() => {
                                                        const element = document.getElementById(link.href);
                                                        if (element) {
                                                            element.scrollIntoView({ behavior: 'smooth' });
                                                        }
                                                    }, 100);
                                                },
                                                className: "cursor-pointer",
                                                children: _jsx(motion.span, {
                                                    className: "relative inline-block font-medium hover:text-primary-600 transition-colors",
                                                    style: { color: textColor },
                                                    whileHover: { y: -2 },
                                                    children: link.name
                                                })
                                            }, link.href)
                                        )
                                    )),
                                    _jsx(motion.button, {
                                        className: "p-1.5 rounded-full bg-transparent hover:bg-gray-100 dark:hover:bg-slate-800/60 hover:shadow-md dark:hover:shadow-purple-900/20 transition-all relative overflow-hidden",
                                        onClick: (e) => {
                                            e.stopPropagation();
                                            toggleTheme();
                                        },
                                        whileHover: { scale: 1.05 },
                                        whileTap: { scale: 0.95 },
                                        aria: { label: "Toggle theme" },
                                        children: theme === 'dark' 
                                            ? _jsx(Sun, { className: "w-5 h-5", style: { color: textColor } }) 
                                            : _jsx(Moon, { className: "w-5 h-5", style: { color: textColor } })
                                    })
                                ]
                            }),
                            _jsx("div", {
                                className: "md:hidden",
                                onClick: (e) => {
                                    e.stopPropagation();
                                    setIsOpen(!isOpen);
                                },
                                children: _jsx("button", {
                                    className: "p-2 focus:outline-none",
                                    "aria-label": "Toggle menu",
                                    children: isOpen ? (_jsx(X, { className: "w-6 h-6", style: { color: textColor } })) : (_jsx(Menu, { className: "w-6 h-6", style: { color: textColor } }))
                                })
                            })
                        ]
                    })
                }),
                isOpen && (
                    _jsx(motion.div, {
                        className: "md:hidden absolute top-full left-0 w-full bg-white/90 dark:bg-slate-900/95 shadow-lg backdrop-blur-sm dark:shadow-slate-900/50 border-t border-gray-100 dark:border-slate-800",
                        initial: { opacity: 0, height: 0 },
                        animate: { opacity: 1, height: 'auto' },
                        exit: { opacity: 0, height: 0 },
                        transition: { duration: 0.2 },
                        onClick: (e) => e.stopPropagation(),
                        children: _jsxs("div", {
                            className: "container-custom py-4 flex flex-col",
                            children: [
                                navLinks.map((link) => (
                                    link.isExternalPage ? (
                                        _jsx("a", {
                                            href: `#${link.href}`,
                                            className: "py-3 px-4 text-gray-700 dark:text-gray-200 hover:bg-gray-50 dark:hover:bg-slate-800/70 hover:text-primary-600 dark:hover:text-primary-400 cursor-pointer",
                                            onClick: (e) => {
                                                e.preventDefault();
                                                setIsOpen(false);
                                                navigateTo(link.href);
                                            },
                                            children: link.name
                                        }, link.href)
                                    ) : isHomePage ? (
                                        _jsx(ScrollLink, {
                                            to: link.href,
                                            spy: true,
                                            smooth: true,
                                            offset: -80,
                                            duration: 500,
                                            className: "py-3 px-4 text-gray-700 dark:text-gray-200 hover:bg-gray-50 dark:hover:bg-slate-800/70 hover:text-primary-600 dark:hover:text-primary-400 cursor-pointer",
                                            onClick: () => setIsOpen(false),
                                            children: link.name
                                        }, link.href)
                                    ) : (
                                        _jsx("a", {
                                            href: "#",
                                            className: "py-3 px-4 text-gray-700 dark:text-gray-200 hover:bg-gray-50 dark:hover:bg-slate-800/70 hover:text-primary-600 dark:hover:text-primary-400 cursor-pointer",
                                            onClick: (e) => {
                                                e.preventDefault();
                                                setIsOpen(false);
                                                navigateTo('home');
                                                // Add a small delay to allow page transition before scrolling
                                                setTimeout(() => {
                                                    const element = document.getElementById(link.href);
                                                    if (element) {
                                                        element.scrollIntoView({ behavior: 'smooth' });
                                                    }
                                                }, 100);
                                            },
                                            children: link.name
                                        }, link.href)
                                    )
                                )),
                                _jsxs("div", {
                                    className: "mt-2 px-4 py-3 border-t border-gray-100 dark:border-slate-800/70 flex items-center justify-between",
                                    children: [
                                        _jsx("span", {
                                            className: "text-sm text-gray-600 dark:text-gray-400",
                                            children: theme === 'dark' ? "Switch to light mode" : "Switch to dark mode"
                                        }),
                                        _jsx("button", {
                                            className: "p-1.5 rounded-full bg-transparent hover:bg-gray-100 dark:hover:bg-slate-800/60 hover:shadow-md dark:hover:shadow-purple-900/20 transition-all",
                                            onClick: (e) => {
                                                e.stopPropagation();
                                                toggleTheme();
                                            },
                                            "aria-label": "Toggle theme",
                                            children: theme === 'dark' 
                                                ? _jsx(Sun, { className: "w-5 h-5 text-gray-700 dark:text-gray-200" }) 
                                                : _jsx(Moon, { className: "w-5 h-5 text-gray-700 dark:text-gray-200" })
                                        })
                                    ]
                                })
                            ]
                        })
                    })
                )
            ]
        })
    );
};

export default Navbar;
