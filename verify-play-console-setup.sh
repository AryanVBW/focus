#!/bin/bash

# Play Console Setup Verification Script
# This script helps you verify that Play Console is properly configured

set -e

echo "=================================================="
echo "Play Console Setup Verification"
echo "=================================================="
echo ""

# Colors
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Function to ask yes/no questions
ask_yes_no() {
    local question=$1
    while true; do
        read -p "$question (y/n): " yn
        case $yn in
            [Yy]* ) return 0;;
            [Nn]* ) return 1;;
            * ) echo "Please answer yes (y) or no (n).";;
        esac
    done
}

# Function to display step
show_step() {
    echo ""
    echo -e "${BLUE}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
    echo -e "${BLUE}$1${NC}"
    echo -e "${BLUE}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
    echo ""
}

# Part 1: Play Console Account
show_step "Part 1: Play Console Account"

if ask_yes_no "Do you have a Google Play Developer account?"; then
    echo -e "${GREEN}✓${NC} Play Developer account exists"
else
    echo -e "${YELLOW}⚠${NC} You need to create a Play Developer account"
    echo "   1. Go to: https://play.google.com/console"
    echo "   2. Pay the \$25 one-time registration fee"
    echo "   3. Accept the Developer Distribution Agreement"
    echo ""
    read -p "Press Enter when you've completed this step..."
fi

# Part 2: App Created
show_step "Part 2: App in Play Console"

if ask_yes_no "Have you created the app 'Focus' in Play Console?"; then
    echo -e "${GREEN}✓${NC} App created in Play Console"
    
    if ask_yes_no "Is the package name 'com.aryanvbw.focus'?"; then
        echo -e "${GREEN}✓${NC} Package name is correct"
    else
        echo -e "${RED}✗${NC} Package name mismatch!"
        echo "   The package name MUST be: com.aryanvbw.focus"
        echo "   You may need to create a new app with the correct package name"
        exit 1
    fi
else
    echo -e "${YELLOW}⚠${NC} You need to create the app in Play Console"
    echo "   1. Go to: https://play.google.com/console"
    echo "   2. Click 'Create app'"
    echo "   3. Name: Focus - Productivity & App Blocker"
    echo "   4. Package name will be set when you upload first AAB"
    echo ""
    read -p "Press Enter when you've completed this step..."
fi

# Part 3: Policy Sections
show_step "Part 3: Policy Sections"

echo "Have you completed these policy sections?"
echo ""

POLICY_SECTIONS=(
    "App access"
    "Ads declaration"
    "Content rating"
    "Target audience"
    "News app declaration"
    "COVID-19 apps declaration"
    "Data safety"
)

COMPLETED_COUNT=0
for section in "${POLICY_SECTIONS[@]}"; do
    if ask_yes_no "  - $section"; then
        echo -e "    ${GREEN}✓${NC} $section completed"
        ((COMPLETED_COUNT++))
    else
        echo -e "    ${YELLOW}⚠${NC} $section needs completion"
        echo "       Go to: Play Console → Policy → $section"
    fi
done

echo ""
echo "Policy sections completed: $COMPLETED_COUNT/${#POLICY_SECTIONS[@]}"

