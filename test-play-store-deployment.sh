#!/bin/bash

# Google Play Store Deployment Testing Script
# This script tests the complete publishing workflow for Focus Android app

set -e

echo "=================================================="
echo "Google Play Store Deployment Testing"
echo "=================================================="
echo ""

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Test 1: Verify build configuration
echo "Test 1: Verifying build configuration..."
if [ -f "app/build.gradle" ]; then
    echo -e "${GREEN}✓${NC} app/build.gradle exists"
    VERSION_CODE=$(grep "versionCode" app/build.gradle | awk '{print $2}')
    VERSION_NAME=$(grep "versionName" app/build.gradle | awk '{print $2}' | tr -d '"')
    echo "  Version Code: $VERSION_CODE"
    echo "  Version Name: $VERSION_NAME"
else
    echo -e "${RED}✗${NC} app/build.gradle not found"
    exit 1
fi

# Test 2: Verify keystore exists
echo ""
echo "Test 2: Verifying keystore..."
if [ -f "keystore/focus-release-key.jks" ]; then
    echo -e "${GREEN}✓${NC} Keystore file exists"
    KEYSTORE_SIZE=$(du -h keystore/focus-release-key.jks | cut -f1)
    echo "  Size: $KEYSTORE_SIZE"
else
    echo -e "${RED}✗${NC} Keystore file not found"
    exit 1
fi

# Test 3: Verify keystore properties
echo ""
echo "Test 3: Verifying keystore properties..."
if [ -f "keystore.properties" ]; then
    echo -e "${GREEN}✓${NC} keystore.properties exists"
    if grep -q "keyAlias" keystore.properties; then
        KEY_ALIAS=$(grep "keyAlias" keystore.properties | cut -d'=' -f2)
        echo "  Key Alias: $KEY_ALIAS"
    fi
else
    echo -e "${RED}✗${NC} keystore.properties not found"
    exit 1
fi

# Test 4: Verify GitHub secrets
echo ""
echo "Test 4: Verifying GitHub secrets..."
SECRETS=$(gh secret list 2>/dev/null || echo "")
if [ -n "$SECRETS" ]; then
    echo -e "${GREEN}✓${NC} GitHub CLI authenticated"
    
    REQUIRED_SECRETS=("KEYSTORE_BASE64" "KEYSTORE_PASSWORD" "KEY_ALIAS" "KEY_PASSWORD" "PLAY_STORE_SERVICE_ACCOUNT_JSON")
    for secret in "${REQUIRED_SECRETS[@]}"; do
        if echo "$SECRETS" | grep -q "$secret"; then
            echo -e "  ${GREEN}✓${NC} $secret is set"
        else
            echo -e "  ${RED}✗${NC} $secret is missing"
        fi
    done
else
    echo -e "${YELLOW}⚠${NC} Cannot verify secrets (gh CLI not authenticated or no access)"
fi

# Test 5: Clean build
echo ""
echo "Test 5: Running clean build..."
./gradlew clean > /dev/null 2>&1
if [ $? -eq 0 ]; then
    echo -e "${GREEN}✓${NC} Clean build successful"
else
    echo -e "${RED}✗${NC} Clean build failed"
    exit 1
fi

# Test 6: Build release AAB
echo ""
echo "Test 6: Building release AAB..."
./gradlew bundleRelease --stacktrace > /tmp/aab_build.log 2>&1
if [ $? -eq 0 ]; then
    echo -e "${GREEN}✓${NC} AAB build successful"
    if [ -f "app/build/outputs/bundle/release/app-release.aab" ]; then
        AAB_SIZE=$(du -h app/build/outputs/bundle/release/app-release.aab | cut -f1)
        echo "  AAB Size: $AAB_SIZE"
        echo "  Location: app/build/outputs/bundle/release/app-release.aab"
    fi
else
    echo -e "${RED}✗${NC} AAB build failed"
    echo "Check /tmp/aab_build.log for details"
    exit 1
fi

# Test 7: Build release APK
echo ""
echo "Test 7: Building release APK..."
./gradlew assembleRelease --stacktrace > /tmp/apk_build.log 2>&1
if [ $? -eq 0 ]; then
    echo -e "${GREEN}✓${NC} APK build successful"
    if [ -f "app/build/outputs/apk/release/app-release.apk" ]; then
        APK_SIZE=$(du -h app/build/outputs/apk/release/app-release.apk | cut -f1)
        echo "  APK Size: $APK_SIZE"
        echo "  Location: app/build/outputs/apk/release/app-release.apk"
    fi
else
    echo -e "${RED}✗${NC} APK build failed"
    echo "Check /tmp/apk_build.log for details"
    exit 1
fi

# Test 8: Verify release notes
echo ""
echo "Test 8: Verifying release notes..."
if [ -f ".github/whatsnew/whatsnew-en-US" ]; then
    echo -e "${GREEN}✓${NC} Release notes exist"
    NOTES_LENGTH=$(wc -c < .github/whatsnew/whatsnew-en-US)
    echo "  Length: $NOTES_LENGTH characters"
    if [ $NOTES_LENGTH -gt 500 ]; then
        echo -e "  ${YELLOW}⚠${NC} Release notes exceed 500 character limit for Play Store"
    fi
else
    echo -e "${YELLOW}⚠${NC} Release notes not found"
fi

# Test 9: Verify workflow file
echo ""
echo "Test 9: Verifying workflow file..."
if [ -f ".github/workflows/play-store-deploy.yml" ]; then
    echo -e "${GREEN}✓${NC} Workflow file exists"
    
    # Check for common YAML issues
    if grep -q "^  push:" .github/workflows/play-store-deploy.yml; then
        echo -e "  ${GREEN}✓${NC} Push trigger configured"
    fi
    
    if grep -q "workflow_dispatch:" .github/workflows/play-store-deploy.yml; then
        echo -e "  ${GREEN}✓${NC} Manual dispatch configured"
    fi
else
    echo -e "${RED}✗${NC} Workflow file not found"
    exit 1
fi

# Test 10: Check recent workflow runs
echo ""
echo "Test 10: Checking recent workflow runs..."
RECENT_RUNS=$(gh run list --workflow=play-store-deploy.yml --limit 3 --json status,conclusion,event 2>/dev/null || echo "")
if [ -n "$RECENT_RUNS" ]; then
    echo -e "${GREEN}✓${NC} Can access workflow runs"
    echo "$RECENT_RUNS" | jq -r '.[] | "  Event: \(.event), Status: \(.status), Conclusion: \(.conclusion // "N/A")"'
else
    echo -e "${YELLOW}⚠${NC} Cannot access workflow runs"
fi

# Summary
echo ""
echo "=================================================="
echo "Test Summary"
echo "=================================================="
echo ""
echo "Build Configuration: ✓ PASSED"
echo "Local Builds: ✓ PASSED"
echo "  - AAB: $AAB_SIZE"
echo "  - APK: $APK_SIZE"
echo "Workflow Configuration: ✓ PASSED"
echo ""
echo "Next Steps:"
echo "1. Trigger manual deployment: gh workflow run play-store-deploy.yml --ref release/internal -f track=internal"
echo "2. Monitor deployment: gh run list --workflow=play-store-deploy.yml"
echo "3. View run details: gh run view <RUN_ID>"
echo "4. Check Play Console: https://play.google.com/console"
echo ""
echo "For beta track testing:"
echo "  git checkout release/beta"
echo "  git merge release/internal"
echo "  git push origin release/beta"
echo ""

