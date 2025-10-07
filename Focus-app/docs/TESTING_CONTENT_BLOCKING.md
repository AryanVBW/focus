# Testing Content Blocking Behavior

## Overview

This document provides step-by-step instructions for testing that the Focus app correctly blocks only distracting content (Reels/Shorts) and NOT the entire app.

## Prerequisites

1. Focus app installed and accessibility service enabled
2. Test apps installed: Instagram, YouTube, Snapchat, Facebook
3. ADB connected (for log monitoring)
4. Normal Mode enabled (NOT Focus Mode)

## Test Cases

### Test 1: YouTube - Block Shorts Only

**Objective:** Verify that YouTube Shorts are blocked but regular videos work

**Steps:**
1. Open YouTube app
2. Navigate to Home tab
3. Verify: Home feed loads normally ✅
4. Play a regular long-form video
5. Verify: Video plays without interruption ✅
6. Go back to Home
7. Navigate to Shorts tab or tap a Shorts video
8. **Expected Result:** 
   - Shorts player starts to open
   - Immediately returns to previous screen (Home or previous tab)
   - Toast message: "Focus: returned to previous screen"
   - YouTube app remains open ✅
9. Navigate to Subscriptions tab
10. Verify: Subscriptions load normally ✅
11. Navigate to Library tab
12. Verify: Library loads normally ✅

**Success Criteria:**
- ✅ Shorts are blocked (back navigation triggered)
- ✅ Regular videos play normally
- ✅ All other YouTube features work
- ✅ App does NOT close or show blocked page

**ADB Log Commands:**
```bash
# Monitor Focus service logs
adb logcat -s FocusAccessService:* | grep -i "youtube\|shorts"

# Expected logs when Shorts detected:
# "YouTube Shorts detected. Blocking."
# "Executed close player action (back navigation)"
```

### Test 2: Instagram - Block Reels Only

**Objective:** Verify that Instagram Reels are blocked but feed works

**Steps:**
1. Open Instagram app
2. View Home feed
3. Verify: Feed loads and scrolls normally ✅
4. Tap on a regular post
5. Verify: Post opens and can be viewed ✅
6. Go back to feed
7. Tap on a Story
8. Verify: Story plays (Stories are NOT blocked in Normal Mode) ✅
9. Navigate to Reels tab or tap a Reel
10. **Expected Result:**
    - Reels player starts to open
    - Immediately returns to previous screen (Feed or Profile)
    - Toast message: "Focus: returned to previous screen"
    - Instagram app remains open ✅
11. Navigate to Profile tab
12. Verify: Profile loads normally ✅
13. Navigate to Search/Explore
14. Verify: Search works normally ✅
15. Open Direct Messages
16. Verify: Messages work normally ✅

**Success Criteria:**
- ✅ Reels are blocked (back navigation triggered)
- ✅ Regular feed posts work
- ✅ Stories work (in Normal Mode)
- ✅ All other Instagram features work
- ✅ App does NOT close or show blocked page

**ADB Log Commands:**
```bash
# Monitor Focus service logs
adb logcat -s FocusAccessService:* | grep -i "instagram\|reels"

# Expected logs when Reels detected:
# "Instagram Reels detected. Blocking."
# "Executed close player action (back navigation)"
```

### Test 3: Snapchat - Block Spotlight Only

**Objective:** Verify that Spotlight is blocked but Chat/Camera work

**Steps:**
1. Open Snapchat app
2. Verify: Opens to Camera screen ✅
3. Swipe right to Chat
4. Verify: Chat loads normally ✅
5. Send a message
6. Verify: Messaging works normally ✅
7. Swipe left to Stories/Spotlight
8. **Expected Result:**
    - Spotlight/Stories start to load
    - Immediately returns to Camera or Chat
    - Toast message: "Focus: returned to previous screen"
    - Snapchat app remains open ✅
9. Take a photo/video
10. Verify: Camera works normally ✅
11. View Snap Map
12. Verify: Map works normally ✅

**Success Criteria:**
- ✅ Spotlight/Stories are blocked
- ✅ Camera works normally
- ✅ Chat/messaging works normally
- ✅ All other Snapchat features work
- ✅ App does NOT close or show blocked page

### Test 4: Facebook - Block Reels Only

**Objective:** Verify that Facebook Reels are blocked but feed works

**Steps:**
1. Open Facebook app
2. View News Feed
3. Verify: Feed loads and scrolls normally ✅
4. Tap on a regular post
5. Verify: Post opens and can be viewed ✅
6. Like and comment on a post
7. Verify: Interactions work normally ✅
8. Navigate to Reels tab or tap a Reel
9. **Expected Result:**
    - Reels player starts to open
    - Immediately returns to previous screen (Feed)
    - Toast message: "Focus: returned to previous screen"
    - Facebook app remains open ✅
10. Navigate to Groups
11. Verify: Groups work normally ✅
12. Navigate to Marketplace
13. Verify: Marketplace works normally ✅
14. Open Messenger
15. Verify: Messaging works normally ✅

**Success Criteria:**
- ✅ Reels are blocked
- ✅ Regular feed posts work
- ✅ All other Facebook features work
- ✅ App does NOT close or show blocked page

### Test 5: Focus Mode vs Normal Mode

**Objective:** Verify the difference between modes