if [ $COMPLETED_COUNT -lt ${#POLICY_SECTIONS[@]} ]; then
    echo -e "${YELLOW}⚠${NC} Complete remaining policy sections before proceeding"
    echo ""
    read -p "Press Enter when you've completed all policy sections..."
fi

# Part 4: First Manual Upload
show_step "Part 4: First Manual Upload"

if ask_yes_no "Have you uploaded at least one release manually to Internal Testing?"; then
    echo -e "${GREEN}✓${NC} First release uploaded"
    
    if ask_yes_no "Did the upload succeed without errors?"; then
        echo -e "${GREEN}✓${NC} Upload successful"
    else
        echo -e "${RED}✗${NC} Upload had errors"
        echo "   Common issues:"
        echo "   - Missing policy sections"
        echo "   - Invalid signing key"
        echo "   - Package name mismatch"
        echo ""
        read -p "Press Enter to continue..."
    fi
else
    echo -e "${YELLOW}⚠${NC} You need to upload the first release manually"
    echo ""
    echo "   The AAB file is ready at:"
    echo "   ${PWD}/app/build/outputs/bundle/release/app-release.aab"
    echo ""
    echo "   Steps:"
    echo "   1. Go to: Play Console → Testing → Internal testing"
    echo "   2. Click 'Create new release'"
    echo "   3. Upload the AAB file (8.5M)"
    echo "   4. Add release notes"
    echo "   5. Click 'Review release' → 'Start rollout'"
    echo ""
    read -p "Press Enter when you've completed the upload..."
fi

# Part 5: Service Account
show_step "Part 5: Service Account Setup"

if ask_yes_no "Have you created a service account in Google Cloud Console?"; then
    echo -e "${GREEN}✓${NC} Service account created"
    
    if ask_yes_no "Have you downloaded the service account JSON key file?"; then
        echo -e "${GREEN}✓${NC} JSON key downloaded"
        
        # Check if JSON file exists
        echo ""
        echo "Looking for service account JSON file..."
        JSON_FILES=$(find . -maxdepth 2 -name "*service*.json" -o -name "*play*.json" 2>/dev/null)
        
        if [ -n "$JSON_FILES" ]; then
            echo -e "${GREEN}✓${NC} Found JSON file(s):"
            echo "$JSON_FILES"
        else
            echo -e "${YELLOW}⚠${NC} No JSON file found in current directory"
            echo "   Make sure you have the service account JSON file"
        fi
    else
        echo -e "${YELLOW}⚠${NC} You need to download the JSON key"
        echo "   1. Go to: https://console.cloud.google.com/"
        echo "   2. APIs & Services → Credentials"
        echo "   3. Click on your service account"
        echo "   4. Keys tab → Add Key → Create new key → JSON"
        echo ""
        read -p "Press Enter when you've downloaded the key..."
    fi
else
    echo -e "${YELLOW}⚠${NC} You need to create a service account"
    echo "   See: PLAY_CONSOLE_SETUP_GUIDE.md - Part 3"
    echo ""
    read -p "Press Enter when you've created the service account..."
fi

# Part 6: Service Account Permissions
show_step "Part 6: Service Account Permissions in Play Console"

if ask_yes_no "Have you linked the service account to Play Console?"; then
    echo -e "${GREEN}✓${NC} Service account linked"
    
    if ask_yes_no "Does the service account have 'Release Manager' or 'Admin' permissions?"; then
        echo -e "${GREEN}✓${NC} Permissions granted"
    else
        echo -e "${RED}✗${NC} Insufficient permissions"
        echo "   1. Go to: Play Console → Settings → API access"
        echo "   2. Find your service account"
        echo "   3. Click 'Grant access'"
        echo "   4. Select 'Admin' or at minimum:"
        echo "      - Manage production releases"
        echo "      - Manage testing track releases"
        echo ""
        read -p "Press Enter when you've granted permissions..."
    fi
else
    echo -e "${YELLOW}⚠${NC} You need to link the service account"
    echo "   1. Go to: Play Console → Settings → API access"
    echo "   2. Link your Google Cloud project (if not already linked)"
    echo "   3. Find your service account in the list"
    echo "   4. Click 'Grant access'"
    echo "   5. Select appropriate permissions"
    echo ""
    read -p "Press Enter when you've linked the service account..."
fi

# Part 7: GitHub Secret
show_step "Part 7: GitHub Secret"

echo "Checking GitHub secret..."
SECRET_INFO=$(gh secret list 2>/dev/null | grep PLAY_STORE_SERVICE_ACCOUNT_JSON || echo "")

if [ -n "$SECRET_INFO" ]; then
    echo -e "${GREEN}✓${NC} PLAY_STORE_SERVICE_ACCOUNT_JSON secret exists"
    echo "   $SECRET_INFO"
    
    if ask_yes_no "Have you updated this secret with the NEW service account JSON?"; then
        echo -e "${GREEN}✓${NC} Secret updated with new JSON"
    else
        echo -e "${YELLOW}⚠${NC} You need to update the secret"
        echo ""
        echo "   Update the secret with:"
        echo "   gh secret set PLAY_STORE_SERVICE_ACCOUNT_JSON < your-service-account.json"
        echo ""
        read -p "Press Enter when you've updated the secret..."
    fi
else
    echo -e "${RED}✗${NC} Secret not found or cannot access"
    echo "   Make sure you're authenticated with gh CLI"
fi

# Part 8: Test Deployment
show_step "Part 8: Ready to Test"

echo "Setup verification complete! Here's your status:"
echo ""
echo "Next steps:"
echo ""
echo "1. Test the automated deployment:"
echo "   ${GREEN}gh workflow run play-store-deploy.yml --ref release/internal -f track=internal${NC}"
echo ""
echo "2. Monitor the deployment:"
echo "   ${GREEN}gh run watch${NC}"
echo ""
echo "3. Check Play Console:"
echo "   ${GREEN}open https://play.google.com/console${NC}"
echo ""

if ask_yes_no "Would you like to trigger a test deployment now?"; then
    echo ""
    echo "Triggering deployment to internal track..."
    gh workflow run play-store-deploy.yml --ref release/internal -f track=internal -f rollout_percentage=100
    
    if [ $? -eq 0 ]; then
        echo -e "${GREEN}✓${NC} Deployment triggered successfully!"
        echo ""
        echo "Monitor the deployment with:"
        echo "  gh run watch"
        echo ""
        echo "Or view in browser:"
        echo "  https://github.com/AryanVBW/focus/actions"
        
        if ask_yes_no "Would you like to watch the deployment now?"; then
            sleep 3
            gh run watch
        fi
    else
        echo -e "${RED}✗${NC} Failed to trigger deployment"
        echo "Check your GitHub CLI authentication"
    fi
else
    echo ""
    echo "You can trigger the deployment manually when ready."
fi

echo ""
echo "=================================================="
echo "Verification Complete!"
echo "=================================================="
echo ""
echo "For detailed setup instructions, see:"
echo "  - PLAY_CONSOLE_SETUP_GUIDE.md"
echo "  - DEPLOYMENT_QUICK_START.md"
echo ""

