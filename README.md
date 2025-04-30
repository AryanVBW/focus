# Focus

<p align="center">
  <img src="app/src/main/res/mipmap-xxxhdpi/ic_launcher.png" width="120" height="120" alt="Focus Logo"/>
</p>

<h3 align="center">Your time is only yours</h3>

<p align="center">Reclaim your attention by blocking digital distractions</p>

---

## 🎯 What is Focus?

Focus is a powerful Android application designed to help you regain control of your digital life. In an era of constant notifications and addictive content designed to capture your attention, Focus works as your digital gatekeeper, allowing you to be intentional with your time and attention.

## 🔍 The Problem

Modern social media apps are engineered to be addictive. Short-form video content like Instagram Reels, Snapchat Stories, and YouTube Shorts are particularly designed to keep you scrolling through an endless stream of dopamine-triggering content. Studies show this can lead to:

- Reduced productivity and focus
- Increased anxiety and FOMO (Fear of Missing Out)
- Sleep disruption and reduced quality of rest
- Decreased real-world social interactions

## 💡 Our Solution

Focus uses advanced detection algorithms and Android's Accessibility Service to identify and block distracting content, helping you maintain your attention on what truly matters. The app operates in two primary modes:

### ⚙️ Normal Mode

Selectively blocks the most addictive features while allowing you to use the core functionality of your apps:

- Use Instagram, but blocks Reels
- Use Snapchat, but blocks Stories and Spotlight
- Use YouTube, but blocks Shorts
- Use browsers, but blocks adult content
- And more!

### 🚫 Focus Mode

When you need to deeply concentrate, activate Focus Mode to completely block distracting apps:

- Completely blocks access to Instagram, TikTok, YouTube, etc.
- Shows a branded blocking page explaining why the app is blocked
- Set a timer or schedule for automatic activation during productive hours

## ✨ Key Features

### 🛑 Smart Content Blocking
- **Selective Feature Blocking**: Block only the most distracting features while preserving app utility
- **Intelligent Detection**: App uses advanced accessibility service to identify distracting content
- **Custom Blocking Pages**: Explains what's being blocked and why for better user experience

### 🔞 Adult Content Filter
- **Browser Protection**: Automatically detects and blocks adult websites across major browsers
- **Domain & Keyword Filtering**: Uses both domain and keyword matching for effective blocking
- **Safe Browsing Redirection**: Offers option to redirect to safe sites when adult content is detected

### 📱 App Management
- **Custom App Blocking**: Choose which applications to block or monitor
- **Usage Statistics**: Track your digital wellbeing and progress
- **Time Limits**: Set daily time limits for specific apps

### 🔔 Notification Control
- **Distraction-Free**: Option to block notifications from distracting apps
- **Customizable Alerts**: Get reminders when you're spending too much time on certain apps

## 📱 How It Works

Focus utilizes Android's Accessibility Services to monitor screen content in real-time without requiring root access. This allows the app to:

1. **Detect Content Types**: Identify when you're viewing distracting content like reels or stories
2. **Recognize Apps**: Know when you're using applications that might distract you
3. **Take Action**: Block the distraction and redirect you to productive alternatives

## 🔐 Privacy & Permissions

At Focus, we take your privacy seriously. The app:

- **Operates Entirely On-Device**: No data is sent to external servers
- **No Personal Data Collection**: We don't track or store your personal information
- **Transparent Permissions**: We clearly explain why each permission is needed

### Required Permissions

- **Accessibility Service**: Needed to detect and block distracting content
- **Usage Stats Permission**: For tracking app usage time (optional)
- **Display Over Other Apps**: To show blocking pages when distractions are detected
- **Query All Packages**: To show list of installed apps for selection

## 💾 Installation & Setup

1. **Download**: Get the APK from the [Releases](https://github.com/AryanVBW/focus/releases) page
2. **Install**: Open the APK and follow installation prompts
3. **Initial Setup**:
   - Grant Accessibility Service permission
   - Choose which apps to monitor
   - Configure blocking preferences
4. **Customize**: Set up Focus Mode timers, notification preferences, and blocking levels

## 💻 Technical Details

### Architecture

Focus is built using modern Android development practices:

- **Kotlin**: 100% Kotlin for type safety and modern syntax
- **MVVM Architecture**: For clean separation of UI and business logic
- **Room Database**: For local data persistence
- **Coroutines**: For asynchronous operations
- **Jetpack Libraries**: Utilizing the best of Android's official components

### Accessibility Service

The core of Focus relies on Android's Accessibility API to detect and respond to screen content:

- **Content Detection**: Pattern matching and element identification to detect distracting content
- **Non-Invasive**: Does not modify other apps or require root access
- **Battery Efficient**: Optimized to minimize battery impact

## 👏 Acknowledgments

- Focus logo designed with simplicity and purpose in mind - a bullseye target symbolizing focused attention with a red background (#E94057) and white elements
- Thanks to all the users who provided feedback and helped improve the app

## 📝 Future Roadmap

- **App Time Limits**: Set specific time limits for individual apps
- **Block Schedules**: Configure automatic blocking during specific times of day
- **Website/Domain Blocking**: Add custom website blocking in browsers
- **Statistics Dashboard**: Enhanced analytics about your digital habits
- **Focus Challenges**: Complete challenges to build better digital habits

## 🎁 Contributing

Focus is an ongoing project, and we welcome contributions! If you'd like to contribute:

1. Fork the repository
2. Create a feature branch
3. Submit a pull request with your changes

## 👋 Conclusion

Focus is more than just an app - it's a tool to help you reclaim your most valuable resource: your attention. In a world designed to distract, Focus helps you stay intentional about how you spend your time and mental energy.

By blocking the most addictive aspects of social media and providing awareness about your digital habits, Focus aims to create a healthier relationship with technology.

Download Focus today and take the first step toward a more intentional digital life!
