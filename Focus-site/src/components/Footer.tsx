import React from 'react';
import { motion } from 'framer-motion';
import { GithubIcon, Mail } from 'lucide-react';
import { Link } from 'react-scroll';
import logoImage from '../assets/focus.png';

const Footer: React.FC = () => {
  return (
    <footer className="bg-gray-900 text-white pt-16 pb-8">
      <div className="container-custom">
        <div className="grid grid-cols-1 md:grid-cols-4 gap-8 mb-12">
          <div className="col-span-1 md:col-span-2">
            <div className="flex items-center mb-4">
              <img 
                src={logoImage} 
                alt="Focus Logo" 
                className="mr-3 h-10 w-10 object-contain rounded-full" 
                style={{ filter: 'drop-shadow(0 0 2px rgba(124, 58, 237, 0.5))' }}
              />
              <span className="text-xl font-bold">Focus</span>
            </div>
            <p className="text-gray-400 mb-6 max-w-md">
              Helping you regain control of your digital life through smart content blocking and digital wellness tools.
            </p>
            <div className="flex space-x-4">
              <a 
                href="https://github.com/AryanVBW/focus" 
                target="_blank" 
                rel="noopener noreferrer"
                className="text-gray-400 hover:text-white transition-colors"
              >
                <GithubIcon size={20} />
              </a>
              <a 
                href="mailto:vivek.aryanvbw@gmail.com" 
                className="text-gray-400 hover:text-white transition-colors"
              >
                <Mail size={20} />
              </a>
            </div>
          </div>

          <div>
            <h5 className="font-medium text-lg mb-4">Navigation</h5>
            <ul className="space-y-2">
              {['Home', 'About', 'Features', 'Privacy', 'Download'].map((item) => (
                <li key={item}>
                  <Link
                    to={item.toLowerCase()}
                    spy={true}
                    smooth={true}
                    offset={-80}
                    duration={500}
                    className="text-gray-400 hover:text-white transition-colors cursor-pointer"
                  >
                    {item}
                  </Link>
                </li>
              ))}
            </ul>
          </div>

          <div>
            <h5 className="font-medium text-lg mb-4">Resources</h5>
            <ul className="space-y-2">
              <li>
                <a href="#" className="text-gray-400 hover:text-white transition-colors">
                  Privacy Policy
                </a>
              </li>
              <li>
                <a href="#" className="text-gray-400 hover:text-white transition-colors">
                  Terms of Service
                </a>
              </li>
              <li>
                <a 
                  href="https://github.com/AryanVBW/focus/releases" 
                  target="_blank" 
                  rel="noopener noreferrer"
                  className="text-gray-400 hover:text-white transition-colors"
                >
                  GitHub Releases
                </a>
              </li>
              <li>
                <a href="#" className="text-gray-400 hover:text-white transition-colors">
                  Contact Us
                </a>
              </li>
            </ul>
          </div>
        </div>

        <div className="pt-8 border-t border-gray-800 text-center">
          <p className="text-gray-500 text-sm">
            &copy; {new Date().getFullYear()} Focus App. All rights reserved.
          </p>
          <p className="text-gray-600 text-xs mt-2">
            Made with ❤️ for a more mindful digital experience
          </p>
        </div>
      </div>
    </footer>
  );
};

export default Footer;