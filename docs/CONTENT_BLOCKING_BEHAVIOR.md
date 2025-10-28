# Content Blocking Behavior - Focus App

## Overview

The Focus app implements **selective content blocking** that closes only distracting content (like short-form videos) while keeping the rest of the app functional. This document explains how the blocking behavior works for each supported app.

## Key Principle

**The app does NOT block entire applications. It only closes the specific distracting feature (short videos/reels) and lets users continue using other parts of the app.**

## Smart Blocking Mechanism (Enhanced)

### How It Works

Focus uses an intelligent **two-tier strategy** specifically for short-form video content:

1. **Detection**: The accessibility service detects when a user opens distracting content (Reels, Shorts, Stories, etc.)
2. **Strategy 1 - Smart Redirection (Primary)**: Attempts to navigate to a safe section within the app (e.g., Home tab, Search tab)
3. **Strategy 2 - Back Navigation (Fallback)**: If redirection fails, performs a single `GLOBAL_ACTION_BACK` (back button press)
4. **Result**: The distracting content player closes, returning the user to a safe screen within the app
5. **Outcome**: The user can continue using other features of the app normally

### Why This Two-Tier Approach?

**Primary Strategy - Smart Redirection:**
- Navigates directly to safe tabs (Home, Search, etc.)
- More reliable than back navigation
- Prevents edge cases where back navigation might close the entire app
- Provides better user experience with predictable navigation

**Fallback Strategy - Back Navigation:**
- Mimics a natural user action (pressing the back button)
- Works when specific UI elements aren't found
- Consistent across all Android versions
- Doesn't require root access or special permissions

### Throttling Protection

To prevent multiple rapid blocking events that could close the entire app:
- **Back Press Cooldown**: 1 second between back presses
- **Event Throttling**: 300ms between blocking events for the same app
- **Single Action Guarantee**: Only one blocking action per detection event

## Blocking Actions

The app supports three blocking actions, but **for short-form video content (Reels/Shorts/Stories), it ALWAYS uses "Close Player" mode** regardless of user settings:

### Close Player (Default & Forced for Short Videos)
- ✅ **Recommended for all users**
- Only closes the player/shorts viewer
- Keeps the app open for other features
- Uses smart redirection + back navigation

### Close App
- ❌ **Not used for short videos** (overridden by app)
- Forces the entire app to close
- Only applies to other content types if user selects this

### Lock Screen
- 🔒 Can be selected for other content types
- Locks the device screen entirely
- Not applied to short videos (overridden)

## App-Specific Behavior

### 1. YouTube

#### What Gets Blocked
- **YouTube Shorts** - The short-form vertical video player

#### What Remains Accessible
- Regular long-form videos
- Home feed
- Subscriptions
- Search
- Library
- Comments and interactions

#### Blocking Behavior
When a user opens YouTube Shorts:
1. The Shorts player is detected
2. Back navigation is triggered
3. User returns to the previous YouTube screen (Home, Search, etc.)
4. User can continue watching regular videos

**Detection Methods:**
- Text indicators: "shorts", "Short"
- URL patterns: `/shorts/`
- View IDs: `shorts_container`
- UI patterns: Vertical scrolling with Like/Comments buttons

### 2. Instagram

#### What Gets Blocked
- **Instagram Reels** - The short-form vertical video player

#### What Remains Accessible
- Normal feed posts
- Stories (if not in Focus Mode)
- Direct messages
- Profile viewing
- Search and Explore (non-Reels content)
- IGTV/long-form videos

#### Blocking Behavior
When a user opens Instagram Reels:
1. The Reels player is detected
2. Back navigation is triggered
3. User returns to the previous Instagram screen (Feed, Profile, etc.)
4. User can continue using Instagram normally

**Detection Methods:**
- Text indicators: "reels", "Reel"
- View IDs: `clips_viewer_view_pager`, `reel_viewer`
- UI patterns: Vertical scrolling video container
- Content descriptions containing "reel"

### 3. Snapchat

#### What Gets Blocked
- **Spotlight** - Short-form video feed
- **Stories** - Story viewing interface (when enabled)

#### What Remains Accessible
- Chat/messaging
- Camera
- Snap Map
- Profile
- Memories

#### Blocking Behavior
When a user opens Spotlight or Stories:
1. The content is detected
2. Back navigation is triggered
3. User returns to Camera or Chat screen
4. User can continue using Snapchat for messaging

**Detection Methods:**
- Text indicators: "spotlight", "story", "stories"
- View IDs: Snapchat-specific identifiers
- UI patterns: Full-screen video players

### 4. Facebook

#### What Gets Blocked
- **Facebook Reels** - Short-form video player
- **Short videos** in the feed

#### What Remains Accessible
- Normal feed posts
- Comments and interactions
- Messenger
- Groups
- Marketplace
- Profile viewing

#### Blocking Behavior
When a user opens Facebook Reels:
1. The Reels player is detected
2. Back navigation is triggered
3. User returns to the Facebook feed
4. User can continue browsing Facebook normally

**Detection Methods:**
- Text indicators: "reels", "Reel"
- View IDs: Facebook-specific video player IDs
- UI patterns: Vertical scrolling video interface

### 5. TikTok

#### What Gets Blocked
- **All TikTok content** (as the entire app is short-form video)

