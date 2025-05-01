import { jsx as _jsx, jsxs as _jsxs } from "react/jsx-runtime";
import { useEffect } from 'react';
import Navbar from './components/Navbar';
import HeroSection from './components/HeroSection';
import AboutSection from './components/AboutSection';
import ProblemSection from './components/ProblemSection';
import SolutionSection from './components/SolutionSection';
import FeaturesSection from './components/FeaturesSection';
import HowItWorksSection from './components/HowItWorksSection';
import PrivacySection from './components/PrivacySection';
import DownloadSection from './components/DownloadSection';
import Footer from './components/Footer';
import AnimatedBackground from './components/AnimatedBackground';
import ParallaxEffect from './components/ParallaxEffect';
import AOS from 'aos';
function App() {
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
    
    return (_jsxs("div", { 
        className: "min-h-screen", 
        children: [
            _jsx(ParallaxEffect, {}),
            _jsx(AnimatedBackground, {}),
            _jsx(Navbar, {}), 
            _jsx("div", { "data-aos": "fade-up", "data-aos-duration": "1000", children: _jsx(HeroSection, {}) }),
            _jsx("div", { "data-aos": "fade-up", "data-aos-delay": "100", children: _jsx(AboutSection, {}) }),
            _jsx("div", { "data-aos": "fade-up", "data-aos-delay": "150", children: _jsx(ProblemSection, {}) }),
            _jsx("div", { "data-aos": "fade-up", "data-aos-delay": "200", children: _jsx(SolutionSection, {}) }),
            _jsx("div", { "data-aos": "fade-up", "data-aos-delay": "250", className: "relative z-10", children: _jsx(FeaturesSection, {}) }),
            _jsx("div", { "data-aos": "fade-up", "data-aos-delay": "300", className: "relative z-10", children: _jsx(HowItWorksSection, {}) }),
            _jsx("div", { "data-aos": "fade-up", "data-aos-delay": "350", className: "relative z-10", children: _jsx(PrivacySection, {}) }),
            _jsx("div", { "data-aos": "fade-up", "data-aos-delay": "400", children: _jsx(DownloadSection, {}) }),
            _jsx(Footer, {})
        ] 
    }))
}
export default App;
