# Testing Guide - Blocking Behavior Fix

## Overview

This guide provides step-by-step instructions to test the blocking behavior fix that ensures only the player/shorts viewer closes (not the entire app) when Instagram Reels or YouTube Shorts are detected.

## Prerequisites

1. **Build and Install**: Ensure the latest version of the Focus app is installed
2. **Permissions**: Grant all required permissions (Accessibility, Usage Stats)
3. **Test Apps**: Install Instagram, YouTube, and Snapchat on the test device
4. **Enable Service**: Ensure Focus accessibility service is enabled in device settings

## Test Scenarios

### Test 1: Instagram Reels - Basic Behavior

**Purpose**: Verify that only Reels player closes, not the entire Instagram app

**Steps**:
1. Open Focus app
2. Go to Dashboard → Check blocking status widget shows "Player Only Mode (Active)"
3. Go to Settings → Verify "Blocking Action" shows info card explaining behavior
4. Open Instagram app
5. Navigate to Reels tab (usually at the bottom center)
6. Wait for a Reel to start playing

**Expected Result**:
- ✅ Reel player closes immediately OR redirects to Home feed
- ✅ Instagram app stays open
- ✅ You see the Instagram feed/home screen
- ✅ Toast message appears: "Focus: Returned to Instagram feed" or "Focus: Closed reels player"
- ✅ Haptic feedback (vibration) occurs (if enabled)

**Failure Indicators**:
- ❌ Instagram closes entirely and you see home screen
- ❌ Multiple rapid back actions occur
- ❌ No feedback provided

### Test 2: YouTube Shorts - Basic Behavior

**Purpose**: Verify that only Shorts player closes, not the entire YouTube app

**Steps**:
1. Ensure Focus service is active
2. Open YouTube app
3. Navigate to Shorts tab (swipe from bottom or tap Shorts icon)
4. Or: Find a Short in the home feed and tap it
5. Wait for Short to start playing

**Expected Result**:
- ✅ Short player closes immediately OR redirects to Home/Subscriptions
- ✅ YouTube app stays open
- ✅ You see YouTube home screen or previous screen
- ✅ Toast message appears: "Focus: Returned to YouTube feed" or "Focus: Closed shorts player"
- ✅ Haptic feedback occurs

**Failure Indicators**:
- ❌ YouTube closes entirely
- ❌ Multiple rapid actions
- ❌ App becomes unresponsive

### Test 3: Snapchat Stories/Spotlight - Basic Behavior

**Purpose**: Verify Snapchat blocking works correctly

**Steps**:
1. Ensure Focus service is active
2. Open Snapchat app
3. Swipe to Stories or tap Spotlight tab
4. Wait for content to load

**Expected Result**:
- ✅ Story/Spotlight closes OR redirects to Chat/Camera
- ✅ Snapchat stays open
- ✅ You see Camera or Chat screen
- ✅ Toast message appears
- ✅ Haptic feedback occurs

### Test 4: Throttling Protection

**Purpose**: Verify that rapid repeated blocking doesn't occur

**Steps**:
1. Open Instagram
2. Quickly navigate to Reels tab
3. Immediately after blocking, try to open Reels again within 1 second
4. Repeat 3-4 times rapidly

**Expected Result**:
- ✅ First blocking action works normally
- ✅ Subsequent attempts within 1 second are throttled (no action)
- ✅ After 1 second cooldown, blocking works again
- ✅ No multiple rapid back presses
- ✅ App never closes entirely

**Check Logs**:
```
adb logcat | grep "BlockingActionHandler"
```
Look for: "Back press throttled - cooldown active"

### Test 5: Smart Redirection Priority

**Purpose**: Verify that smart redirection is tried before back navigation

**Steps**:
1. Open Instagram
2. Navigate directly to Reels tab (not from Home)
3. Observe the blocking behavior

