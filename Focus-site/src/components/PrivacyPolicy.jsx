import React from 'react';
import { motion } from 'framer-motion';

const PrivacyPolicy = () => {
  return (
    <section className="section bg-white">
      <div className="container-custom max-w-4xl mx-auto py-12">
        <motion.div
          initial={{ opacity: 0 }}
          animate={{ opacity: 1 }}
          transition={{ duration: 0.5 }}
        >
          <h1 className="text-3xl md:text-4xl font-bold mb-8 text-center">Privacy Policy</h1>
          
          <div className="prose prose-lg max-w-none">
            <p className="text-gray-600 mb-6">
              Last Updated: May 2, 2025
            </p>

            <h2 className="text-2xl font-semibold mt-8 mb-4">Introduction</h2>
            <p>
              Welcome to Focus . We are committed to protecting your privacy and ensuring 
              the security of your personal information. This Privacy Policy explains how we collect, use, disclose, 
              and safeguard your information when you use our Focus application and related services.
            </p>

            <h2 className="text-2xl font-semibold mt-8 mb-4">Information We Collect</h2>
            <p>
              <strong>Focus V1 (Current Version):</strong> We do not collect any personal information in the current version of Focus. 
              All your data, including your usage timeline and settings, is stored entirely locally on your device. 
              We believe in absolute privacy and giving you complete control over your data.
            </p>
            
            <h3 className="text-xl font-semibold mt-6 mb-3">Future Plans (Focus V2)</h3>
            <p>
              In an upcoming V2 release, we plan to introduce an optional analytics dashboard that will require 
              collecting certain information to provide personalized insights. This future version may collect:
            </p>
            <ul className="list-disc pl-6 mb-4">
              <li>Basic user information (email for account identification)</li>
              <li>Device information (device type, operating system)</li>
              <li>App usage patterns (which apps you use most frequently)</li>
              <li>Focus app engagement statistics (how often you open the app)</li>
            </ul>
            <p className="mb-4">
              <strong>Important:</strong> This data collection will be entirely optional and opt-in. 
              You will always have the option to continue using Focus without sharing any data, and 
              we will provide clear notice before implementing these changes.
            </p>
            
            <p>
              <strong>App Permissions:</strong> Focus may request the following permissions:
            </p>
            <ul className="list-disc pl-6 mb-4">
              <li><strong>Usage Access:</strong> To monitor which apps you're using to help you stay focused</li>
              <li><strong>Notification Access:</strong> To manage notifications during focus sessions</li>
              <li><strong>Display Over Other Apps:</strong> To show focus reminders when necessary</li>
              <li><strong>Internet Access:</strong> For syncing settings across devices (if applicable)</li>
            </ul>

            <h2 className="text-2xl font-semibold mt-8 mb-4">How We Use Your Information</h2>
            <p><strong>Current Version (V1):</strong> Since we don't collect any data in the current version, all processing happens locally on your device.</p>
            
            <p className="mt-4"><strong>Future Plans (V2):</strong> In our planned V2 release, if you opt in to data collection, we would use the information for:</p>
            <ul className="list-disc pl-6 mb-4">
              <li>Providing personalized analytics and insights about your digital habits</li>
              <li>Creating a dashboard to visualize your progress and focus patterns</li>
              <li>Enabling cross-device synchronization of your settings and progress</li>
              <li>Improving the app based on aggregated usage patterns</li>
              <li>Communicating important updates about the service</li>
            </ul>

            <h2 className="text-2xl font-semibold mt-8 mb-4">Data Storage and Security</h2>
            <p>
              <strong>Current Version (V1):</strong> All data processing and storage happens entirely on your device. 
              No data is transmitted to our servers or to any third parties. Your privacy is completely preserved as 
              we have no access to any of your information.
            </p>
            <p className="mt-4">
              <strong>Future Plans (V2):</strong> If you opt in to our analytics features in V2, we will employ 
              industry-standard security measures including encryption and secure authentication to protect your information. 
              We will never sell your personal information to third parties under any circumstances.
            </p>

            <h2 className="text-2xl font-semibold mt-8 mb-4">Data Retention</h2>
            <p>
              We retain your information only for as long as necessary to fulfill the purposes outlined in this privacy policy,
              unless a longer retention period is required by law.
            </p>

            <h2 className="text-2xl font-semibold mt-8 mb-4">Your Rights</h2>
            <p>Depending on your location, you may have rights regarding your personal information, including:</p>
            <ul className="list-disc pl-6 mb-4">
              <li>Access to your personal information</li>
              <li>Correction of inaccurate or incomplete information</li>
              <li>Deletion of your personal information</li>
              <li>Restriction or objection to processing</li>
              <li>Data portability</li>
            </ul>
            <p>
              To exercise these rights, please contact us using the information provided below.
            </p>

            <h2 className="text-2xl font-semibold mt-8 mb-4">Children's Privacy</h2>
            <p>
              Our services are not intended for children under 13 years of age. We do not knowingly collect personal 
              information from children under 13.
            </p>

            <h2 className="text-2xl font-semibold mt-8 mb-4">Changes to This Privacy Policy</h2>
            <p>
              We may update our Privacy Policy from time to time. We will notify you of any changes by posting the new 
              Privacy Policy on this page and updating the "Last Updated" date.
            </p>

            <h2 className="text-2xl font-semibold mt-8 mb-4">Contact Us</h2>
            <p>
              If you have any questions or concerns about this Privacy Policy, please contact us at:
            </p>
            <p className="mb-6">
              Email: privacy@focusapp.com
            </p>

            <div className="bg-gray-100 dark:bg-slate-800 p-6 rounded-lg mt-8 border border-gray-200 dark:border-slate-700">
              <p className="text-center font-medium text-gray-900 dark:text-white">
                By using Focus, you acknowledge that you have read and understand this Privacy Policy.
              </p>
            </div>
          </div>
        </motion.div>
      </div>
    </section>
  );
};

export default PrivacyPolicy;
