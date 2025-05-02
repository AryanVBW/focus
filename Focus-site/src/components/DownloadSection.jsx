import { jsx as _jsx, jsxs as _jsxs } from "react/jsx-runtime";
import { motion, AnimatePresence } from 'framer-motion';
import { GithubIcon, Download, Check } from 'lucide-react';
import { useState } from 'react';
import googlePlayIcon from '../assets/google-play-icon.svg';
const DownloadSection = () => {
    const [isDownloading, setIsDownloading] = useState(false);
    
    const handleDownload = () => {
        setIsDownloading(true);
        
        // Start download
        const link = document.createElement('a');
        link.href = 'https://github.com/AryanVBW/focus/releases/download/V1/Focus_V.0.1.0.apk';
        link.download = 'Focus_V.0.1.0.apk';
        document.body.appendChild(link);
        link.click();
        document.body.removeChild(link);
        
        // Reset button state after animation
        setTimeout(() => {
            setIsDownloading(false);
        }, 3000);
    };
    return (_jsxs("section", { id: "download", className: "section relative overflow-hidden bg-gradient-to-b from-primary-800 to-primary-900 text-white", children: [_jsxs("div", { className: "absolute inset-0 overflow-hidden", children: [_jsx("div", { className: "absolute -top-24 -right-24 w-64 h-64 bg-primary-700 rounded-full opacity-20" }), _jsx("div", { className: "absolute top-1/2 -left-32 w-96 h-96 bg-primary-600 rounded-full opacity-10" }), _jsx("div", { className: "absolute bottom-0 right-0 w-full h-1/3 bg-gradient-to-t from-black/20 to-transparent" })] }), _jsxs("div", { className: "container-custom relative z-10", children: [_jsxs(motion.div, { className: "text-center mb-12", initial: { opacity: 0, y: 20 }, whileInView: { opacity: 1, y: 0 }, viewport: { once: true, margin: "-100px" }, transition: { duration: 0.6 }, children: [_jsx("h2", { className: "text-3xl md:text-4xl font-bold mb-4", children: "Get Focus Today" }), _jsx("p", { className: "text-primary-200 max-w-2xl mx-auto", children: "Start your journey to a healthier digital life. Download Focus now and take back control of your screen time." })] }), _jsxs("div", { className: "flex flex-col md:flex-row items-center justify-center gap-8 mb-16", children: [
                _jsxs(motion.a, { 
                    href: "https://play.google.com/apps/internaltest/4701255352364772217", 
                    target: "_blank", 
                    rel: "noopener noreferrer", 
                    className: "w-full md:w-auto flex items-center justify-center bg-white text-primary-600 px-8 py-4 rounded-full shadow-lg hover:bg-gray-100 transition-colors", 
                    initial: { opacity: 0, y: 20 }, 
                    whileInView: { opacity: 1, y: 0 }, 
                    viewport: { once: true, margin: "-100px" }, 
                    transition: { duration: 0.6, delay: 0.1 }, 
                    whileHover: { scale: 1.05 }, 
                    whileTap: { scale: 0.95 }, 
                    children: [
                        _jsx("img", { src: googlePlayIcon, alt: "Google Play", className: "w-7 h-7 mr-3" }), 
                        _jsxs("div", { className: "text-left", children: [
                            _jsx("div", { className: "text-xs", children: "Download on" }), 
                            _jsx("div", { className: "font-bold text-lg -mt-1", children: "Google Play" })
                        ] })
                    ] 
                }),
                _jsxs(motion.a, { 
                    href: "https://github.com/AryanVBW/focus/releases/download/V1/Focus_V.0.1.0.apk", 
                    target: "_blank", 
                    rel: "noopener noreferrer", 
                    className: "w-full md:w-auto flex items-center justify-center border-2 border-white bg-transparent text-white px-8 py-4 rounded-full hover:bg-white/10 transition-colors", 
                    initial: { opacity: 0, y: 20 }, 
                    whileInView: { opacity: 1, y: 0 }, 
                    viewport: { once: true, margin: "-100px" }, 
                    transition: { duration: 0.6, delay: 0.2 }, 
                    whileHover: { scale: 1.05 }, 
                    whileTap: { scale: 0.95 }, 
                    children: [
                        _jsx(GithubIcon, { className: "w-6 h-6 mr-3" }), 
                        _jsxs("div", { className: "text-left", children: [
                            _jsx("div", { className: "text-xs", children: "Download APK from" }), 
                            _jsx("div", { className: "font-bold text-lg -mt-1", children: "GitHub" })
                        ] })
                    ] 
                }),
                _jsxs(motion.a, {
                    href: "https://github.com/AryanVBW/focus/releases/download/V1/Focus_V.0.1.0.apk",
                    onClick: (e) => {
                        e.preventDefault(); // Prevent default to handle the animation
                        handleDownload();
                    },
                    download: "Focus_V.0.1.0.apk",
                    target: "_blank", 
                    rel: "noopener noreferrer",
                    className: `w-full md:w-auto flex items-center justify-center 
                        bg-gradient-to-r from-primary-500 to-primary-700 text-white px-8 py-4 
                        rounded-full shadow-lg transition-all relative overflow-hidden
                        ${isDownloading ? 'bg-primary-600' : ''}
                    `,
                    initial: { opacity: 0, y: 20 },
                    whileInView: { opacity: 1, y: 0 },
                    viewport: { once: true, margin: "-100px" },
                    transition: { duration: 0.6, delay: 0.3 },
                    whileHover: { scale: 1.05, boxShadow: '0 0 15px rgba(84, 105, 212, 0.5)' },
                    whileTap: { scale: 0.95 },
                    children: [
                        _jsx(motion.span, {
                            className: "absolute inset-0 bg-gradient-to-r from-primary-400 to-primary-600 opacity-0",
                            initial: { opacity: 0 },
                            animate: isDownloading ? { opacity: 0.3 } : { opacity: 0 },
                            exit: { opacity: 0 },
                            transition: { duration: 0.5 }
                        }),
                        _jsx(AnimatePresence, {
                            initial: false,
                            mode: "wait",
                            children: isDownloading 
                                ? _jsx(motion.div, {
                                    key: "downloading",
                                    className: "flex items-center",
                                    initial: { opacity: 0, x: 20 },
                                    animate: { opacity: 1, x: 0 },
                                    exit: { opacity: 0, x: -20 },
                                    transition: { duration: 0.3 },
                                    children: [
                                        _jsx(Check, { className: "w-6 h-6 mr-3 text-white" }),
                                        _jsx("span", { className: "font-bold", children: "Downloading..." })
                                    ]
                                })
                                : _jsxs(motion.div, {
                                    key: "download",
                                    className: "flex items-center",
                                    initial: { opacity: 0, x: -20 },
                                    animate: { opacity: 1, x: 0 },
                                    exit: { opacity: 0, x: 20 },
                                    transition: { duration: 0.3 },
                                    children: [
                                        _jsx(Download, { className: "w-6 h-6 mr-3 text-white" }),
                                        _jsx("span", { className: "font-bold", children: "Get Latest Version" })
                                    ]
                                })
                        })
                    ]
                })
            ] }), _jsx(motion.div, { className: "bg-primary-800/50 backdrop-blur-sm rounded-xl p-6 max-w-3xl mx-auto", initial: { opacity: 0, y: 20 }, whileInView: { opacity: 1, y: 0 }, viewport: { once: true, margin: "-100px" }, transition: { duration: 0.6, delay: 0.3 }, children: _jsxs("div", { className: "text-center", children: [_jsx("h4", { className: "text-xl font-medium mb-4", children: "What users are saying" }), _jsx("div", { className: "flex justify-center mb-4", children: [...Array(5)].map((_, i) => (_jsx("svg", { className: "w-5 h-5 text-yellow-400", fill: "currentColor", viewBox: "0 0 20 20", xmlns: "http://www.w3.org/2000/svg", children: _jsx("path", { d: "M9.049 2.927c.3-.921 1.603-.921 1.902 0l1.07 3.292a1 1 0 00.95.69h3.462c.969 0 1.371 1.24.588 1.81l-2.8 2.034a1 1 0 00-.364 1.118l1.07 3.292c.3.921-.755 1.688-1.54 1.118l-2.8-2.034a1 1 0 00-1.175 0l-2.8 2.034c-.784.57-1.838-.197-1.539-1.118l1.07-3.292a1 1 0 00-.364-1.118L2.98 8.72c-.783-.57-.38-1.81.588-1.81h3.461a1 1 0 00.951-.69l1.07-3.292z" }) }, i))) }), _jsx("blockquote", { className: "italic text-primary-100", children: "\"Focus has completely transformed how I use my phone. I'm more productive, less distracted, and much more mindful of my screen time.\"" }), _jsx("p", { className: "mt-4 text-primary-300 text-sm", children: "\u2014 curiouscoder-cmd" })] }) })] })] }));
};
export default DownloadSection;
