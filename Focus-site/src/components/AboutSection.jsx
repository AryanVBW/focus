import { jsx as _jsx, jsxs as _jsxs } from "react/jsx-runtime";
import { motion } from 'framer-motion';
import { Brain } from 'lucide-react';
import { useEffect, useState } from 'react';
const AboutSection = () => {
    // State to control wave animations
    const [isWaving, setIsWaving] = useState(false);
    
    // Start waving animation when component is in view
    useEffect(() => {
        const timeout = setTimeout(() => {
            setIsWaving(true);
        }, 500);
        
        return () => clearTimeout(timeout);
    }, []);
    return (_jsx("section", { id: "about", className: "section bg-white", children: _jsxs("div", { className: "container-custom", children: [_jsxs("div", { className: "section-title", children: [_jsx(motion.h2, { initial: { opacity: 0, y: 20 }, whileInView: { opacity: 1, y: 0 }, viewport: { once: true, margin: "-100px" }, transition: { duration: 0.6 }, children: "What is Focus?" }), _jsx(motion.p, { initial: { opacity: 0, y: 20 }, whileInView: { opacity: 1, y: 0 }, viewport: { once: true, margin: "-100px" }, transition: { duration: 0.6, delay: 0.1 }, children: "A powerful tool to help you break free from digital distractions" })] }), _jsxs("div", { className: "grid grid-cols-1 md:grid-cols-2 gap-12 items-center", children: [_jsx(motion.div, { initial: { opacity: 0, x: -30 }, whileInView: { opacity: 1, x: 0 }, viewport: { once: true, margin: "-100px" }, transition: { duration: 0.6 }, children: _jsxs("div", { className: "aspect-square max-w-md mx-auto relative", children: [_jsx("div", { className: "absolute inset-0 bg-gradient-to-br from-primary-100 to-primary-50 rounded-full" }), _jsxs("div", { className: "absolute inset-0 flex items-center justify-center", children: [
                                    /* Wave animations - multiple ripples */
                                    isWaving && [
                                        /* Larger outer wave with glow effect */
                                        _jsx(motion.div, { 
                                            key: "wave1",
                                            className: "absolute w-36 h-36 rounded-full border-[3px] border-primary-400/80 dark:border-purple-400/90 shadow-[0_0_15px_rgba(139,92,246,0.5)] dark:shadow-[0_0_20px_rgba(167,139,250,0.6)]", 
                                            initial: { scale: 0.7, opacity: 0.9 },
                                            animate: { scale: 2, opacity: 0 },
                                            transition: { 
                                                repeat: Infinity, 
                                                duration: 1.8, /* Faster animation */
                                                ease: "easeOut",
                                                repeatDelay: 0 
                                            }
                                        }),
                                        /* Mid wave with higher opacity */
                                        _jsx(motion.div, { 
                                            key: "wave2",
                                            className: "absolute w-36 h-36 rounded-full border-[3px] border-primary-500/90 dark:border-purple-300/90 shadow-[0_0_10px_rgba(139,92,246,0.5)] dark:shadow-[0_0_15px_rgba(167,139,250,0.6)]", 
                                            initial: { scale: 0.75, opacity: 0.9 },
                                            animate: { scale: 1.7, opacity: 0 },
                                            transition: { 
                                                repeat: Infinity, 
                                                duration: 1.8,
                                                delay: 0.3, /* Shorter delay */
                                                ease: "easeOut",
                                                repeatDelay: 0 
                                            }
                                        }),
                                        /* Inner wave with color gradient */
                                        _jsx(motion.div, { 
                                            key: "wave3",
                                            className: "absolute w-36 h-36 rounded-full border-[3px] border-primary-600/90 dark:border-violet-300/90 shadow-[0_0_8px_rgba(139,92,246,0.6)] dark:shadow-[0_0_12px_rgba(167,139,250,0.7)]", 
                                            initial: { scale: 0.8, opacity: 0.9 },
                                            animate: { scale: 1.4, opacity: 0 },
                                            transition: { 
                                                repeat: Infinity, 
                                                duration: 1.8,
                                                delay: 0.6,
                                                ease: "easeOut",
                                                repeatDelay: 0 
                                            }
                                        }),
                                        /* Close wave with intense glow */
                                        _jsx(motion.div, { 
                                            key: "wave4",
                                            className: "absolute w-36 h-36 rounded-full border-[3px] border-primary-700/90 dark:border-violet-200/90 shadow-[0_0_12px_rgba(139,92,246,0.7)] dark:shadow-[0_0_15px_rgba(167,139,250,0.8)]", 
                                            initial: { scale: 0.85, opacity: 0.95 },
                                            animate: { scale: 1.2, opacity: 0 },
                                            transition: { 
                                                repeat: Infinity, 
                                                duration: 1.8,
                                                delay: 0.9,
                                                ease: "easeOut",
                                                repeatDelay: 0 
                                            }
                                        })
                                    ],
                                    /* Brain icon with enhanced pulsing and movement animation */
                                    _jsx(motion.div, { 
                                        className: "w-32 h-32 bg-gradient-to-br from-primary-500 to-primary-700 dark:from-violet-500 dark:to-purple-800 rounded-full flex items-center justify-center text-white relative z-10 shadow-[0_0_20px_rgba(124,58,237,0.6)] dark:shadow-[0_0_25px_rgba(139,92,246,0.7)]", 
                                        animate: { 
                                            scale: [1, 1.08, 0.98, 1.04, 1],
                                            rotate: [0, 2, -2, 1, 0]
                                        }, 
                                        transition: { 
                                            repeat: Infinity, 
                                            duration: 3,
                                            times: [0, 0.25, 0.5, 0.75, 1],
                                            ease: "easeInOut"
                                        }, 
                                        whileHover: {
                                            scale: 1.1,
                                            boxShadow: "0 0 30px rgba(124,58,237,0.8)"
                                        },
                                        children: _jsx(motion.div, {
                                            animate: { 
                                                rotateY: [0, 15, -15, 10, -10, 0],
                                                y: [0, -5, 5, -3, 3, 0],
                                                x: [0, 3, -3, 2, -2, 0]
                                            },
                                            transition: { 
                                                repeat: Infinity, 
                                                duration: 5,
                                                ease: "easeInOut" 
                                            },
                                            className: "filter drop-shadow-lg",
                                            children: _jsx(Brain, { 
                                                size: 64,
                                                className: "filter drop-shadow-md" 
                                            })
                                        })
                                    })
                                ]})] }) }), _jsxs(motion.div, { initial: { opacity: 0, x: 30 }, whileInView: { opacity: 1, x: 0 }, viewport: { once: true, margin: "-100px" }, transition: { duration: 0.6, delay: 0.2 }, children: [_jsx("h3", { className: "text-2xl font-semibold mb-4 text-gray-800", children: "Digital Wellness, Reimagined" }), _jsx("p", { className: "text-gray-600 mb-6", children: "Focus is an Android app designed to help you regain control of your digital life. In a world of constant notifications and endless scrolling, Focus helps you establish healthier technology habits." }), _jsx("p", { className: "text-gray-600 mb-6", children: "Our app intelligently blocks distracting content, filters out adult material, and gives you control over which apps can interrupt your focus time. It's not about completely disconnecting\u2014it's about creating a healthier relationship with technology." }), _jsxs("div", { className: "flex flex-col gap-4", children: [_jsxs("div", { className: "flex items-center gap-3", children: [_jsx("span", { className: "w-2 h-2 bg-primary-500 rounded-full" }), _jsx("p", { className: "text-gray-700", children: "Monitor and understand your digital habits" })] }), _jsxs("div", { className: "flex items-center gap-3", children: [_jsx("span", { className: "w-2 h-2 bg-primary-500 rounded-full" }), _jsx("p", { className: "text-gray-700", children: "Block distractions during important work" })] }), _jsxs("div", { className: "flex items-center gap-3", children: [_jsx("span", { className: "w-2 h-2 bg-primary-500 rounded-full" }), _jsx("p", { className: "text-gray-700", children: "Create healthier digital boundaries" })] })] })] })] })] }) }));
};
export default AboutSection;
