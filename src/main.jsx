import { jsx as _jsx } from "react/jsx-runtime";
import React, { useEffect } from 'react';
import ReactDOM from 'react-dom/client';
import './index.css';
import './css/animations.css';
import App from './App';
import { ThemeProvider } from './context/ThemeContext';


// Import AOS for scroll animations
import AOS from 'aos';
import 'aos/dist/aos.css';

// Initialize AOS
AOS.init({
  duration: 800,
  easing: 'ease-out',
  once: true,
  offset: 120,
  delay: 100,
});

ReactDOM.createRoot(document.getElementById('root')).render(
  _jsx(React.StrictMode, {
    children: _jsx(ThemeProvider, {
      children: _jsx(App, {})
    })
  })
);
