import { jsx as _jsx } from "react/jsx-runtime";
import React, { createContext, useState, useEffect, useContext } from 'react';

const ThemeContext = createContext();

export const useTheme = () => useContext(ThemeContext);

export const ThemeProvider = ({ children }) => {
    const [theme, setTheme] = useState(() => {
        // Check local storage first (if we add a manual toggle later)
        const storedTheme = localStorage.getItem('theme');
        if (storedTheme) return storedTheme;
        
        // Check system preference
        if (window.matchMedia('(prefers-color-scheme: dark)').matches) {
            return 'dark';
        }
        return 'light';
    });
    
    // Toggle theme function
    const toggleTheme = () => {
        setTheme(prevTheme => prevTheme === 'light' ? 'dark' : 'light');
    };

    useEffect(() => {
        const root = window.document.documentElement;
        const isDark = theme === 'dark';

        root.classList.remove(isDark ? 'light' : 'dark');
        root.classList.add(theme);

        // Save theme preference
        localStorage.setItem('theme', theme);

        // Listener for system theme changes
        const mediaQuery = window.matchMedia('(prefers-color-scheme: dark)');
        
        const handleChange = (e) => {
            setTheme(e.matches ? 'dark' : 'light');
        };

        // Use deprecated addListener for broader compatibility, fallback to addEventListener
        try {
            mediaQuery.addEventListener('change', handleChange);
        } catch (e) {
            // Fallback for older browsers
            mediaQuery.addListener(handleChange);
        }

        return () => {
            try {
                mediaQuery.removeEventListener('change', handleChange);
            } catch (e) {
                // Fallback for older browsers
                mediaQuery.removeListener(handleChange);
            }
        };
    }, [theme]);

    return (
        _jsx(ThemeContext.Provider, { value: { theme, toggleTheme }, children: children })
    );
};
