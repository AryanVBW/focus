import { motion } from 'framer-motion';
import { GithubIcon, ArrowRight, Play, Star } from 'lucide-react';
import { useState } from 'react';
import { useMediaQuery } from '../hooks/usePerformance';
import { useAnnouncement } from '../utils/accessibility';
import googlePlayIcon from '../assets/google-play-icon.svg';

const HeroSection = () => {
    const [isVideoPlaying, setIsVideoPlaying] = useState(false);
    const isMobile = useMediaQuery('(max-width: 768px)');
    const announce = useAnnouncement();

    const handleVideoPlay = () => {
        setIsVideoPlaying(true);
        announce('Playing product demo video', 'polite');
    };

    const scrollToDownload = () => {
        document.getElementById('download')?.scrollIntoView({ 
            behavior: 'smooth',
            block: 'start'
        });
        announce('Scrolling to download section', 'polite');
    };

    return (
        <section 
            id="home" 
            className="relative min-h-screen flex items-center overflow-hidden"
            aria-label="Hero section"
        >
            {/* Background gradient */}
            <div className="absolute inset-0 bg-gradient-to-br from-primary-900 via-primary-800 to-secondary-800 z-0" />
            
            {/* Animated background pattern */}
            <div className="absolute inset-0 overflow-hidden z-0">
                <div className="absolute inset-0 opacity-10">
                    <motion.div
                        className="absolute inset-0"
                        animate={{
                            background: [
                                'radial-gradient(circle at 20% 80%, rgba(255,255,255,0.1) 0%, transparent 50%)',
                                'radial-gradient(circle at 80% 20%, rgba(255,255,255,0.1) 0%, transparent 50%)',
                                'radial-gradient(circle at 40% 40%, rgba(255,255,255,0.1) 0%, transparent 50%)'
                            ]
                        }}
                        transition={{
                            duration: 8,
                            repeat: Infinity,
                            ease: "linear"
                        }}
                    />
                </div>
            </div>

            {/* Floating elements */}
            <div className="absolute inset-0 z-0">
                <motion.div
                    className="absolute top-20 left-10 w-2 h-2 bg-primary-300 rounded-full"
                    animate={{
                        y: [-10, 10, -10],
                        opacity: [0.5, 1, 0.5]
                    }}
                    transition={{ duration: 4, repeat: Infinity }}
                />
                <motion.div
                    className="absolute top-1/3 right-20 w-3 h-3 bg-accent-400 rounded-full"
                    animate={{
                        y: [10, -10, 10],
                        opacity: [0.3, 0.8, 0.3]
                    }}
                    transition={{ duration: 6, repeat: Infinity, delay: 1 }}
                />
                <motion.div
                    className="absolute bottom-1/4 left-1/4 w-1 h-1 bg-primary-200 rounded-full"
                    animate={{
                        scale: [1, 1.5, 1],
                        opacity: [0.4, 0.9, 0.4]
                    }}
                    transition={{ duration: 5, repeat: Infinity, delay: 2 }}
                />
            </div>

            <div className="container-custom relative z-10 pt-20 pb-12 md:pt-0 md:pb-24">
                <div className="grid grid-cols-1 lg:grid-cols-2 gap-8 lg:gap-12 items-center">
                    {/* Content */}
                    <motion.div
                        initial={{ opacity: 0, y: 30 }}
                        animate={{ opacity: 1, y: 0 }}
                        transition={{ duration: 0.8 }}
                        className="text-center lg:text-left order-2 lg:order-1"
                    >
                        {/* Badge */}
                        <motion.div
                            initial={{ opacity: 0, scale: 0.8 }}
                            animate={{ opacity: 1, scale: 1 }}
                            transition={{ duration: 0.6, delay: 0.2 }}
                            className="inline-flex items-center gap-2 bg-white/10 backdrop-blur-sm text-white px-4 py-2 rounded-full text-sm font-medium mb-6"
                        >
                            <Star className="w-4 h-4 text-yellow-400 fill-current" />
                            <span>4.8 Rating • 10K+ Downloads</span>
                        </motion.div>

                        {/* Main heading */}
                        <h1 className="text-4xl sm:text-5xl lg:text-6xl xl:text-7xl font-bold text-white mb-6 leading-tight">
                            <motion.span
                                initial={{ opacity: 0, y: 20 }}
                                animate={{ opacity: 1, y: 0 }}
                                transition={{ duration: 0.8, delay: 0.3 }}
                                className="block"
                            >
                                Regain control of
                            </motion.span>
                            <motion.span
                                initial={{ opacity: 0, y: 20 }}
                                animate={{ opacity: 1, y: 0 }}
                                transition={{ duration: 0.8, delay: 0.5 }}
                                className="block text-transparent bg-clip-text bg-gradient-to-r from-primary-300 to-accent-400"
                            >
                                your digital life.
                            </motion.span>
                        </h1>

                        {/* Description */}
                        <motion.p
                            initial={{ opacity: 0, y: 20 }}
                            animate={{ opacity: 1, y: 0 }}
                            transition={{ duration: 0.8, delay: 0.7 }}
                            className="text-gray-200 text-lg sm:text-xl mb-8 max-w-lg mx-auto lg:mx-0 leading-relaxed"
                        >
                            Focus is the smart content blocker that helps you stay productive, 
                            reduce screen time, and build healthier digital habits.
                        </motion.p>

                        {/* Action buttons */}
                        <motion.div
                            initial={{ opacity: 0, y: 20 }}
                            animate={{ opacity: 1, y: 0 }}
                            transition={{ duration: 0.8, delay: 0.9 }}
                            className="flex flex-col sm:flex-row gap-4 justify-center lg:justify-start items-center"
                        >
                            <motion.button
                                onClick={scrollToDownload}
                                className="group bg-white text-primary-600 hover:bg-gray-100 px-8 py-4 rounded-full font-semibold text-lg flex items-center gap-3 transition-all duration-300 shadow-lg hover:shadow-xl w-full sm:w-auto justify-center"
                                whileHover={{ scale: 1.05 }}
                                whileTap={{ scale: 0.95 }}
                                aria-label="Download Focus app"
                            >
                                <img 
                                    src={googlePlayIcon} 
                                    alt="" 
                                    className="w-6 h-6" 
                                    aria-hidden="true"
                                />
                                <span>Download App</span>
                                <ArrowRight className="w-5 h-5 group-hover:translate-x-1 transition-transform" />
                            </motion.button>

                            <motion.button
                                onClick={handleVideoPlay}
                                className="group bg-transparent text-white border-2 border-white/30 hover:border-white/60 hover:bg-white/10 px-8 py-4 rounded-full font-semibold text-lg flex items-center gap-3 transition-all duration-300 w-full sm:w-auto justify-center backdrop-blur-sm"
                                whileHover={{ scale: 1.05 }}
                                whileTap={{ scale: 0.95 }}
                                aria-label="Watch demo video"
                            >
                                <Play className="w-5 h-5 group-hover:scale-110 transition-transform" />
                                <span>Watch Demo</span>
                            </motion.button>
                        </motion.div>

                        {/* Social proof */}
                        <motion.div
                            initial={{ opacity: 0, y: 20 }}
                            animate={{ opacity: 1, y: 0 }}
                            transition={{ duration: 0.8, delay: 1.1 }}
                            className="mt-8 flex items-center justify-center lg:justify-start gap-6 text-gray-300"
                        >
                            <div className="flex items-center gap-2">
                                <div className="flex -space-x-2">
                                    {[...Array(3)].map((_, i) => (
                                        <div
                                            key={i}
                                            className="w-8 h-8 bg-gradient-to-br from-primary-300 to-primary-500 rounded-full border-2 border-white"
                                        />
                                    ))}
                                </div>
                                <span className="text-sm">Join 10K+ users</span>
                            </div>
                            <div className="h-4 w-px bg-gray-500" />
                            <div className="flex items-center gap-1">
                                <Star className="w-4 h-4 text-yellow-400 fill-current" />
                                <span className="text-sm">Open Source</span>
                            </div>
                        </motion.div>
                    </motion.div>

                    {/* Phone mockup - Enhanced for mobile */}
                    <motion.div
                        className="order-1 lg:order-2 flex justify-center"
                        initial={{ opacity: 0, scale: 0.8, rotateY: -15 }}
                        animate={{ opacity: 1, scale: 1, rotateY: 0 }}
                        transition={{ duration: 1, delay: 0.4 }}
                    >
                        <div className="relative">
                            {/* Phone frame */}
                            <motion.div
                                className="relative w-64 sm:w-72 lg:w-80 aspect-[9/16] rounded-[32px] sm:rounded-[40px] border-4 sm:border-8 border-gray-800 overflow-hidden shadow-2xl bg-gray-900"
                                animate={{
                                    y: [-8, 8, -8],
                                    rotateY: [-2, 2, -2]
                                }}
                                transition={{
                                    duration: 6,
                                    repeat: Infinity,
                                    ease: "easeInOut"
                                }}
                            >
                                {/* Screen content */}
                                <div className="absolute inset-0 bg-gradient-to-b from-primary-600 to-primary-800">
                                    {/* Status bar */}
                                    <div className="absolute top-0 left-0 right-0 h-16 sm:h-20 bg-primary-700/50 backdrop-blur-sm flex items-end justify-center pb-3">
                                        <div className="w-24 sm:w-32 h-4 sm:h-6 bg-primary-600 rounded-lg opacity-80" />
                                        <div className="absolute right-3 sm:right-4 bottom-3 sm:bottom-4 w-8 sm:w-10 h-8 sm:h-10 bg-primary-500 rounded-full flex items-center justify-center">
                                            <div className="w-4 sm:w-5 h-4 sm:h-5 bg-white rounded-full" />
                                        </div>
                                    </div>

                                    {/* App grid */}
                                    <div className="absolute top-16 sm:top-20 inset-x-0 bottom-0 p-3 sm:p-4">
                                        <div className="grid grid-cols-2 gap-3 sm:gap-4 h-full">
                                            {[...Array(6)].map((_, i) => (
                                                <motion.div
                                                    key={i}
                                                    className="bg-primary-500/40 backdrop-blur-sm rounded-lg sm:rounded-xl p-3 sm:p-4 flex items-center justify-center relative overflow-hidden"
                                                    initial={{ opacity: 0, scale: 0.8 }}
                                                    animate={{ opacity: 1, scale: 1 }}
                                                    transition={{ 
                                                        duration: 0.5, 
                                                        delay: 1.2 + (i * 0.1) 
                                                    }}
                                                >
                                                    <div className="w-8 sm:w-10 h-8 sm:h-10 bg-white/20 rounded-lg" />
                                                    <motion.div
                                                        className="absolute inset-0 bg-gradient-to-r from-transparent via-white/10 to-transparent"
                                                        animate={{ x: [-100, 100] }}
                                                        transition={{
                                                            duration: 2,
                                                            repeat: Infinity,
                                                            delay: i * 0.3,
                                                            ease: "easeInOut"
                                                        }}
                                                    />
                                                </motion.div>
                                            ))}
                                        </div>
                                    </div>
                                </div>

                                {/* Screen reflection */}
                                <div className="absolute inset-0 bg-gradient-to-br from-white/10 via-transparent to-transparent pointer-events-none" />
                            </motion.div>

                            {/* Floating UI elements */}
                            <motion.div
                                className="absolute -bottom-4 -left-6 bg-green-500 text-white px-3 py-2 rounded-lg text-xs font-medium shadow-lg"
                                initial={{ opacity: 0, scale: 0.8, y: 20 }}
                                animate={{ opacity: 1, scale: 1, y: 0 }}
                                transition={{ duration: 0.6, delay: 1.8 }}
                            >
                                ✓ 2h saved today
                            </motion.div>

                            <motion.div
                                className="absolute -top-2 -right-6 bg-blue-500 text-white px-3 py-2 rounded-lg text-xs font-medium shadow-lg"
                                initial={{ opacity: 0, scale: 0.8, y: -20 }}
                                animate={{ opacity: 1, scale: 1, y: 0 }}
                                transition={{ duration: 0.6, delay: 2 }}
                            >
                                📱 Focus Mode ON
                            </motion.div>

                            {/* Glow effects */}
                            <div className="absolute -bottom-8 -left-8 w-16 h-16 bg-accent-500 rounded-full opacity-60 blur-xl animate-pulse" />
                            <div className="absolute -top-8 -right-8 w-20 h-20 bg-primary-300 rounded-full opacity-40 blur-xl animate-pulse" />
                        </div>
                    </motion.div>
                </div>
            </div>

            {/* Scroll indicator */}
            <motion.div
                initial={{ opacity: 0 }}
                animate={{ opacity: 1 }}
                transition={{ duration: 1, delay: 2 }}
                className="absolute bottom-8 left-1/2 transform -translate-x-1/2 hidden md:block"
            >
                <motion.div
                    animate={{ y: [0, 10, 0] }}
                    transition={{ duration: 2, repeat: Infinity }}
                    className="flex flex-col items-center text-white/60"
                >
                    <span className="text-sm mb-2">Scroll to explore</span>
                    <div className="w-6 h-10 border-2 border-white/40 rounded-full flex justify-center">
                        <motion.div
                            animate={{ y: [0, 12, 0] }}
                            transition={{ duration: 2, repeat: Infinity }}
                            className="w-1 h-3 bg-white/60 rounded-full mt-2"
                        />
                    </div>
                </motion.div>
            </motion.div>
        </section>
    );
};
export default HeroSection;
