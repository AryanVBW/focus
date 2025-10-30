import React from 'react';
import { motion } from 'framer-motion';

const TermsOfService = () => {
  return (
    <section className="section bg-white">
      <div className="container-custom max-w-4xl mx-auto py-12">
        <motion.div
          initial={{ opacity: 0 }}
          animate={{ opacity: 1 }}
          transition={{ duration: 0.5 }}
        >
          <h1 className="text-3xl md:text-4xl font-bold mb-8 text-center text-gray-900 dark:text-gray-100">Terms of Service</h1>
          
          <div className="prose prose-lg max-w-none">
            <p className="text-gray-600 dark:text-gray-300 mb-6">
              Last Updated: May 2, 2025
            </p>

            <h2 className="text-2xl font-semibold mt-8 mb-4 text-gray-900 dark:text-gray-100">Introduction</h2>
            <p>
              Welcome to Focus. By accessing or using our application, you agree to be bound by these Terms of Service 
              ("Terms"). Please read these Terms carefully before using Focus.
            </p>

            <h2 className="text-2xl font-semibold mt-8 mb-4 text-gray-900 dark:text-gray-100">Acceptance of Terms</h2>
            <p>
              By downloading, installing, or using Focus, you agree to these Terms and our Privacy Policy. If you do 
              not agree with any part of these Terms, you may not use our application.
            </p>

            <h2 className="text-2xl font-semibold mt-8 mb-4 text-gray-900 dark:text-gray-100">Description of Service</h2>
            <p>
              Focus is a productivity application designed to help users minimize distractions and stay focused on their 
              tasks. The app monitors application usage locally on your device, manages notifications, and provides tools to improve productivity.
            </p>
            
            <h3 className="text-xl font-semibold mt-6 mb-3 text-gray-900 dark:text-gray-100">Current Version (V1)</h3>
            <p>
              The current version of Focus operates entirely on your local device. All data, including your usage patterns and focus sessions, 
              is stored locally on your device. No data is transmitted to our servers or third parties.
            </p>
            
            <h3 className="text-xl font-semibold mt-6 mb-3 text-gray-900 dark:text-gray-100">Upcoming Features (V2)</h3>
            <p>
              In a future update (V2), we plan to introduce an optional analytics dashboard that will enable you to track your digital habits 
              across devices, visualize your progress, and gain deeper insights into your productivity patterns. This feature will require 
              creating an account and will involve some data collection as described in our Privacy Policy. This upcoming feature will be 
              entirely optional and opt-in. The core functionality of Focus will remain available without requiring any data sharing.
            </p>

            <h2 className="text-2xl font-semibold mt-8 mb-4 text-gray-900 dark:text-gray-100">User Accounts</h2>
            <p>
              <strong>Current Version (V1):</strong> The current version of Focus does not require or support user accounts as all data is stored locally on your device.
            </p>
            <p className="mt-4">
              <strong>Future Plans (V2):</strong> In our planned V2 release, some enhanced features may require you to create an account. 
              If you choose to create an account, you will be responsible for maintaining the confidentiality of your account information 
              and for all activities that occur under your account. You would agree to:
            </p>
            <ul className="list-disc pl-6 mb-4">
              <li>Provide accurate and complete information when creating your account</li>
              <li>Update your information when necessary to keep it accurate and current</li>
              <li>Notify us immediately of any unauthorized use of your account</li>
              <li>Choose whether to opt in or out of any data collection features</li>
            </ul>
            <p>
              Account creation will be entirely optional. The core functionality of Focus will remain available without requiring an account.
            </p>

            <h2 className="text-2xl font-semibold mt-8 mb-4 text-gray-900 dark:text-gray-100">User Conduct</h2>
            <p>
              When using Focus, you agree not to:
            </p>
            <ul className="list-disc pl-6 mb-4">
              <li>Use the application for any illegal purpose</li>
              <li>Attempt to gain unauthorized access to any part of the application</li>
              <li>Interfere with or disrupt the application or servers</li>
              <li>Bypass any measures we use to restrict access to the application</li>
              <li>Reverse engineer, decompile, or attempt to extract the source code of the application</li>
            </ul>

            <h2 className="text-2xl font-semibold mt-8 mb-4 text-gray-900 dark:text-gray-100">Intellectual Property Rights</h2>
            <p>
              The application, including all content, features, and functionality, is owned by Focus and is protected by 
              copyright, trademark, and other intellectual property laws. You may not modify, reproduce, distribute, or 
              create derivative works based on any materials from Focus without our explicit permission.
            </p>

            <h2 className="text-2xl font-semibold mt-8 mb-4 text-gray-900 dark:text-gray-100">Third-Party Links and Services</h2>
            <p>
              Our application may contain links to third-party websites or services. We are not responsible for the 
              content or practices of these third parties. Your interactions with these third parties are solely between 
              you and them.
            </p>

            <h2 className="text-2xl font-semibold mt-8 mb-4 text-gray-900 dark:text-gray-100">Limitation of Liability</h2>
            <p>
              To the maximum extent permitted by law, we shall not be liable for any indirect, incidental, special, 
              consequential, or punitive damages resulting from your use or inability to use the application.
            </p>

            <h2 className="text-2xl font-semibold mt-8 mb-4 text-gray-900 dark:text-gray-100">Disclaimer of Warranties</h2>
            <p>
              The application is provided "as is" and "as available" without warranties of any kind, either express or 
              implied. We do not guarantee that the application will always be secure, error-free, or available.
            </p>

            <h2 className="text-2xl font-semibold mt-8 mb-4 text-gray-900 dark:text-gray-100">Changes to Terms</h2>
            <p>
              We may modify these Terms at any time by posting the revised terms on our website or within the application. 
              Your continued use of Focus after such changes constitutes your acceptance of the new Terms.
            </p>

            <h2 className="text-2xl font-semibold mt-8 mb-4 text-gray-900 dark:text-gray-100">Governing Law</h2>
            <p>
              These Terms shall be governed by and construed in accordance with the laws of the jurisdiction in which 
              we operate, without regard to its conflict of law principles.
            </p>

            <h2 className="text-2xl font-semibold mt-8 mb-4 text-gray-900 dark:text-gray-100">Contact Information</h2>
            <p>
              If you have any questions about these Terms, please contact us at:
            </p>
            <p className="mb-6">
              Email: vivek.aryanvbw@gmail.com
            </p>

            <div className="bg-gray-100 dark:bg-slate-800 p-6 rounded-lg mt-8 border border-gray-200 dark:border-slate-700">
              <p className="text-center font-medium text-gray-900 dark:text-white">
                By using Focus, you acknowledge that you have read and understand these Terms of Service.
              </p>
            </div>
          </div>
        </motion.div>
      </div>
    </section>
  );
};

export default TermsOfService;
