#!/bin/bash

echo "Focus App - Accessibility Permission Troubleshooting Script"
echo "========================================================="
echo ""

# Check if adb is available
if ! command -v adb &> /dev/null; then
    echo "❌ ADB is not installed or not in PATH"
    echo "This script requires Android Debug Bridge (ADB) to be installed."
    echo "Please install Android SDK Platform Tools."
    exit 1
fi

# Check if device is connected
if ! adb devices | grep -q "device$"; then
    echo "❌ No Android device connected"
    echo "Please connect your Android device and enable USB debugging."
    exit 1
fi

echo "✅ Device connected. Running diagnostics..."
echo ""

# Check if Focus app is installed
APP_PACKAGE="com.aryanvbw.focus"
if adb shell pm list packages | grep -q "$APP_PACKAGE"; then
    echo "✅ Focus app is installed"
else
    echo "❌ Focus app is not installed"
    echo "Please install the Focus app first."
    exit 1
fi

# Get device information
echo "📱 Device Information:"
echo "  Manufacturer: $(adb shell getprop ro.product.manufacturer)"
echo "  Model: $(adb shell getprop ro.product.model)"  
echo "  Android Version: $(adb shell getprop ro.build.version.release)"
echo "  API Level: $(adb shell getprop ro.build.version.sdk)"
echo ""

# Check app version
echo "📦 App Information:"
APP_VERSION=$(adb shell dumpsys package $APP_PACKAGE | grep "versionName" | head -1 | cut -d'=' -f2)
echo "  Version: $APP_VERSION"
echo ""

# Check accessibility settings
echo "🔍 Accessibility Service Status:"
ENABLED_SERVICES=$(adb shell settings get secure enabled_accessibility_services)
if echo "$ENABLED_SERVICES" | grep -q "$APP_PACKAGE"; then
    echo "  ✅ Focus accessibility service is ENABLED"
else
    echo "  ❌ Focus accessibility service is NOT ENABLED"
    echo ""
    echo "🔧 To fix this:"
    echo "  1. Go to Settings > Accessibility on your device"
    echo "  2. Look for 'Focus Blocker' or 'Focus' in the list"
    echo "  3. Toggle it ON"
    echo "  4. Tap 'Allow' when prompted"
fi

echo ""
echo "Enabled accessibility services:"
echo "$ENABLED_SERVICES"
echo ""

# Check app permissions
echo "🔐 App Permissions:"
PERMISSIONS=$(adb shell dumpsys package $APP_PACKAGE | grep "permission\." | head -10)
echo "$PERMISSIONS"
echo ""

# Check if app is being battery optimized
echo "🔋 Battery Optimization:"
if adb shell dumpsys deviceidle | grep -q "$APP_PACKAGE"; then
    echo "  ⚠️  Focus app may be affected by battery optimization"
    echo "  Consider adding Focus to the battery whitelist"
else
    echo "  ✅ Focus app is not being battery optimized"
fi

echo ""

# Check for recent crashes
echo "📝 Recent Logs (last 50 lines):"
echo "Looking for Focus-related errors..."
adb logcat -d | grep -i "focus\|accessibility" | tail -20

echo ""
echo "🚀 Quick Test:"
echo "1. Open Instagram on your device"
echo "2. Navigate to Reels section"  
echo "3. The app should navigate back if working correctly"
echo ""
echo "Monitor real-time logs with:"
echo "  adb logcat -s FocusAccessibilityService"
echo ""

# Offer to open accessibility settings
read -p "Would you like to open Accessibility Settings on your device? (y/n): " -n 1 -r
echo
if [[ $REPLY =~ ^[Yy]$ ]]; then
    echo "Opening Accessibility Settings..."
    adb shell am start -a android.settings.ACCESSIBILITY_SETTINGS
    echo "Look for 'Focus Blocker' and enable it."
fi

echo ""
echo "Troubleshooting complete!"
echo "If issues persist, please share the output above with support."
