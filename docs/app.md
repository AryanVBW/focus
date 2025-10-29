# Focus App - Enhanced Video Content Detection and Scroll Blocking

## Overview

The Focus app has been updated with a proper directory structure and enhanced logic for blocking scrolling on reels and short videos. The new architecture provides more sophisticated content detection and blocking capabilities while maintaining backward compatibility.

## 📚 Documentation

For comprehensive documentation, please refer to:
- **[Documentation Index](docs/INDEX.md)** - Complete guide to all documentation
- **[Implementation Guides](docs/implementation/)** - Technical implementation details
- **[Feature Documentation](docs/features/)** - Feature-specific guides
- **[Troubleshooting](docs/troubleshooting/)** - Common issues and solutions
- **[Scripts](scripts/)** - Utility scripts for development

## 🏗️ Project Structure

```
Focus-app/
├── app/                          # Main Android application
│   └── src/main/java/com/focus/app/
│       ├── blocking/             # Scroll blocking strategies
│       ├── detection/            # Content detection system
│       ├── model/               # Data models
│       ├── ui/                  # User interface
│       ├── service/             # Background services
│       └── database/            # Data persistence
├── docs/                        # 📖 Documentation
│   ├── INDEX.md                # Documentation index
│   ├── implementation/         # Implementation guides
│   ├── features/               # Feature documentation
│   ├── troubleshooting/        # Troubleshooting guides
│   └── RELEASE.md              # Release notes
├── scripts/                     # 🛠️ Utility scripts
│   ├── fix_packages.sh         # Package fixing utility
│   └── troubleshoot_accessibility.sh
├── backups/                     # Code backups
├── keystore/                    # Release signing
└── gradle-wrapper/              # Gradle wrapper
```

## Enhanced Directory Structure

### New Package Organization

```
com.focus.app/
├── blocking/
│   └── ScrollBlockingHandler.kt          # Enhanced scroll blocking strategies
├── detection/
│   ├── ContentDetectionCoordinator.kt    # Unified detection coordinator
│   ├── VideoContentDetector.kt           # Enhanced video content detection
│   └── model/
│       ├── ScrollDirection.kt            # Scroll direction enumeration
│       └── VideoDetectionResult.kt       # Detection result data model
├── data/                                 # Existing data layer
├── service/                              # Existing service layer
├── ui/                                   # Existing UI layer
└── util/                                 # Existing utility classes
```

## Enhanced Features

### 1. Advanced Video Content Detection

- **Multi-strategy Detection**: Combines legacy detection with enhanced video-specific detection
- **Confidence-based Results**: Uses confidence scoring to determine the most reliable detection method
- **App-specific Logic**: Tailored detection strategies for Instagram, YouTube, TikTok, Facebook, and Snapchat
- **Scroll Direction Analysis**: Detects vertical and horizontal scrolling patterns characteristic of short-form video content

### 2. Intelligent Scroll Blocking

- **Multiple Blocking Strategies**:
  - **Back Navigation**: Traditional back button approach
  - **Counter-scrolling**: Prevents scroll momentum in video feeds
  - **App Redirect**: Redirects users to safe sections within apps
  - **Hybrid Approach**: Combines multiple strategies for maximum effectiveness

- **App-specific Safe Navigation**:
  - **Instagram**: Redirects from Reels to Home, Search, or Profile
  - **Snapchat**: Redirects from Stories/Spotlight to Chat or Camera
  - **YouTube**: Redirects from Shorts to Home, Subscriptions, or Library

### 3. Enhanced Detection Capabilities

- **Video Player Recognition**: Detects various video player components (VideoView, PlayerView, ExoPlayer)
- **UI Pattern Analysis**: Identifies characteristic layouts of short-form video interfaces
- **Content Description Analysis**: Uses accessibility labels to identify video content
- **Scroll Container Detection**: Finds and analyzes scrollable containers that may contain video feeds

## Key Components

### ContentDetectionCoordinator

The central coordinator that:
- Combines legacy and enhanced detection methods
- Determines optimal blocking strategies based on app and content type
- Executes blocking actions with fallback mechanisms
- Provides detection statistics and confidence metrics

### VideoContentDetector

Enhanced detection engine that:
- Analyzes accessibility trees for video content indicators
- Detects scroll patterns characteristic of short-form video
- Provides confidence-based detection results
- Supports multiple detection strategies per app

### ScrollBlockingHandler

Advanced blocking system that:
- Implements multiple blocking strategies
- Provides app-specific safe navigation
- Handles counter-scrolling and gesture blocking
- Includes fallback mechanisms for failed redirections

## Configuration Options

The enhanced system adds new configuration options in AppSettings:

- `isEnhancedVideoDetectionEnabled()`: Enable/disable enhanced detection
- `isScrollBlockingEnabled(packageName)`: Per-app scroll blocking control
- `getVideoDetectionConfidenceThreshold()`: Minimum confidence for detection
- `isCounterScrollingEnabled()`: Enable counter-scrolling blocking
- `isAppRedirectEnabled()`: Enable app redirection blocking

## Backward Compatibility

The enhanced system maintains full backward compatibility:
- Legacy ContentDetector continues to function
- Existing settings and preferences are preserved
- Gradual migration path from legacy to enhanced detection
- Fallback mechanisms ensure continued functionality

## Usage

The enhanced detection system is automatically integrated into the FocusAccessibilityService. When enabled, it:

1. Monitors accessibility events for supported apps
2. Analyzes content using both legacy and enhanced detection
3. Determines the optimal blocking strategy based on confidence and app type
4. Executes blocking with appropriate fallback mechanisms
5. Logs detection events and provides user notifications

## Supported Apps

- **Instagram**: Reels detection and redirection to safe sections
- **YouTube**: Shorts detection with navigation to Home/Subscriptions
- **TikTok**: Full video feed detection and blocking
- **Snapchat**: Stories/Spotlight detection with Chat/Camera redirection
- **Facebook**: Reels and video content detection
- **Twitter**: Video content detection in feeds

## Technical Implementation

### Detection Methods

1. **Legacy Detection**: Uses existing text and view ID matching
2. **Video Detection**: Analyzes video player components and UI patterns
3. **Combined Detection**: Merges results from both methods with confidence weighting
4. **Enhanced Video**: Advanced analysis with scroll pattern recognition

### Blocking Strategies

1. **Back Navigation**: Simple back button press
2. **Scroll Blocking**: Prevents scroll gestures in video feeds
3. **App Redirect**: Navigates to safe app sections
4. **Counter Gesture**: Actively counters user scroll attempts
5. **Hybrid**: Combines multiple strategies for maximum effectiveness

## Future Enhancements

- Machine learning-based content detection
- Real-time scroll pattern analysis
- User behavior adaptation
- Cross-app content correlation
- Advanced gesture recognition and blocking