import { useState } from 'react';
import { motion } from 'framer-motion';
import { 
    GithubIcon, 
    Mail, 
    ArrowUp, 
    ExternalLink,
    Heart,
    Star,
    Users,
    Download,
    Shield,
    ChevronUp,
    MessageCircle
} from 'lucide-react';
import { Link } from 'react-scroll';
import { useMediaQuery } from '../hooks/usePerformance';
import { useAnnouncement } from '../utils/accessibility';
import logoImage from '../assets/focus.png';

const Footer = ({ navigateTo }) => {
    const [expandedSection, setExpandedSection] = useState(null);
    const isMobile = useMediaQuery('(max-width: 768px)');
    const announce = useAnnouncement();

    const scrollToTop = () => {
        window.scrollTo({ top: 0, behavior: 'smooth' });
        announce('Scrolled to top of page', 'polite');
    };

    const toggleSection = (section) => {
        if (isMobile) {
            const newState = expandedSection === section ? null : section;
            setExpandedSection(newState);
            announce(`${newState ? 'Expanded' : 'Collapsed'} ${section} section`, 'polite');
        }
    };

    const stats = [
        { icon: Download, value: '10K+', label: 'Downloads' },
        { icon: Star, value: '4.8', label: 'Rating' },
        { icon: Users, value: '5K+', label: 'Active Users' },
        { icon: Shield, value: '100%', label: 'Secure' }
    ];

    const FooterSection = ({ title, children, sectionKey }) => (
        <div className="mb-6 md:mb-0">
            <button
                onClick={() => toggleSection(sectionKey)}
                className={`
                    w-full flex items-center justify-between font-medium text-lg mb-4 text-gray-900 dark:text-white
                    ${isMobile ? 'hover:text-primary-600 transition-colors' : 'cursor-default'}
                `}
                aria-expanded={isMobile ? expandedSection === sectionKey : true}
                aria-controls={`footer-section-${sectionKey}`}
            >
                <span>{title}</span>
                {isMobile && (
                    <ChevronUp 
                        className={`w-5 h-5 transition-transform duration-300 ${
                            expandedSection === sectionKey ? 'rotate-180' : ''
                        }`}
                    />
                )}
            </button>
            <div
                id={`footer-section-${sectionKey}`}
                className={`
                    ${isMobile 
                        ? `overflow-hidden transition-all duration-300 ${
                            expandedSection === sectionKey 
                                ? 'max-h-96 opacity-100' 
                                : 'max-h-0 opacity-0'
                        }`
                        : 'block'
                    }
                `}
            >
                {children}
            </div>
        </div>
    );

    return (
        <footer className="bg-gradient-to-b from-gray-100 to-gray-200 dark:from-slate-900 dark:to-slate-800 text-gray-800 dark:text-gray-200 relative overflow-hidden">
            {/* Background decoration */}
            <div className="absolute inset-0 overflow-hidden">
                <div className="absolute top-0 left-0 w-64 h-64 bg-primary-500/5 rounded-full blur-3xl" />
                <div className="absolute bottom-0 right-0 w-96 h-96 bg-accent-500/5 rounded-full blur-3xl" />
            </div>

            <div className="relative z-10">
                {/* Stats section */}
                <div className="bg-white/50 dark:bg-slate-800/50 backdrop-blur-sm border-b border-gray-200 dark:border-gray-700">
                    <div className="container-custom py-8">
                        <div className="grid grid-cols-2 md:grid-cols-4 gap-6">
                            {stats.map((stat, index) => (
                                <motion.div
                                    key={stat.label}
                                    initial={{ opacity: 0, y: 20 }}
                                    whileInView={{ opacity: 1, y: 0 }}
                                    viewport={{ once: true }}
                                    transition={{ duration: 0.5, delay: index * 0.1 }}
                                    className="text-center"
                                >
                                    <div className="inline-flex items-center justify-center w-12 h-12 bg-primary-100 dark:bg-primary-900 text-primary-600 dark:text-primary-400 rounded-xl mb-2">
                                        <stat.icon className="w-6 h-6" />
                                    </div>
                                    <div className="text-2xl font-bold text-gray-900 dark:text-white mb-1">
                                        {stat.value}
                                    </div>
                                    <div className="text-sm text-gray-600 dark:text-gray-400">
                                        {stat.label}
                                    </div>
                                </motion.div>
                            ))}
                        </div>
                    </div>
                </div>

                {/* Main footer content */}
                <div className="container-custom pt-16 pb-8">
                    <div className="grid grid-cols-1 md:grid-cols-4 gap-8 mb-12">
                        {/* Brand section */}
                        <motion.div 
                            className="col-span-1 md:col-span-2"
                            initial={{ opacity: 0, y: 20 }}
                            whileInView={{ opacity: 1, y: 0 }}
                            viewport={{ once: true }}
                            transition={{ duration: 0.6 }}
                        >
                            <div className="flex items-center mb-6">
                                <div className="relative">
                                    <img
                                        src={logoImage}
                                        alt="Focus Logo"
                                        className="mr-3 h-12 w-12 object-contain rounded-full"
                                        style={{ filter: 'drop-shadow(0 0 4px rgba(124, 58, 237, 0.3))' }}
                                    />
                                    <div className="absolute -top-1 -right-1 w-4 h-4 bg-green-500 rounded-full border-2 border-white dark:border-gray-800" />
                                </div>
                                <div>
                                    <span className="text-2xl font-bold bg-gradient-to-r from-primary-600 to-accent-600 bg-clip-text text-transparent">
                                        Focus
                                    </span>
                                    <div className="text-xs text-gray-500 dark:text-gray-400">
                                        Digital Wellness App
                                    </div>
                                </div>
                            </div>
                            
                            <p className="text-gray-600 dark:text-gray-300 mb-6 max-w-md leading-relaxed">
                                Helping you regain control of your digital life through smart content blocking, 
                                app management, and digital wellness tools designed for mindful technology use.
                            </p>

                            {/* Social links */}
                            <div className="flex items-center gap-4 mb-6">
                                <a
                                    href="https://github.com/AryanVBW/focus"
                                    target="_blank"
                                    rel="noopener noreferrer"
                                    className="group flex items-center justify-center w-10 h-10 bg-gray-200 dark:bg-gray-700 hover:bg-primary-600 dark:hover:bg-primary-600 text-gray-600 dark:text-gray-300 hover:text-white rounded-full transition-all duration-300"
                                    aria-label="View on GitHub"
                                >
                                    <GithubIcon className="w-5 h-5 group-hover:scale-110 transition-transform" />
                                </a>
                                <a
                                    href="mailto:vivek.aryanvbw@gmail.com"
                                    className="group flex items-center justify-center w-10 h-10 bg-gray-200 dark:bg-gray-700 hover:bg-primary-600 dark:hover:bg-primary-600 text-gray-600 dark:text-gray-300 hover:text-white rounded-full transition-all duration-300"
                                    aria-label="Send email"
                                >
                                    <Mail className="w-5 h-5 group-hover:scale-110 transition-transform" />
                                </a>
                                <a
                                    href="#"
                                    className="group flex items-center justify-center w-10 h-10 bg-gray-200 dark:bg-gray-700 hover:bg-primary-600 dark:hover:bg-primary-600 text-gray-600 dark:text-gray-300 hover:text-white rounded-full transition-all duration-300"
                                    aria-label="Join community"
                                >
                                    <MessageCircle className="w-5 h-5 group-hover:scale-110 transition-transform" />
                                </a>
                            </div>

                            {/* Newsletter signup */}
                            <div className="bg-white/80 dark:bg-gray-800/80 backdrop-blur-sm rounded-xl p-4 border border-gray-200 dark:border-gray-700">
                                <h4 className="font-medium text-gray-900 dark:text-white mb-2">
                                    Stay updated
                                </h4>
                                <p className="text-sm text-gray-600 dark:text-gray-400 mb-3">
                                    Get notified about new features and updates
                                </p>
                                <div className="flex gap-2">
                                    <input
                                        type="email"
                                        placeholder="Enter your email"
                                        className="flex-1 px-3 py-2 bg-white dark:bg-gray-700 border border-gray-300 dark:border-gray-600 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-primary-500"
                                        aria-label="Email address for newsletter"
                                    />
                                    <button className="px-4 py-2 bg-primary-600 hover:bg-primary-700 text-white rounded-lg text-sm font-medium transition-colors">
                                        Subscribe
                                    </button>
                                </div>
                            </div>
                        </motion.div>

                        {/* Navigation section */}
                        <motion.div
                            initial={{ opacity: 0, y: 20 }}
                            whileInView={{ opacity: 1, y: 0 }}
                            viewport={{ once: true }}
                            transition={{ duration: 0.6, delay: 0.1 }}
                        >
                            <FooterSection title="Navigation" sectionKey="navigation">
                                <ul className="space-y-3">
                                    {['Home', 'About', 'Features', 'Privacy', 'Download'].map((item) => (
                                        <li key={item}>
                                            <Link
                                                to={item.toLowerCase()}
                                                spy={true}
                                                smooth={true}
                                                offset={-80}
                                                duration={500}
                                                className="group flex items-center text-gray-600 dark:text-gray-400 hover:text-primary-600 dark:hover:text-primary-400 transition-colors cursor-pointer"
                                            >
                                                <span className="group-hover:translate-x-1 transition-transform">
                                                    {item}
                                                </span>
                                            </Link>
                                        </li>
                                    ))}
                                </ul>
                            </FooterSection>
                        </motion.div>

                        {/* Resources section */}
                        <motion.div
                            initial={{ opacity: 0, y: 20 }}
                            whileInView={{ opacity: 1, y: 0 }}
                            viewport={{ once: true }}
                            transition={{ duration: 0.6, delay: 0.2 }}
                        >
                            <FooterSection title="Resources" sectionKey="resources">
                                <ul className="space-y-3">
                                    <li>
                                        <button
                                            onClick={(e) => { e.preventDefault(); navigateTo('privacy-policy'); }}
                                            className="group flex items-center text-gray-600 dark:text-gray-400 hover:text-primary-600 dark:hover:text-primary-400 transition-colors"
                                        >
                                            <span className="group-hover:translate-x-1 transition-transform">
                                                Privacy Policy
                                            </span>
                                        </button>
                                    </li>
                                    <li>
                                        <button
                                            onClick={(e) => { e.preventDefault(); navigateTo('terms-of-service'); }}
                                            className="group flex items-center text-gray-600 dark:text-gray-400 hover:text-primary-600 dark:hover:text-primary-400 transition-colors"
                                        >
                                            <span className="group-hover:translate-x-1 transition-transform">
                                                Terms of Service
                                            </span>
                                        </button>
                                    </li>
                                    <li>
                                        <a
                                            href="https://github.com/AryanVBW/focus/releases"
                                            target="_blank"
                                            rel="noopener noreferrer"
                                            className="group flex items-center text-gray-600 dark:text-gray-400 hover:text-primary-600 dark:hover:text-primary-400 transition-colors"
                                        >
                                            <span className="group-hover:translate-x-1 transition-transform">
                                                GitHub Releases
                                            </span>
                                            <ExternalLink className="w-3 h-3 ml-1 group-hover:scale-110 transition-transform" />
                                        </a>
                                    </li>
                                    <li>
                                        <button
                                            onClick={(e) => { e.preventDefault(); navigateTo('app-overview'); }}
                                            className="group flex items-center text-gray-600 dark:text-gray-400 hover:text-primary-600 dark:hover:text-primary-400 transition-colors"
                                        >
                                            <span className="group-hover:translate-x-1 transition-transform">
                                                App Overview
                                            </span>
                                        </button>
                                    </li>
                                </ul>
                            </FooterSection>
                        </motion.div>
                    </div>

                    {/* Bottom section */}
                    <div className="pt-8 border-t border-gray-300 dark:border-gray-700">
                        <div className="flex flex-col md:flex-row justify-between items-center gap-4">
                            <div className="text-center md:text-left">
                                <p className="text-gray-500 dark:text-gray-400 text-sm">
                                    &copy; {new Date().getFullYear()} Focus App. All rights reserved.
                                </p>
                                <p className="text-gray-600 dark:text-gray-500 text-xs mt-1 flex items-center justify-center md:justify-start gap-1">
                                    <span>Made with</span>
                                    <Heart className="w-3 h-3 text-red-500 fill-current animate-pulse" />
                                    <span>for a more mindful digital experience</span>
                                </p>
                            </div>

                            {/* Back to top button */}
                            <motion.button
                                onClick={scrollToTop}
                                className="group flex items-center gap-2 px-4 py-2 bg-primary-600 hover:bg-primary-700 text-white rounded-full transition-all duration-300 shadow-lg hover:shadow-xl"
                                whileHover={{ scale: 1.05 }}
                                whileTap={{ scale: 0.95 }}
                                aria-label="Scroll to top"
                            >
                                <span className="text-sm font-medium">Back to top</span>
                                <ArrowUp className="w-4 h-4 group-hover:-translate-y-1 transition-transform" />
                            </motion.button>
                        </div>
                    </div>
                </div>
            </div>
        </footer>
    );
};

export default Footer;
