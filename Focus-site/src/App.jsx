import { jsx as _jsx, jsxs as _jsxs } from "react/jsx-runtime";
import { useEffect, useState } from 'react';
import Navbar from './components/Navbar';
import HeroSection from './components/HeroSection';
import AboutSection from './components/AboutSection';
import ProblemSection from './components/ProblemSection';
import SolutionSection from './components/SolutionSection';
import FeaturesSection from './components/FeaturesSection';
import HowItWorksSection from './components/HowItWorksSection';
import PrivacySection from './components/PrivacySection';
import PrivacyPolicy from './components/PrivacyPolicy';
import TermsOfService from './components/TermsOfService';
import AppOverview from './components/AppOverview';
import DownloadSection from './components/DownloadSection';
import Footer from './components/Footer';
import AnimatedBackground from './components/AnimatedBackground';
import ParallaxEffect from './components/ParallaxEffect';
import AOS from 'aos';
function App() {
    // State to handle which page to show
    const [currentPage, setCurrentPage] = useState('home');
    
    // Function to handle navigation
    const navigateTo = (page) => {
        // Update URL hash
        if (page === 'home') {
            // For home page, just remove the hash
            window.location.hash = '';
        } else {
            window.location.hash = page;
        }
        setCurrentPage(page);
        window.scrollTo(0, 0);
    };
    
    // Get page from URL on initial load
    useEffect(() => {
        const hash = window.location.hash.replace('#', '');
        if (hash === 'privacy-policy') setCurrentPage('privacy-policy');
        if (hash === 'terms-of-service') setCurrentPage('terms-of-service');
        if (hash === 'app-overview') setCurrentPage('app-overview');
        
        // Update URL when page changes
        const handleHashChange = () => {
            const hash = window.location.hash.replace('#', '');
            if (hash === 'privacy-policy') setCurrentPage('privacy-policy');
            else if (hash === 'terms-of-service') setCurrentPage('terms-of-service');
            else if (hash === 'app-overview') setCurrentPage('app-overview');
            else if (hash === '') setCurrentPage('home');
        };
        
        window.addEventListener('hashchange', handleHashChange);
        return () => window.removeEventListener('hashchange', handleHashChange);
    }, []);
    
    // Refresh AOS on window resize for responsive animations
    useEffect(() => {
        window.addEventListener('resize', () => {
            AOS.refresh();
        });
        
        return () => {
            window.removeEventListener('resize', () => {
                AOS.refresh();
            });
        };
    }, []);
    
    // Function to render main content
    const renderMainContent = () => {
        return (
            _jsxs("div", { 
                className: "min-h-screen", 
                children: [
                    _jsx(ParallaxEffect, {}),
                    _jsx(AnimatedBackground, {}),
                    _jsx(Navbar, { navigateTo: navigateTo }), 
                    _jsx("div", { "data-aos": "fade-up", "data-aos-duration": "1000", children: _jsx(HeroSection, {}) }),
                    _jsx("div", { "data-aos": "fade-up", "data-aos-delay": "100", children: _jsx(AboutSection, {}) }),
                    _jsx("div", { "data-aos": "fade-up", "data-aos-delay": "150", children: _jsx(ProblemSection, {}) }),
                    _jsx("div", { "data-aos": "fade-up", "data-aos-delay": "200", children: _jsx(SolutionSection, {}) }),
                    _jsx("div", { "data-aos": "fade-up", "data-aos-delay": "250", className: "relative z-10", children: _jsx(FeaturesSection, {}) }),
                    _jsx("div", { "data-aos": "fade-up", "data-aos-delay": "300", className: "relative z-10", children: _jsx(HowItWorksSection, {}) }),
                    _jsx("div", { "data-aos": "fade-up", "data-aos-delay": "350", className: "relative z-10", children: _jsx(PrivacySection, { navigateTo: navigateTo }) }),
                    _jsx("div", { "data-aos": "fade-up", "data-aos-delay": "400", children: _jsx(DownloadSection, {}) }),
                    _jsx(Footer, { navigateTo: navigateTo })
                ] 
            })
        );
    };
    
    // Render the appropriate page based on currentPage state
    const renderPage = () => {
        switch (currentPage) {
            case 'privacy-policy':
                return (
                    _jsxs("div", {
                        className: "min-h-screen",
                        children: [
                            _jsx(Navbar, { navigateTo: navigateTo }),
                            _jsx(PrivacyPolicy, {}),
                            _jsx(Footer, { navigateTo: navigateTo })
                        ]
                    })
                );
            case 'terms-of-service':
                return (
                    _jsxs("div", {
                        className: "min-h-screen",
                        children: [
                            _jsx(Navbar, { navigateTo: navigateTo }),
                            _jsx(TermsOfService, {}),
                            _jsx(Footer, { navigateTo: navigateTo })
                        ]
                    })
                );
            case 'app-overview':
                return (
                    _jsxs("div", {
                        className: "min-h-screen",
                        children: [
                            _jsx(Navbar, { navigateTo: navigateTo }),
                            _jsx(AppOverview, {}),
                            _jsx(Footer, { navigateTo: navigateTo })
                        ]
                    })
                );
            default:
                return renderMainContent();
        }
    };
    
    return renderPage();
}
export default App;