**Normal Mode Test:**
1. Ensure Normal Mode is enabled (Focus Mode OFF)
2. Open Instagram
3. Try to open Reels
4. **Expected:** Reels blocked, but Instagram stays open ✅
5. Navigate to Feed
6. **Expected:** Feed works normally ✅

**Focus Mode Test:**
1. Enable Focus Mode in Focus app
2. Add Instagram to "Blocked Apps" list
3. Open Instagram
4. **Expected:** BlockedPageActivity shows (full app block) ✅
5. Tap "Return to Home"
6. **Expected:** Returns to home screen, Instagram closes ✅

**Success Criteria:**
- ✅ Normal Mode: Only content blocked, app stays open
- ✅ Focus Mode: Entire app blocked if in blocked list
- ✅ Clear distinction between modes

## Automated Testing

### ADB Test Script

```bash
#!/bin/bash

echo "=== Focus App Content Blocking Test ==="
echo ""

# Function to test an app
test_app() {
    local package=$1
    local app_name=$2
    
    echo "Testing $app_name..."
    
    # Launch app
    adb shell monkey -p $package -c android.intent.category.LAUNCHER 1
    sleep 3
    
    # Monitor logs for 10 seconds
    timeout 10 adb logcat -s FocusAccessService:* | grep -i "$app_name" &
    
    echo "  - App launched, monitoring for blocking events..."
    sleep 10
    
    # Return to home
    adb shell input keyevent KEYCODE_HOME
    sleep 1
    
    echo "  - Test complete"
    echo ""
}

# Test each app
test_app "com.instagram.android" "Instagram"
test_app "com.google.android.youtube" "YouTube"
test_app "com.snapchat.android" "Snapchat"
test_app "com.facebook.katana" "Facebook"

echo "=== All tests complete ==="
echo "Review logs above for blocking events"
```

### Expected Log Patterns

**When content is blocked:**
```
FocusAccessService: Instagram Reels detected. Blocking.
FocusAccessService: Executed close player action (back navigation)
FocusAccessService: Focus: returned to previous screen
```

**When content is NOT blocked (normal usage):**
```
FocusAccessService: Processing accessibility tree for com.instagram.android
FocusAccessService: No distracting content detected
```

## Verification Checklist

### For Each App

- [ ] App opens normally
- [ ] Non-distracting content works (feed, regular videos, etc.)
- [ ] Distracting content is detected (Reels, Shorts, etc.)
- [ ] Back navigation is triggered (not full app block)
- [ ] User returns to previous screen within the app
- [ ] App remains open and functional
- [ ] Toast notification shows
- [ ] No BlockedPageActivity appears (unless Focus Mode)
- [ ] Can continue using other app features

### Overall System

- [ ] Accessibility service is running
- [ ] Detection is accurate (no false positives)
- [ ] Blocking is consistent
- [ ] No performance issues
- [ ] Battery usage is reasonable
- [ ] Logs show correct behavior

## Troubleshooting

### Issue: App Closes Completely

**Diagnosis:**
```bash
adb logcat -s FocusAccessService:* | grep -i "BlockedPageActivity"
```

If you see `BlockedPageActivity`, the app is in the blocked apps list.

**Fix:**
1. Open Focus app
2. Go to Settings → Blocked Apps
3. Remove the app from the list
4. Test again

### Issue: Content Not Being Blocked

**Diagnosis:**
```bash
adb logcat -s FocusAccessService:* | grep -i "detected"
```

If you don't see detection logs, the service may not be running.

**Fix:**
1. Check accessibility service is enabled
2. Restart Focus app
3. Restart accessibility service
4. Test again

### Issue: False Positives

**Diagnosis:**
```bash
adb logcat -s FocusAccessService:* -v time
```

Check if blocking happens on non-distracting content.

**Fix:**
1. Review detection patterns
2. Adjust confidence thresholds
3. Update detection logic

## Performance Testing

### Battery Impact

```bash
# Monitor battery usage
adb shell dumpsys batterystats | grep -i focus

# Expected: < 5% battery usage per day
```

### CPU Usage

```bash
# Monitor CPU usage
adb shell top | grep -i focus

# Expected: < 2% CPU usage when idle
```

### Memory Usage

```bash
# Monitor memory usage
adb shell dumpsys meminfo com.aryanvbw.focus

# Expected: < 50MB RAM usage
```

## Regression Testing

After any code changes, run all test cases to ensure:
1. Content blocking still works
2. No new false positives
3. Performance hasn't degraded
4. User experience remains smooth

## Test Results Template

```
Date: ___________
Tester: ___________
Focus App Version: ___________

YouTube:
- Shorts blocked: [ ] Pass [ ] Fail
- Regular videos work: [ ] Pass [ ] Fail
- App stays open: [ ] Pass [ ] Fail

Instagram:
- Reels blocked: [ ] Pass [ ] Fail
- Feed works: [ ] Pass [ ] Fail
- App stays open: [ ] Pass [ ] Fail

Snapchat:
- Spotlight blocked: [ ] Pass [ ] Fail
- Chat works: [ ] Pass [ ] Fail
- App stays open: [ ] Pass [ ] Fail

Facebook:
- Reels blocked: [ ] Pass [ ] Fail
- Feed works: [ ] Pass [ ] Fail
- App stays open: [ ] Pass [ ] Fail

Overall: [ ] Pass [ ] Fail

Notes:
_________________________________
_________________________________
```

---

*Last Updated: 2025-10-07*

