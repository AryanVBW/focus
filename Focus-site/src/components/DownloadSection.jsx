import { motion, AnimatePresence } from 'framer-motion';
import { GithubIcon, Download, Check, ExternalLink, Star, Users, Shield } from 'lucide-react';
import { useState, useRef } from 'react';
import { useLoadingState } from '../hooks/usePerformance';
import { useAnnouncement } from '../utils/accessibility';
import googlePlayIcon from '../assets/google-play-icon.svg';

const DownloadSection = () => {
    const [isDownloading, setIsDownloading] = useState(false);
    const [downloadProgress, setDownloadProgress] = useState(0);
    const [showSuccess, setShowSuccess] = useState(false);
    const { loading: analyticsLoading, execute: loadAnalytics } = useLoadingState();
    const announce = useAnnouncement();
    const downloadButtonRef = useRef(null);
    
    const handleDownload = async () => {
        try {
            setIsDownloading(true);
            announce('Starting download', 'polite');
            
            // Simulate download progress
            const progressInterval = setInterval(() => {
                setDownloadProgress(prev => {
                    if (prev >= 100) {
                        clearInterval(progressInterval);
                        return 100;
                    }
                    return prev + 10;
                });
            }, 100);
            
            // Start actual download
            const link = document.createElement('a');
            link.href = 'https://github.com/AryanVBW/focus/releases/download/V1/Focus_V.0.1.0.apk';
            link.download = 'Focus_V.0.1.0.apk';
            link.setAttribute('aria-label', 'Download Focus APK file');
            document.body.appendChild(link);
            link.click();
            document.body.removeChild(link);
            
            // Wait for progress to complete
            setTimeout(() => {
                setIsDownloading(false);
                setShowSuccess(true);
                setDownloadProgress(0);
                announce('Download completed successfully', 'polite');
                
                // Hide success message after 3 seconds
                setTimeout(() => {
                    setShowSuccess(false);
                }, 3000);
            }, 1500);
            
        } catch (error) {
            console.error('Download failed:', error);
            setIsDownloading(false);
            setDownloadProgress(0);
            announce('Download failed. Please try again.', 'assertive');
        }
    };

    const downloadStats = [
        {
            icon: Users,
            value: 'Coming',
            label: 'Soon',
            color: 'text-blue-400'
        },
        {
            icon: Star,
            value: 'Early',
            label: 'Access',
            color: 'text-yellow-400'
        },
        {
            icon: Shield,
            value: '100%',
            label: 'Secure',
            color: 'text-green-400'
        }
    ];    return (
        <section 
            id="download" 
            className="section relative overflow-hidden bg-gradient-to-b from-primary-800 to-primary-900 text-white"
        >
            {/* Background decoration */}
            <div className="absolute inset-0 overflow-hidden">
                <div className="absolute -top-24 -right-24 w-64 h-64 bg-primary-700 rounded-full opacity-20" />
                <div className="absolute top-1/2 -left-32 w-96 h-96 bg-primary-600 rounded-full opacity-10" />
                <div className="absolute bottom-0 right-0 w-full h-1/3 bg-gradient-to-t from-black/20 to-transparent" />
            </div>

            <div className="container-custom relative z-10">
                {/* Header */}
                <motion.div 
                    className="text-center mb-12"
                    initial={{ opacity: 0, y: 20 }}
                    whileInView={{ opacity: 1, y: 0 }}
                    viewport={{ once: true, margin: "-100px" }}
                    transition={{ duration: 0.6 }}
                >
                    <h2 className="text-3xl md:text-4xl font-bold mb-4">
                        Download Focus Today
                    </h2>
                    <p className="text-xl text-primary-100 max-w-2xl mx-auto">
                        Start your journey to digital wellness. Available for Android devices.
                    </p>
                </motion.div>

                {/* Stats */}
                <motion.div 
                    className="grid grid-cols-3 gap-4 md:gap-8 mb-12 max-w-md mx-auto"
                    initial={{ opacity: 0, y: 20 }}
                    whileInView={{ opacity: 1, y: 0 }}
                    viewport={{ once: true, margin: "-100px" }}
                    transition={{ duration: 0.6, delay: 0.1 }}
                >
                    {downloadStats.map((stat, index) => (
                        <div key={stat.label} className="text-center">
                            <div className={`${stat.color} text-2xl md:text-3xl font-bold mb-1`}>
                                {stat.value}
                            </div>
                            <div className="text-primary-200 text-sm">
                                {stat.label}
                            </div>
                        </div>
                    ))}
                </motion.div>

                {/* Download buttons */}
                <div className="flex flex-col md:flex-row gap-4 justify-center items-center mb-12">
                    {/* Google Play button */}
                    <motion.a
                        href="https://play.google.com/apps/internaltest/4701255352364772217"
                        target="_blank"
                        rel="noopener noreferrer"
                        className="w-full md:w-auto flex items-center justify-center bg-white text-primary-600 px-8 py-4 rounded-full shadow-lg hover:bg-gray-100 transition-all focus:outline-none focus:ring-2 focus:ring-white focus:ring-offset-2 focus:ring-offset-primary-600"
                        initial={{ opacity: 0, y: 20 }}
                        whileInView={{ opacity: 1, y: 0 }}
                        viewport={{ once: true, margin: "-100px" }}
                        transition={{ duration: 0.6, delay: 0.1 }}
                        whileHover={{ scale: 1.05 }}
                        whileTap={{ scale: 0.95 }}
                        aria-label="Download Focus from Google Play Store"
                    >
                        <img 
                            src={googlePlayIcon} 
                            alt="" 
                            className="w-7 h-7 mr-3" 
                            aria-hidden="true" 
                        />
                        <div className="text-left">
                            <div className="text-xs">Download on</div>
                            <div className="font-bold text-lg -mt-1">Google Play</div>
                        </div>
                    </motion.a>

                    {/* GitHub APK download button */}
                    <motion.button
                        ref={downloadButtonRef}
                        onClick={handleDownload}
                        disabled={isDownloading}
                        className={`w-full md:w-auto flex items-center justify-center 
                            bg-gradient-to-r from-primary-500 to-primary-700 text-white px-8 py-4 
                            rounded-full shadow-lg transition-all relative overflow-hidden
                            focus:outline-none focus:ring-2 focus:ring-white focus:ring-offset-2 focus:ring-offset-primary-600
                            disabled:opacity-70 disabled:cursor-not-allowed
                            ${isDownloading ? 'cursor-wait' : 'hover:shadow-xl'}
                        `}
                        initial={{ opacity: 0, y: 20 }}
                        whileInView={{ opacity: 1, y: 0 }}
                        viewport={{ once: true, margin: "-100px" }}
                        transition={{ duration: 0.6, delay: 0.2 }}
                        whileHover={!isDownloading ? { scale: 1.05 } : {}}
                        whileTap={!isDownloading ? { scale: 0.95 } : {}}
                        aria-label={isDownloading ? "Downloading APK..." : "Download APK from GitHub"}
                    >
                        {/* Progress bar */}
                        <motion.div
                            className="absolute inset-0 bg-primary-400 opacity-30"
                            initial={{ scaleX: 0 }}
                            animate={{ scaleX: downloadProgress / 100 }}
                            style={{ originX: 0 }}
                            transition={{ duration: 0.3 }}
                        />
                        
                        {/* Button content */}
                        <AnimatePresence mode="wait">
                            {showSuccess ? (
                                <motion.div
                                    key="success"
                                    className="flex items-center relative z-10"
                                    initial={{ opacity: 0, scale: 0.8 }}
                                    animate={{ opacity: 1, scale: 1 }}
                                    exit={{ opacity: 0, scale: 0.8 }}
                                    transition={{ duration: 0.3 }}
                                >
                                    <Check className="w-6 h-6 mr-3" />
                                    <div className="text-left">
                                        <div className="text-xs">Download</div>
                                        <div className="font-bold text-lg -mt-1">Complete!</div>
                                    </div>
                                </motion.div>
                            ) : isDownloading ? (
                                <motion.div
                                    key="downloading"
                                    className="flex items-center relative z-10"
                                    initial={{ opacity: 0, x: 20 }}
                                    animate={{ opacity: 1, x: 0 }}
                                    exit={{ opacity: 0, x: -20 }}
                                    transition={{ duration: 0.3 }}
                                >
                                    <motion.div
                                        animate={{ rotate: 360 }}
                                        transition={{ duration: 1, repeat: Infinity, ease: "linear" }}
                                    >
                                        <Download className="w-6 h-6 mr-3" />
                                    </motion.div>
                                    <div className="text-left">
                                        <div className="text-xs">Downloading...</div>
                                        <div className="font-bold text-lg -mt-1">{downloadProgress}%</div>
                                    </div>
                                </motion.div>
                            ) : (
                                <motion.div
                                    key="download"
                                    className="flex items-center relative z-10"
                                    initial={{ opacity: 0, x: -20 }}
                                    animate={{ opacity: 1, x: 0 }}
                                    exit={{ opacity: 0, x: 20 }}
                                    transition={{ duration: 0.3 }}
                                >
                                    <GithubIcon className="w-6 h-6 mr-3" />
                                    <div className="text-left">
                                        <div className="text-xs">Download APK from</div>
                                        <div className="font-bold text-lg -mt-1">GitHub</div>
                                    </div>
                                </motion.div>
                            )}
                        </AnimatePresence>
                    </motion.button>
                </div>

                {/* Additional info */}
                <motion.div 
                    className="text-center"
                    initial={{ opacity: 0, y: 20 }}
                    whileInView={{ opacity: 1, y: 0 }}
                    viewport={{ once: true, margin: "-100px" }}
                    transition={{ duration: 0.6, delay: 0.3 }}
                >
                    <p className="text-primary-200 text-sm mb-4">
                        Compatible with Android 7.0 and above • Free and Open Source
                    </p>
                    <div className="flex flex-wrap justify-center gap-4 text-xs text-primary-300">
                        <span className="flex items-center gap-1">
                            <Shield className="w-3 h-3" />
                            No ads or tracking
                        </span>
                        <span className="flex items-center gap-1">
                            <ExternalLink className="w-3 h-3" />
                            View source code
                        </span>
                        <span className="flex items-center gap-1">
                            <Star className="w-3 h-3" />
                            Community driven
                        </span>
                    </div>
                </motion.div>

                {/* Coming Soon Message */}
                <motion.div 
                    className="bg-primary-800/50 backdrop-blur-sm rounded-xl p-6 max-w-3xl mx-auto mt-12"
                    initial={{ opacity: 0, y: 20 }}
                    whileInView={{ opacity: 1, y: 0 }}
                    viewport={{ once: true, margin: "-100px" }}
                    transition={{ duration: 0.6, delay: 0.4 }}
                >
                    <div className="text-center">
                        <h3 className="text-xl font-medium mb-4">Be Among the First Users</h3>
                        <div className="flex justify-center mb-4">
                            {[...Array(5)].map((_, i) => (
                                <Star key={i} className="w-5 h-5 text-yellow-400 fill-current opacity-50" />
                            ))}
                        </div>
                        <blockquote className="text-primary-100 italic mb-4">
                            "Focus is currently in early access. Download now to be among our first users and help shape the future of digital wellness."
                        </blockquote>
                        <cite className="text-primary-300 text-sm">- The Focus Team</cite>
                    </div>
                </motion.div>
            </div>
        </section>
    );
};
export default DownloadSection;
