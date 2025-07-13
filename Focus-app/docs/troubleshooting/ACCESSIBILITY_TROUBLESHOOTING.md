# Accessibility Permission Troubleshooting Guide

## Common Issues and Solutions

### 1. Service Not Appearing in Accessibility Settings
**Symptoms:** The Focus app doesn't show up in Android Settings > Accessibility

**Possible Causes:**
- App not properly installed
- Manifest configuration issues
- Target SDK compatibility issues

**Solutions:**
1. **Reinstall the app completely:**
   ```bash
   adb uninstall com.aryanvbw.focus
   ./gradlew assembleDebug
   adb install app/build/outputs/apk/debug/app-debug.apk
   ```

2. **Check Android version compatibility:**
   - Android 6.0+ required for accessibility services
   - Some newer Android versions have stricter requirements

3. **Clear app data and cache:**
   ```bash
   adb shell pm clear com.aryanvbw.focus
   ```

### 2. Service Appears but Can't Be Enabled
**Symptoms:** Service shows in accessibility settings but toggle doesn't work or immediately turns off

**Possible Causes:**
- Permission conflicts
- Service crashes on startup
- Missing required permissions

**Solutions:**
1. **Check logcat for errors:**
   ```bash
   adb logcat -s FocusAccessibilityService AccessibilityManager
   ```

2. **Grant all required permissions manually:**
   ```bash
   adb shell pm grant com.aryanvbw.focus android.permission.SYSTEM_ALERT_WINDOW
   adb shell appops set com.aryanvbw.focus SYSTEM_ALERT_WINDOW allow
   ```

### 3. Service Enables but Doesn't Work
**Symptoms:** Toggle is on but app doesn't block content

**Possible Causes:**
- Service not properly initialized
- Event handling issues
- App settings not configured

**Solutions:**
1. **Check service initialization:**
   - Monitor logs for "FocusAccessibilityService" tags
   - Verify service startup messages

2. **Test with simple apps first:**
   - Try Instagram Reels blocking first
   - Gradually test other features

### 4. Battery Optimization Issues
**Symptoms:** Service works initially but stops after some time

**Solutions:**
1. **Disable battery optimization:**
   ```bash
   adb shell dumpsys deviceidle whitelist +com.aryanvbw.focus
   ```

2. **Add to battery whitelist in device settings**

### 5. Device-Specific Issues

**Samsung Devices:**
- May require additional permissions
- Check "Special Access" settings
- Disable "Put unused apps to sleep"

**Xiaomi/MIUI:**
- Enable AutoStart permission
- Disable MIUI Optimization temporarily
- Check "Other permissions" section

**OnePlus/OxygenOS:**
- Check "Advanced" permissions
- Enable "Display over other apps"

**Huawei/EMUI:**
- Enable "Protected apps"
- Check "Battery optimization" settings

## Testing Steps

### 1. Manual Testing
1. Install app
2. Go to Android Settings > Accessibility
3. Look for "Focus Blocker" or "Focus"
4. Enable the service
5. Grant any additional permissions requested
6. Test with Instagram Reels

### 2. Debug Testing
```bash
# Enable verbose logging
adb shell setprop log.tag.FocusAccessibilityService VERBOSE

# Monitor accessibility events
adb logcat -s AccessibilityService FocusAccessibilityService

# Check service status
adb shell service list | grep accessibility
```

### 3. Automated Testing Script
```bash
#!/bin/bash
echo "Focus App Accessibility Troubleshooting"
echo "======================================="

echo "1. Checking if app is installed..."
adb shell pm list packages | grep com.aryanvbw.focus

echo "2. Checking accessibility services..."
adb shell settings get secure enabled_accessibility_services

echo "3. Checking app permissions..."
adb shell dumpsys package com.aryanvbw.focus | grep permission

echo "4. Checking battery optimization..."
adb shell dumpsys deviceidle | grep com.aryanvbw.focus

echo "5. Testing accessibility events..."
echo "Please open Instagram and navigate to Reels, then check logs"
adb logcat -s FocusAccessibilityService
```

## Code Improvements Needed

### 1. Better Error Handling in Service
The current service could benefit from:
- More detailed error logging
- Graceful fallback mechanisms
- Better initialization checks

### 2. Enhanced Permission Detection
- Real-time permission status monitoring
- Better user guidance for device-specific settings
- Automatic retry mechanisms

### 3. Improved User Experience
- Clear step-by-step instructions per device manufacturer
- Visual indicators of service status
- Troubleshooting tips in-app