#### Blocking Behavior
TikTok is a special case because the entire app is designed around short-form videos:
1. When TikTok is opened, content is detected immediately
2. Back navigation is triggered
3. User exits TikTok
4. This effectively blocks the app since there's no non-video content

**Note:** TikTok cannot have selective blocking because the app's primary purpose is short-form videos.

### 6. Twitter/X

#### What Gets Blocked
- **Video content** in the feed (when detected)

#### What Remains Accessible
- Text tweets
- Images
- Threads
- Comments
- Profile viewing
- Search

#### Blocking Behavior
When video content is detected:
1. The video player is identified
2. Back navigation is triggered
3. User returns to the feed
4. User can continue reading tweets

## Blocking Modes

### Normal Mode (Default)

**Behavior:**
- Blocks only short-form video content (Reels, Shorts, Spotlight)
- Allows normal app usage
- Uses back navigation to close players
- Non-intrusive and seamless

**Apps Affected:**
- Instagram (Reels only)
- YouTube (Shorts only)
- Snapchat (Spotlight/Stories only)
- Facebook (Reels only)
- TikTok (entire app)

### Focus Mode (Enhanced Blocking)

**Behavior:**
- Blocks short-form video content (same as Normal Mode)
- Additionally blocks entire apps if configured
- Shows `BlockedPageActivity` for fully blocked apps
- More aggressive blocking for deep focus sessions

**Apps Affected:**
- All apps in the blocked list
- Can be configured per-app

## Technical Implementation

### Detection Flow

```
User opens distracting content
    ↓
Accessibility Event Triggered
    ↓
FocusAccessibilityService.onAccessibilityEvent()
    ↓
Content Detection (VideoContentDetector + ContentDetector)
    ↓
Is distracting content? (Reels/Shorts/etc.)
    ↓
YES → Execute Blocking Action
    ↓
performGlobalAction(GLOBAL_ACTION_BACK)
    ↓
Player closes, user returns to previous screen
    ↓
User continues using app normally
```

### Code Components

1. **FocusAccessibilityService.kt**
   - Main service that monitors accessibility events
   - Coordinates detection and blocking

2. **ContentDetectionCoordinator.kt**
   - Combines multiple detection methods
   - Determines optimal blocking strategy

3. **VideoContentDetector.kt**
   - Specialized detector for video content
   - Identifies Reels, Shorts, and other video players

4. **BlockingActionHandler.kt**
   - Executes blocking actions
   - `closePlayer()` method performs back navigation

5. **ScrollBlockingHandler.kt**
   - Handles scroll-based blocking
   - Prevents scrolling in video feeds

## User Experience

### What Users See

1. **Opening Reels/Shorts:**
   - Content starts to load
   - Immediately returns to previous screen
   - Brief toast notification: "Focus: returned to previous screen"
   - Optional notification showing what was blocked

2. **Continuing App Usage:**
   - All other features work normally
   - No interruption to regular content
   - Seamless experience

### Customization Options

Users can configure:
- **Blocking Action**: Close player (default), Close app, or Lock screen
- **Notifications**: Show/hide block notifications
- **Temporary Unblock**: Disable blocking for a period
- **Per-App Settings**: Enable/disable blocking per app

## Troubleshooting

### Issue: Entire App Gets Blocked

**Cause:** App is in the "Blocked Apps" list (Focus Mode)

**Solution:**
1. Open Focus app settings
2. Go to "Blocked Apps"
3. Remove the app from the list
4. The app will now only have content-specific blocking

### Issue: Content Not Being Blocked

**Possible Causes:**
1. Accessibility service not enabled
2. App not in monitored apps list
3. Detection patterns outdated (app UI changed)

**Solutions:**
1. Enable accessibility service in Android Settings
2. Add app to monitored apps in Focus settings
3. Update Focus app to latest version

### Issue: Blocking Too Aggressive

**Solution:**
1. Adjust blocking sensitivity in settings
2. Use "Temporary Unblock" feature
3. Customize blocking action to be less disruptive

## Best Practices

### For Users

1. **Start with Normal Mode** - Less intrusive, blocks only distracting content
2. **Use Focus Mode for Deep Work** - When you need complete app blocking
3. **Customize Per App** - Different apps may need different settings
4. **Use Temporary Unblock** - When you need to access blocked content briefly

### For Developers

1. **Test Detection Thoroughly** - Ensure only target content is detected
2. **Use Back Navigation** - Most reliable and non-disruptive method
3. **Implement Throttling** - Prevent rapid repeated blocking
4. **Provide User Feedback** - Show what was blocked and why
5. **Allow Customization** - Let users control blocking behavior

## Future Enhancements

Potential improvements:
1. **Smart Detection** - ML-based content detection
2. **Time-Based Rules** - Block content only during certain hours
3. **Usage Limits** - Allow limited access to blocked content
4. **Whitelist Mode** - Allow specific accounts/channels
5. **Gradual Blocking** - Warn before blocking

## Summary

The Focus app implements **selective, non-intrusive content blocking** that:
- ✅ Closes only distracting content (Reels, Shorts, etc.)
- ✅ Keeps the rest of the app functional
- ✅ Uses natural back navigation
- ✅ Provides seamless user experience
- ✅ Allows customization per app
- ✅ Works without root access

**The key principle: Block the distraction, not the app.**

---

*Last Updated: 2025-10-07*
*Version: 2.0*

