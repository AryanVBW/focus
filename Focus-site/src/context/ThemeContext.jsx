import React, { createContext, useState, useEffect, useContext } from 'react';
import { useAnnouncement } from '../utils/accessibility';

const ThemeContext = createContext();

export const useTheme = () => {
    const context = useContext(ThemeContext);
    if (!context) {
        throw new Error('useTheme must be used within a ThemeProvider');
    }
    return context;
};

export const ThemeProvider = ({ children }) => {
    const [theme, setTheme] = useState(() => {
        // Check local storage first
        const storedTheme = localStorage.getItem('focus-theme');
        if (storedTheme && ['light', 'dark', 'system'].includes(storedTheme)) {
            return storedTheme;
        }
        return 'system';
    });

    const [systemTheme, setSystemTheme] = useState(() => {
        return window.matchMedia('(prefers-color-scheme: dark)').matches ? 'dark' : 'light';
    });

    const announce = useAnnouncement();

    // Get the actual theme being applied
    const actualTheme = theme === 'system' ? systemTheme : theme;

    // Toggle theme function with improved options
    const toggleTheme = () => {
        const themes = ['light', 'dark', 'system'];
        const currentIndex = themes.indexOf(theme);
        const nextTheme = themes[(currentIndex + 1) % themes.length];
        setTheme(nextTheme);
        
        // Announce theme change for screen readers
        const themeLabels = {
            light: 'Light theme activated',
            dark: 'Dark theme activated', 
            system: 'System theme preference activated'
        };
        announce(themeLabels[nextTheme], 'polite');
    };

    const setSpecificTheme = (newTheme) => {
        if (['light', 'dark', 'system'].includes(newTheme)) {
            setTheme(newTheme);
        }
    };

    // Listen for system theme changes
    useEffect(() => {
        const mediaQuery = window.matchMedia('(prefers-color-scheme: dark)');
        
        const handleSystemThemeChange = (e) => {
            setSystemTheme(e.matches ? 'dark' : 'light');
        };

        // Use the modern addEventListener API
        mediaQuery.addEventListener('change', handleSystemThemeChange);

        return () => {
            mediaQuery.removeEventListener('change', handleSystemThemeChange);
        };
    }, []);

    // Apply theme to document
    useEffect(() => {
        const root = window.document.documentElement;
        const isDark = actualTheme === 'dark';

        // Remove previous theme classes
        root.classList.remove('light', 'dark');
        root.classList.add(actualTheme);

        // Set CSS custom properties for theme transitions
        root.style.setProperty('--theme-transition', 'background-color 0.3s ease, color 0.3s ease');

        // Save theme preference
        localStorage.setItem('focus-theme', theme);

        // Update meta theme-color for mobile browsers
        const metaThemeColor = document.querySelector('meta[name="theme-color"]');
        if (metaThemeColor) {
            metaThemeColor.setAttribute('content', isDark ? '#1f2937' : '#ffffff');
        }

        // Dispatch custom event for other components to listen to theme changes
        window.dispatchEvent(new CustomEvent('themeChange', { 
            detail: { theme: actualTheme, userPreference: theme } 
        }));
    }, [theme, actualTheme]);

    // Detect user's reduced motion preference
    const prefersReducedMotion = window.matchMedia('(prefers-reduced-motion: reduce)').matches;

    const themeValue = {
        theme,
        actualTheme,
        systemTheme,
        toggleTheme,
        setTheme: setSpecificTheme,
        isDark: actualTheme === 'dark',
        isLight: actualTheme === 'light',
        isSystem: theme === 'system',
        prefersReducedMotion,
        // Utility functions for conditional styling
        getThemeClass: (lightClass, darkClass) => actualTheme === 'dark' ? darkClass : lightClass,
        getConditionalClass: (condition, trueClass, falseClass = '') => condition ? trueClass : falseClass
    };

    return (
        <ThemeContext.Provider value={themeValue}>
            {children}
        </ThemeContext.Provider>
    );
};