**Expected Result**:
- ✅ First attempt: Tries to click Home/Search tab (smart redirection)
- ✅ If successful: Directly navigates to Home feed
- ✅ If fails: Falls back to single back press
- ✅ User sees smooth transition, not jarring back action

**Check Logs**:
```
adb logcat | grep "FocusAccessibilityService"
```
Look for:
- "Short video detected - using smart blocking strategy"
- "Successfully redirected from reels in com.instagram.android"
- OR "Redirection failed, using back navigation"

### Test 6: Migration System

**Purpose**: Verify default settings are correct on fresh install

**Steps**:
1. Uninstall Focus app completely
2. Reinstall from build
3. Launch app for first time
4. Go to Settings → Blocking Action

**Expected Result**:
- ✅ Blocking action is set to "Close Player Only" by default
- ✅ Info card explaining behavior is visible
- ✅ Dashboard widget shows "Player Only Mode (Active)"

**Check Logs**:
```
adb logcat | grep "AppSettings"
```
Look for: "Migration complete: Blocking action set to close_player"

### Test 7: Dashboard Widget

**Purpose**: Verify blocking status widget displays correctly

**Steps**:
1. Open Focus app
2. Go to Dashboard (Home) tab
3. Scroll to view the blocking status widget

**Expected Result**:
- ✅ Widget displays with "Blocking Protection" title
- ✅ Shows "Player Only Mode (Active)" status
- ✅ Description: "Short videos close automatically, apps stay open"
- ✅ Monitored apps list shows: Instagram, YouTube, Snapchat, etc.
- ✅ Settings icon button is visible and clickable

**Test Widget Refresh**:
1. Change blocking action in Settings
2. Return to Dashboard
3. Verify widget updates to show new mode

### Test 8: Settings UI Clarity

**Purpose**: Verify settings screen provides clear information

**Steps**:
1. Open Focus app → Settings
2. Scroll to "Blocking Action" section

**Expected Result**:
- ✅ Info card visible above blocking action setting
- ✅ Info card title: "📱 How Blocking Works for Short Videos"
- ✅ Info card explains that only player closes, not app
- ✅ Second info card: "ℹ️ Smart Blocking Strategy"
- ✅ Blocking action options have clear descriptions with emojis:
  - "✅ Close Player Only - Returns to previous screen (Recommended for social media)"
  - "❌ Close Entire App - Force stops the app completely"
  - "🔒 Lock Screen - Locks device to break scrolling habits"

### Test 9: Edge Case - Direct Reel Open

**Purpose**: Verify behavior when Reel is opened as first screen (no back stack)

**Steps**:
1. Share a Reel link to yourself
2. Open the shared Reel link from outside Instagram
3. Instagram opens directly to the Reel (no back stack)
4. Focus should detect and block

**Expected Result**:
- ✅ Smart redirection tries to navigate to Home tab
- ✅ If redirection succeeds: Goes to Home feed
- ✅ If redirection fails: Instagram might close (acceptable for edge case)
- ✅ No app crash or repeated actions

### Test 10: User Feedback Verification

**Purpose**: Verify all user feedback mechanisms work

**Steps**:
1. Enable haptic feedback in Focus settings
2. Enable block notifications in Focus settings
3. Open Instagram Reels

**Expected Result**:
- ✅ Haptic feedback: Short vibration (50ms) on blocking
- ✅ Toast message: Clear description of action taken
- ✅ Notification: Optional notification with blocking details
- ✅ All feedback is clear and not annoying

## Logging and Debugging

### Enable Detailed Logging

```bash
# Filter for Focus app logs
adb logcat | grep "Focus"

# Filter for specific components
adb logcat | grep "BlockingActionHandler"
adb logcat | grep "FocusAccessibilityService"
adb logcat | grep "AppSettings"

# Save to file
adb logcat | grep "Focus" > focus_logs.txt
```

### Key Log Messages to Look For

