// Simple mock source for now - we'll create a basic implementation
const mockPages = [
  {
    url: '/index',
    data: {
      title: 'Welcome to Focus Documentation',
      description: 'Learn how to use Focus to regain control of your digital life',
      content: `
        <p>Focus is a powerful productivity application designed to help you minimize distractions and stay focused on what matters most. This documentation will guide you through all the features and capabilities of the Focus app.</p>

        <h2>What is Focus?</h2>
        <p>Focus uses advanced detection algorithms and Android's Accessibility Service to identify and block distracting content, helping you maintain your attention on what truly matters. The app operates in two primary modes to give you flexible control over your digital experience.</p>

        <h2>Getting Started</h2>
        <p>To get started with Focus, you'll need to:</p>
        <ol>
          <li><strong>Download the app</strong> from our <a href="https://github.com/AryanVBW/focus/releases">releases page</a></li>
          <li><strong>Install the APK</strong> and follow the installation prompts</li>
          <li><strong>Grant necessary permissions</strong> for the app to function properly</li>
          <li><strong>Configure your preferences</strong> to match your focus goals</li>
        </ol>

        <h2>Key Features</h2>
        <h3>🎯 Normal Mode</h3>
        <p>Selectively blocks the most addictive features while allowing you to use the core functionality of your apps:</p>
        <ul>
          <li>Use Instagram, but blocks Reels</li>
          <li>Use Snapchat, but blocks Stories and Spotlight</li>
          <li>Use YouTube, but blocks Shorts</li>
          <li>Use browsers, but blocks adult content</li>
        </ul>

        <h3>🚫 Focus Mode</h3>
        <p>Complete blocking of selected apps during your focus sessions, helping you maintain deep concentration.</p>

        <h3>📊 Usage Analytics</h3>
        <p>Track your digital habits with detailed analytics and insights (stored locally on your device).</p>

        <h3>🔒 Privacy First</h3>
        <p>All your data is stored locally on your device. We don't collect or transmit any personal information.</p>
      `,
      toc: [],
      full: false
    }
  },
  {
    url: '/installation',
    data: {
      title: 'Installation Guide',
      description: 'Step-by-step instructions to install and set up Focus',
      content: `
        <p>This guide will walk you through the process of installing Focus on your Android device and configuring it for optimal performance.</p>

        <h2>System Requirements</h2>
        <ul>
          <li><strong>Android Version</strong>: 7.0 (API level 24) or higher</li>
          <li><strong>Storage</strong>: At least 50MB of free space</li>
          <li><strong>RAM</strong>: Minimum 2GB recommended for smooth operation</li>
        </ul>

        <h2>Download Focus</h2>
        <ol>
          <li>Visit our <a href="https://github.com/AryanVBW/focus/releases">GitHub Releases page</a></li>
          <li>Download the latest APK file</li>
          <li>Ensure you're downloading from the official repository to avoid security risks</li>
        </ol>

        <h2>Installation Steps</h2>
        <h3>Step 1: Enable Unknown Sources</h3>
        <p>Before installing the APK, you need to allow installation from unknown sources:</p>
        <ol>
          <li>Go to <strong>Settings</strong> > <strong>Security</strong> (or <strong>Privacy</strong>)</li>
          <li>Enable <strong>Unknown Sources</strong> or <strong>Install unknown apps</strong></li>
          <li>Select your browser or file manager and allow it to install apps</li>
        </ol>

        <h3>Step 2: Install the APK</h3>
        <ol>
          <li>Open the downloaded APK file</li>
          <li>Tap <strong>Install</strong> when prompted</li>
          <li>Wait for the installation to complete</li>
          <li>Tap <strong>Open</strong> to launch Focus</li>
        </ol>

        <h2>Initial Setup</h2>
        <h3>Required Permissions</h3>
        <p>Focus requires several permissions to function properly:</p>

        <h4>Accessibility Service</h4>
        <ul>
          <li><strong>Purpose</strong>: Detect and block distracting content</li>
          <li><strong>Setup</strong>: Settings > Accessibility > Focus > Enable</li>
          <li><strong>Note</strong>: This is the core permission that enables Focus to work</li>
        </ul>

        <h4>Usage Stats Permission</h4>
        <ul>
          <li><strong>Purpose</strong>: Track app usage time (optional but recommended)</li>
          <li><strong>Setup</strong>: Settings > Apps > Special access > Usage access > Focus > Enable</li>
        </ul>

        <h4>Display Over Other Apps</h4>
        <ul>
          <li><strong>Purpose</strong>: Show blocking pages when distractions are detected</li>
          <li><strong>Setup</strong>: Settings > Apps > Special access > Display over other apps > Focus > Enable</li>
        </ul>
      `,
      toc: [],
      full: false
    }
  },
  {
    url: '/features',
    data: {
      title: 'Features Overview',
      description: 'Comprehensive guide to all Focus features and capabilities',
      content: `
        <p>Focus offers a comprehensive suite of features designed to help you regain control of your digital life. This guide covers all available features and how to use them effectively.</p>

        <h2>Core Blocking Features</h2>
        <h3>Normal Mode</h3>
        <p>Normal Mode allows you to use your favorite apps while blocking their most addictive features:</p>

        <h4>Social Media Blocking</h4>
        <ul>
          <li><strong>Instagram</strong>: Blocks Reels while allowing posts, stories, and messaging</li>
          <li><strong>Snapchat</strong>: Blocks Stories and Spotlight while preserving chat functionality</li>
          <li><strong>TikTok</strong>: Completely blocks the app during active sessions</li>
          <li><strong>Facebook</strong>: Blocks the news feed while allowing messaging and groups</li>
        </ul>

        <h4>Video Platform Blocking</h4>
        <ul>
          <li><strong>YouTube</strong>: Blocks Shorts while allowing regular videos and subscriptions</li>
          <li><strong>YouTube Music</strong>: Remains fully functional</li>
          <li><strong>Other video apps</strong>: Customizable blocking options</li>
        </ul>

        <h4>Web Browser Protection</h4>
        <ul>
          <li><strong>Adult Content Filtering</strong>: Automatically blocks inappropriate websites</li>
          <li><strong>Social Media Sites</strong>: Blocks access to social media websites</li>
          <li><strong>Custom Blocklist</strong>: Add your own distracting websites</li>
        </ul>

        <h3>Focus Mode</h3>
        <p>Focus Mode provides complete app blocking for deep concentration:</p>
        <ul>
          <li><strong>Complete App Blocking</strong>: Selected apps become completely inaccessible</li>
          <li><strong>Timer-Based Sessions</strong>: Set specific focus periods (15 min, 30 min, 1 hour, custom)</li>
          <li><strong>Break Intervals</strong>: Configure break periods between focus sessions</li>
          <li><strong>Emergency Override</strong>: Break focus mode in genuine emergencies</li>
        </ul>

        <h2>Usage Analytics</h2>
        <h3>Real-Time Monitoring</h3>
        <ul>
          <li><strong>App Usage Time</strong>: Track how long you spend in each app</li>
          <li><strong>Blocking Statistics</strong>: See how many distractions were blocked</li>
          <li><strong>Focus Session History</strong>: Review your focus session performance</li>
          <li><strong>Weekly/Monthly Reports</strong>: Comprehensive usage summaries</li>
        </ul>

        <h3>Privacy-First Analytics</h3>
        <ul>
          <li><strong>Local Storage Only</strong>: All data stays on your device</li>
          <li><strong>No Cloud Sync</strong>: Your usage data never leaves your phone</li>
          <li><strong>Export Options</strong>: Export your data for personal analysis</li>
        </ul>

        <h2>Customization Options</h2>
        <h3>App Selection</h3>
        <ul>
          <li><strong>Individual App Control</strong>: Choose exactly which apps to monitor</li>
          <li><strong>Category-Based Selection</strong>: Select entire categories (Social, Entertainment, etc.)</li>
          <li><strong>Whitelist Mode</strong>: Allow only specific apps during focus sessions</li>
        </ul>

        <h3>Blocking Intensity</h3>
        <ul>
          <li><strong>Gentle Reminders</strong>: Show warnings before blocking</li>
          <li><strong>Immediate Blocking</strong>: Instant blocking without warnings</li>
          <li><strong>Progressive Blocking</strong>: Increase blocking intensity over time</li>
        </ul>
      `,
      toc: [],
      full: false
    }
  },
  {
    url: '/faq',
    data: {
      title: 'Frequently Asked Questions',
      description: 'Common questions and answers about Focus',
      content: `
        <h2>General Questions</h2>

        <h3>What is Focus?</h3>
        <p>Focus is a productivity app for Android that helps you minimize distractions by selectively blocking addictive features in social media apps and providing complete app blocking during focus sessions.</p>

        <h3>Is Focus free?</h3>
        <p>Yes, Focus is completely free and open-source. You can download it from our GitHub releases page.</p>

        <h3>Does Focus collect my data?</h3>
        <p>No, Focus does not collect any personal data. All your usage statistics and settings are stored locally on your device and never transmitted to our servers.</p>

        <h2>Installation & Setup</h2>

        <h3>Why do I need to enable "Unknown Sources"?</h3>
        <p>Focus is distributed as an APK file from GitHub rather than through the Google Play Store. Android requires you to enable "Unknown Sources" to install apps from outside the Play Store.</p>

        <h3>What permissions does Focus need?</h3>
        <p>Focus requires several permissions to function:</p>
        <ul>
          <li><strong>Accessibility Service</strong>: To detect and block distracting content</li>
          <li><strong>Usage Stats</strong>: To track app usage time (optional)</li>
          <li><strong>Display Over Other Apps</strong>: To show blocking screens</li>
          <li><strong>Query All Packages</strong>: To show list of installed apps</li>
        </ul>

        <h3>Is it safe to grant Accessibility Service permission?</h3>
        <p>Yes, Focus uses the Accessibility Service only to detect specific content patterns in supported apps. The app is open-source, so you can review the code to see exactly how this permission is used.</p>

        <h2>Features & Usage</h2>

        <h3>What's the difference between Normal Mode and Focus Mode?</h3>
        <p><strong>Normal Mode</strong> allows you to use apps but blocks their most addictive features (like Instagram Reels or YouTube Shorts). <strong>Focus Mode</strong> completely blocks selected apps for a specified time period.</p>

        <h3>Can I customize which features are blocked?</h3>
        <p>Currently, Focus has predefined blocking rules for supported apps. Future versions will include more customization options.</p>

        <h3>Which apps does Focus support?</h3>
        <p>Focus currently supports:</p>
        <ul>
          <li>Instagram (blocks Reels)</li>
          <li>YouTube (blocks Shorts)</li>
          <li>Snapchat (blocks Stories and Spotlight)</li>
          <li>Web browsers (blocks adult content and social media sites)</li>
          <li>Complete blocking for any installed app in Focus Mode</li>
        </ul>

        <h2>Troubleshooting</h2>

        <h3>Focus isn't blocking content. What should I do?</h3>
        <p>Try these steps:</p>
        <ol>
          <li>Ensure the Accessibility Service is enabled for Focus</li>
          <li>Restart the Focus app</li>
          <li>Restart your device</li>
          <li>Check that the app you're trying to use is supported</li>
        </ol>

        <h3>The app crashes when I open it. How can I fix this?</h3>
        <p>Try these solutions:</p>
        <ol>
          <li>Clear Focus app data: Settings > Apps > Focus > Storage > Clear Data</li>
          <li>Restart your device</li>
          <li>Reinstall the app with the latest version</li>
          <li>Check that your Android version is 7.0 or higher</li>
        </ol>

        <h3>Can I use Focus on multiple devices?</h3>
        <p>Currently, Focus works on individual devices only. Each device needs its own installation and configuration. Cloud sync may be added in future versions.</p>

        <h2>Privacy & Security</h2>

        <h3>Can other apps access my Focus data?</h3>
        <p>No, Focus stores all data in its private app directory, which is only accessible to Focus itself. Other apps cannot read this data without root access.</p>

        <h3>What happens to my data if I uninstall Focus?</h3>
        <p>All Focus data is automatically deleted when you uninstall the app. There are no traces left on your device or any external servers.</p>

        <h2>Development & Support</h2>

        <h3>Is Focus open source?</h3>
        <p>Yes, Focus is completely open source. You can view the source code, report issues, and contribute to development on our <a href="https://github.com/AryanVBW/focus">GitHub repository</a>.</p>

        <h3>How can I report a bug or request a feature?</h3>
        <p>Please visit our <a href="https://github.com/AryanVBW/focus/issues">GitHub Issues page</a> to report bugs or request new features.</p>

        <h3>Can I contribute to Focus development?</h3>
        <p>Absolutely! We welcome contributions. Please check our GitHub repository for contribution guidelines and open issues that need help.</p>
      `,
      toc: [],
      full: false
    }
  },
  {
    url: '/privacy',
    data: {
      title: 'Privacy & Security',
      description: 'Learn about Focus privacy practices and security features',
      content: `
        <h2>Privacy-First Approach</h2>
        <p>Focus is built with privacy as a core principle. We believe your digital habits and usage patterns are personal information that should remain under your control.</p>

        <h2>Data Collection</h2>
        <h3>What We Don't Collect</h3>
        <ul>
          <li><strong>Personal Information</strong>: No names, emails, phone numbers, or other identifying information</li>
          <li><strong>Usage Data</strong>: No analytics, telemetry, or usage statistics sent to our servers</li>
          <li><strong>App Content</strong>: No screenshots, text content, or media from blocked apps</li>
          <li><strong>Location Data</strong>: No GPS coordinates or location tracking</li>
          <li><strong>Device Information</strong>: No device IDs, hardware specs, or system information</li>
        </ul>

        <h3>What We Do Collect</h3>
        <p><strong>Nothing.</strong> Focus operates entirely offline and does not transmit any data to external servers.</p>

        <h2>Local Data Storage</h2>
        <h3>What's Stored Locally</h3>
        <p>Focus stores the following information locally on your device:</p>
        <ul>
          <li><strong>App Settings</strong>: Your blocking preferences and configuration</li>
          <li><strong>Usage Statistics</strong>: Time spent in apps and blocking events (optional)</li>
          <li><strong>Focus Session History</strong>: Records of your focus sessions and their duration</li>
          <li><strong>Blocked Content Logs</strong>: Timestamps of when content was blocked (no content details)</li>
        </ul>

        <h3>Data Security</h3>
        <ul>
          <li><strong>Private Storage</strong>: All data is stored in Focus's private app directory</li>
          <li><strong>No External Access</strong>: Other apps cannot access Focus data without root privileges</li>
          <li><strong>Automatic Cleanup</strong>: Data is automatically deleted when you uninstall Focus</li>
          <li><strong>No Backups</strong>: Data is not included in Android system backups</li>
        </ul>

        <h2>Permissions Explained</h2>
        <h3>Accessibility Service</h3>
        <p><strong>Purpose</strong>: Detect specific UI elements in supported apps to identify distracting content.</p>
        <p><strong>What it accesses</strong>: UI structure and text of apps you're using (processed locally, never stored or transmitted).</p>
        <p><strong>Why it's needed</strong>: This is the core functionality that allows Focus to identify and block distracting features like Instagram Reels or YouTube Shorts.</p>

        <h3>Usage Access</h3>
        <p><strong>Purpose</strong>: Track how much time you spend in different apps.</p>
        <p><strong>What it accesses</strong>: App usage duration and frequency (stored locally only).</p>
        <p><strong>Why it's needed</strong>: Provides usage analytics to help you understand your digital habits.</p>

        <h3>Display Over Other Apps</h3>
        <p><strong>Purpose</strong>: Show blocking screens when distracting content is detected.</p>
        <p><strong>What it accesses</strong>: Ability to display Focus UI over other apps.</p>
        <p><strong>Why it's needed</strong>: Essential for blocking functionality - shows you why content was blocked and provides options to continue or stay focused.</p>

        <h3>Query All Packages</h3>
        <p><strong>Purpose</strong>: Show list of installed apps for selection in Focus settings.</p>
        <p><strong>What it accesses</strong>: Names and icons of installed apps.</p>
        <p><strong>Why it's needed</strong>: Allows you to choose which apps to monitor or block in Focus Mode.</p>

        <h2>Third-Party Services</h2>
        <p><strong>None.</strong> Focus does not integrate with any third-party services, analytics platforms, or advertising networks.</p>

        <h2>Open Source Transparency</h2>
        <p>Focus is completely open source, which means:</p>
        <ul>
          <li><strong>Full Code Review</strong>: Anyone can examine the source code to verify our privacy claims</li>
          <li><strong>Community Oversight</strong>: The developer community can identify and report any privacy concerns</li>
          <li><strong>No Hidden Functionality</strong>: All app behavior is visible in the public source code</li>
          <li><strong>Build Your Own</strong>: You can compile and install Focus from source code if desired</li>
        </ul>

        <h2>Future Privacy Considerations</h2>
        <h3>Planned Features (V2)</h3>
        <p>Future versions may include optional cloud features:</p>
        <ul>
          <li><strong>Cross-Device Sync</strong>: Sync settings across multiple devices (opt-in only)</li>
          <li><strong>Family Sharing</strong>: Share focus goals with family members (explicit consent required)</li>
          <li><strong>Progress Sharing</strong>: Share achievements with friends (user-controlled)</li>
        </ul>

        <h3>Privacy Commitment</h3>
        <p>Even with future cloud features:</p>
        <ul>
          <li>All cloud features will be <strong>opt-in only</strong></li>
          <li>You can continue using Focus completely offline</li>
          <li>Any data sync will use <strong>end-to-end encryption</strong></li>
          <li>You'll have full control over what data (if any) is shared</li>
        </ul>

        <h2>Contact & Questions</h2>
        <p>If you have questions about Focus privacy practices:</p>
        <ul>
          <li>Review our <a href="https://github.com/AryanVBW/focus">open source code</a></li>
          <li>Ask questions on our <a href="https://github.com/AryanVBW/focus/issues">GitHub Issues page</a></li>
          <li>Check our <a href="#documentation/faq">FAQ section</a> for common privacy questions</li>
        </ul>
      `,
      toc: [],
      full: false
    }
  }
];

export const source = {
  getPage: (slug: string | string[]) => {
    const slugPath = Array.isArray(slug) ? `/${slug.join('/')}` : `/${slug}`;
    const normalizedPath = slugPath === '/index' ? '/index' : slugPath;
    return mockPages.find(page => page.url === normalizedPath);
  },
  getPages: () => mockPages,
  pageTree: mockPages.map(page => ({
    name: page.data.title,
    url: page.url,
    children: []
  }))
};
