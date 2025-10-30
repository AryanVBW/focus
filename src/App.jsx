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
import DocumentationRouter from './components/DocumentationRouter';
import DownloadSection from './components/DownloadSection';
import Footer from './components/Footer';
import AnimatedBackground from './components/AnimatedBackground';
import ParallaxEffect from './components/ParallaxEffect';
import { ErrorBoundary, LoadingState } from './components/LoadingComponents';
import { ariaHelpers } from './utils/accessibility';
import { useLoadingState } from './hooks/usePerformance';
import AOS from 'aos';

function App() {
    // State to handle which page to show
    const [currentPage, setCurrentPage] = useState('home');
    const [isInitialLoading, setIsInitialLoading] = useState(true);
    const { loading: pageLoading, execute: loadPage } = useLoadingState();
    
    // Function to handle navigation with loading states
    const navigateTo = async (page) => {
        try {
            await loadPage(async () => {
                // Simulate page loading for smooth transitions
                await new Promise(resolve => setTimeout(resolve, 150));
                
                // Update URL hash
                if (page === 'home') {
                    window.location.hash = '';
                } else {
                    window.location.hash = page;
                }
                
                setCurrentPage(page);
                
                // Set page title for accessibility
                const pageTitles = {
                    'home': 'Focus - Regain control of your digital life',
                    'privacy-policy': 'Privacy Policy',
                    'terms-of-service': 'Terms of Service',
                    'app-overview': 'App Overview',
                    'documentation': 'Focus Documentation'
                };
                
                ariaHelpers.setPageTitle(pageTitles[page] || 'Focus App');
                
                // Scroll to top
                window.scrollTo({ top: 0, behavior: 'smooth' });
                
                // Announce page change to screen readers
                const pageNames = {
                    'home': 'Home page',
                    'privacy-policy': 'Privacy Policy page',
                    'terms-of-service': 'Terms of Service page',
                    'app-overview': 'App Overview page',
                    'documentation': 'Documentation page'
                };
                
                setTimeout(() => {
                    ariaHelpers.announceLoading(`Navigated to ${pageNames[page] || 'new page'}`);
                }, 300);
                
                return page;
            });
        } catch (error) {
            console.error('Navigation error:', error);
            ariaHelpers.announceError('Failed to navigate to the requested page');
        }
    };
    
    // Get page from URL on initial load
    useEffect(() => {
        const initializeApp = async () => {
            try {
                const hash = window.location.hash.replace('#', '');
                if (hash === 'privacy-policy') setCurrentPage('privacy-policy');
                else if (hash === 'terms-of-service') setCurrentPage('terms-of-service');
                else if (hash === 'app-overview') setCurrentPage('app-overview');
                else if (hash.startsWith('documentation')) setCurrentPage('documentation');
                else if (hash === '') setCurrentPage('home');
                
                // Update URL when page changes
                const handleHashChange = () => {
                    const hash = window.location.hash.replace('#', '');
                    if (hash === 'privacy-policy') setCurrentPage('privacy-policy');
                    else if (hash === 'terms-of-service') setCurrentPage('terms-of-service');
                    else if (hash === 'app-overview') setCurrentPage('app-overview');
                    else if (hash.startsWith('documentation')) setCurrentPage('documentation');
                    else if (hash === '') setCurrentPage('home');
                };
                
                window.addEventListener('hashchange', handleHashChange);
                
                // Set initial page title
                const initialTitle = {
                    'privacy-policy': 'Privacy Policy',
                    'terms-of-service': 'Terms of Service',
                    'app-overview': 'App Overview',
                    'documentation': 'Focus Documentation',
                    'home': 'Focus - Regain control of your digital life'
                }[hash.startsWith('documentation') ? 'documentation' : (hash || 'home')];
                
                ariaHelpers.setPageTitle(initialTitle);
                
                // Simulate initial loading
                await new Promise(resolve => setTimeout(resolve, 1000));
                setIsInitialLoading(false);
                
                return () => window.removeEventListener('hashchange', handleHashChange);
            } catch (error) {
                console.error('App initialization error:', error);
                setIsInitialLoading(false);
            }
        };
        
        initializeApp();
    }, []);
    
    // Refresh AOS on window resize for responsive animations
    useEffect(() => {
        const handleResize = () => {
            AOS.refresh();
        };
        
        window.addEventListener('resize', handleResize);
        return () => window.removeEventListener('resize', handleResize);
    }, []);

    // Skip link for accessibility
    const SkipLink = () => (
        <a
            href="#main-content"
            className="sr-only focus:not-sr-only focus:absolute focus:top-4 focus:left-4 bg-primary-600 text-white px-4 py-2 rounded-lg z-50 focus:outline-none focus:ring-2 focus:ring-primary-400"
        >
            Skip to main content
        </a>
    );
    
    // Function to render main content with loading states
    const renderMainContent = () => {
        if (pageLoading) {
            return <LoadingState message="Loading page..." className="min-h-screen" />;
        }

        return (
            <div className="min-h-screen">
                <ParallaxEffect />
                <AnimatedBackground />
                <Navbar navigateTo={navigateTo} />
                <main id="main-content" role="main">
                    <div data-aos="fade-up" data-aos-duration="1000">
                        <HeroSection />
                    </div>
                    <div data-aos="fade-up" data-aos-delay="100">
                        <AboutSection />
                    </div>
                    <div data-aos="fade-up" data-aos-delay="150">
                        <ProblemSection />
                    </div>
                    <div data-aos="fade-up" data-aos-delay="200">
                        <SolutionSection />
                    </div>
                    <div data-aos="fade-up" data-aos-delay="250" className="relative z-10">
                        <FeaturesSection />
                    </div>
                    <div data-aos="fade-up" data-aos-delay="300" className="relative z-10">
                        <HowItWorksSection />
                    </div>
                    <div data-aos="fade-up" data-aos-delay="350" className="relative z-10">
                        <PrivacySection navigateTo={navigateTo} />
                    </div>
                    <div data-aos="fade-up" data-aos-delay="400">
                        <DownloadSection />
                    </div>
                </main>
                <Footer navigateTo={navigateTo} />
            </div>
        );
    };
    
    // Render the appropriate page based on currentPage state
    const renderPage = () => {
        if (isInitialLoading) {
            return <LoadingState message="Loading Focus App..." className="min-h-screen" />;
        }

        switch (currentPage) {
            case 'privacy-policy':
                return (
                    <div className="min-h-screen">
                        <Navbar navigateTo={navigateTo} />
                        <main id="main-content" role="main">
                            <PrivacyPolicy />
                        </main>
                        <Footer navigateTo={navigateTo} />
                    </div>
                );
            case 'terms-of-service':
                return (
                    <div className="min-h-screen">
                        <Navbar navigateTo={navigateTo} />
                        <main id="main-content" role="main">
                            <TermsOfService />
                        </main>
                        <Footer navigateTo={navigateTo} />
                    </div>
                );
            case 'app-overview':
                return (
                    <div className="min-h-screen">
                        <Navbar navigateTo={navigateTo} />
                        <main id="main-content" role="main">
                            <AppOverview />
                        </main>
                        <Footer navigateTo={navigateTo} />
                    </div>
                );
            case 'documentation':
                return (
                    <div className="min-h-screen">
                        <main id="main-content" role="main">
                            <DocumentationRouter />
                        </main>
                    </div>
                );
            default:
                return renderMainContent();
        }
    };
    
    return (
        <ErrorBoundary>
            <SkipLink />
            {renderPage()}
        </ErrorBoundary>
    );
}

export default App;