**Successful Blocking**:
```
Short video detected - using smart blocking strategy
Successfully redirected from reels in com.instagram.android
Executed single back navigation to close player
```

**Throttling Working**:
```
Back press throttled - cooldown active (XXXms remaining)
```

**Migration Success**:
```
Migration complete: Blocking action set to close_player
```

**Smart Redirection**:
```
Attempting to redirect to safe section in com.instagram.android
Successfully redirected to Instagram Home tab
```

## Performance Checks

### Response Time
- Blocking should occur within 100-300ms of content detection
- No noticeable lag or delay

### Battery Impact
- Monitor battery usage in device settings
- Focus should not significantly drain battery

### Memory Usage
- Check memory usage in Android Studio Profiler
- Should remain stable, no memory leaks

## Regression Testing

### Ensure Other Features Still Work

1. **Focus Mode Toggle**: Still activates/deactivates correctly
2. **Statistics**: Blocked count increments correctly
3. **App Limits**: Normal mode features still functional
4. **Settings**: All preferences save correctly
5. **Permissions**: Accessibility service doesn't require re-enabling

## Acceptance Criteria

For the fix to be considered successful, ALL of the following must be true:

- [ ] Instagram Reels: Only player closes, app stays open
- [ ] YouTube Shorts: Only player closes, app stays open
- [ ] Snapchat Stories: Only player closes, app stays open
- [ ] Throttling prevents multiple rapid actions
- [ ] Dashboard widget displays correctly
- [ ] Settings UI is clear and informative
- [ ] Migration sets correct defaults
- [ ] Logs show expected messages
- [ ] No crashes or ANRs (Application Not Responding)
- [ ] User feedback (haptic, toast, notification) works
- [ ] Performance is acceptable (no lag)

## Troubleshooting

### If Entire App Still Closes

1. Check logs for blocking action used:
   ```bash
   adb logcat | grep "Executing blocking action"
   ```
2. Verify migration ran successfully:
   ```bash
   adb logcat | grep "Migration"
   ```
3. Check if `getEffectiveBlockingAction()` is being called
4. Verify accessibility service is enabled

### If No Blocking Occurs

1. Verify Focus accessibility service is enabled
2. Check if app is in monitored apps list
3. Review detection logs:
   ```bash
   adb logcat | grep "detected"
   ```
4. Ensure Focus Mode or content blocking is enabled

### If Multiple Rapid Actions Occur

1. Check throttling logs:
   ```bash
   adb logcat | grep "throttled"
   ```
2. Verify `lastBackPressTime` is being updated
3. Check `BACK_PRESS_COOLDOWN` constant value

## Reporting Issues

If any test fails, report with:

1. **Device**: Model and Android version
2. **App Version**: Focus app version number
3. **Test Scenario**: Which test failed
4. **Actual Result**: What actually happened
5. **Logs**: Relevant logcat output
6. **Steps to Reproduce**: Exact steps that cause the issue
7. **Frequency**: Does it happen every time or intermittently?

## Test Report Template

```
Date: YYYY-MM-DD
Tester: Name
Device: Model / Android Version
Focus Version: X.X.X

Test Results:
[ ] Test 1: Instagram Reels - Basic Behavior
[ ] Test 2: YouTube Shorts - Basic Behavior
[ ] Test 3: Snapchat Stories/Spotlight
[ ] Test 4: Throttling Protection
[ ] Test 5: Smart Redirection Priority
[ ] Test 6: Migration System
[ ] Test 7: Dashboard Widget
[ ] Test 8: Settings UI Clarity
[ ] Test 9: Edge Case - Direct Reel Open
[ ] Test 10: User Feedback Verification

Overall Result: PASS / FAIL

Notes:
[Any observations, issues, or comments]
```

## Next Steps After Testing

1. If all tests pass → Ready for release
2. If some tests fail → Review logs, fix issues, retest
3. Document any edge cases discovered
4. Update documentation based on findings

