import React, { useEffect, useState } from 'react';
import { Link as ScrollLink } from 'react-scroll';
import { motion, useScroll, useTransform } from 'framer-motion';
import { Lock, Menu, X } from 'lucide-react';
import { navLinks } from '../data/content';

const Navbar: React.FC = () => {
  const [isOpen, setIsOpen] = useState(false);
  const { scrollYProgress } = useScroll();
  
  const backgroundColor = useTransform(
    scrollYProgress,
    [0, 0.05],
    ['rgba(255, 255, 255, 0)', 'rgba(255, 255, 255, 0.9)']
  );
  
  const boxShadow = useTransform(
    scrollYProgress,
    [0, 0.05],
    ['none', '0 4px 20px rgba(0, 0, 0, 0.05)']
  );
  
  const textColor = useTransform(
    scrollYProgress,
    [0, 0.05],
    ['#ffffff', '#1f2937']
  );

  const backdropFilter = useTransform(
    scrollYProgress,
    [0, 0.05],
    ['none', 'blur(10px)']
  );
  
  // Close mobile menu when clicking outside
  useEffect(() => {
    const handleClickOutside = () => {
      if (isOpen) setIsOpen(false);
    };
    
    document.addEventListener('click', handleClickOutside);
    return () => document.removeEventListener('click', handleClickOutside);
  }, [isOpen]);

  return (
    <motion.header
      className="fixed top-0 left-0 w-full z-50 py-4"
      style={{ 
        backgroundColor, 
        boxShadow,
        backdropFilter
      }}
      initial={{ y: -100 }}
      animate={{ y: 0 }}
      transition={{ duration: 0.5 }}
    >
      <div className="container-custom mx-auto flex justify-between items-center">
        <ScrollLink
          to="home"
          spy={true}
          smooth={true}
          duration={500}
          className="flex items-center cursor-pointer"
        >
          <div className="mr-2 p-2 rounded-full bg-primary-600 text-white">
            <Lock size={20} />
          </div>
          <motion.span 
            className="text-xl font-bold"
            style={{ color: textColor }}
          >
            Focus
          </motion.span>
        </ScrollLink>

        {/* Desktop Navigation */}
        <nav className="hidden md:flex space-x-8">
          {navLinks.map((link) => (
            <ScrollLink
              key={link.href}
              to={link.href}
              spy={true}
              smooth={true}
              offset={-80}
              duration={500}
              className="cursor-pointer"
            >
              <motion.span
                className="relative inline-block font-medium hover:text-primary-600 transition-colors"
                style={{ color: textColor }}
                whileHover={{ y: -2 }}
              >
                {link.name}
              </motion.span>
            </ScrollLink>
          ))}
        </nav>

        {/* Mobile Navigation Toggle */}
        <div 
          className="md:hidden"
          onClick={(e) => {
            e.stopPropagation();
            setIsOpen(!isOpen);
          }}
        >
          <button 
            className="p-2 focus:outline-none" 
            aria-label="Toggle menu"
          >
            {isOpen ? (
              <X className="w-6 h-6" style={{ color: textColor as any }} />
            ) : (
              <Menu className="w-6 h-6" style={{ color: textColor as any }} />
            )}
          </button>
        </div>
      </div>

      {/* Mobile Navigation Menu */}
      {isOpen && (
        <motion.div
          className="md:hidden absolute top-full left-0 w-full bg-white shadow-lg"
          initial={{ opacity: 0, height: 0 }}
          animate={{ opacity: 1, height: 'auto' }}
          exit={{ opacity: 0, height: 0 }}
          transition={{ duration: 0.2 }}
          onClick={(e) => e.stopPropagation()}
        >
          <div className="container-custom py-4 flex flex-col">
            {navLinks.map((link) => (
              <ScrollLink
                key={link.href}
                to={link.href}
                spy={true}
                smooth={true}
                offset={-80}
                duration={500}
                className="py-3 px-4 hover:bg-gray-50 cursor-pointer"
                onClick={() => setIsOpen(false)}
              >
                {link.name}
              </ScrollLink>
            ))}
          </div>
        </motion.div>
      )}
    </motion.header>
  );
};

export default Navbar;