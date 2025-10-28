#!/bin/bash

# Script to validate the release automation setup
# Usage: ./scripts/validate_release_setup.sh

set -e

# Colors
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

echo -e "${BLUE}╔════════════════════════════════════════════╗${NC}"
echo -e "${BLUE}║  Release Automation Validation Script     ║${NC}"
echo -e "${BLUE}╚════════════════════════════════════════════╝${NC}"
echo ""

ERRORS=0
WARNINGS=0

# Function to check file exists
check_file() {
    if [ -f "$1" ]; then
        echo -e "${GREEN}✓${NC} $1"
        return 0
    else
        echo -e "${RED}✗${NC} $1 ${RED}(missing)${NC}"
        ((ERRORS++))
        return 1
    fi
}

# Function to check directory exists
check_dir() {
    if [ -d "$1" ]; then
        echo -e "${GREEN}✓${NC} $1/"
        return 0
    else
        echo -e "${RED}✗${NC} $1/ ${RED}(missing)${NC}"
        ((ERRORS++))
        return 1
    fi
}

# Function to check if file is executable
check_executable() {
    if [ -x "$1" ]; then
        echo -e "${GREEN}✓${NC} $1 ${GREEN}(executable)${NC}"
        return 0
    else
        echo -e "${YELLOW}⚠${NC} $1 ${YELLOW}(not executable)${NC}"
        echo -e "   Run: ${BLUE}chmod +x $1${NC}"
        ((WARNINGS++))
        return 1
    fi
}

echo -e "${YELLOW}1. Checking Directory Structure${NC}"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
check_dir ".github"
check_dir ".github/workflows"
check_dir "scripts"
check_dir "docs"
echo ""

echo -e "${YELLOW}2. Checking Workflow Files${NC}"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
check_file ".github/workflows/auto-release.yml"
check_file ".github/workflows/README.md"
check_file ".github/RELEASE_CHECKLIST.md"
echo ""

echo -e "${YELLOW}3. Checking Scripts${NC}"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
check_file "scripts/bump_version.sh"
check_executable "scripts/bump_version.sh"
echo ""

echo -e "${YELLOW}4. Checking Documentation${NC}"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
check_file "docs/RELEASE_WORKFLOW.md"
check_file "docs/SETUP_RELEASE_AUTOMATION.md"
check_file "RELEASE_AUTOMATION_SUMMARY.md"
echo ""

echo -e "${YELLOW}5. Checking Build Configuration${NC}"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
check_file "app/build.gradle"

if [ -f "app/build.gradle" ]; then
    VERSION_CODE=$(grep "versionCode" app/build.gradle | awk '{print $2}')
    VERSION_NAME=$(grep "versionName" app/build.gradle | awk '{print $2}' | tr -d '"')
    
    if [ -n "$VERSION_CODE" ] && [ -n "$VERSION_NAME" ]; then
        echo -e "${GREEN}✓${NC} Current version: ${BLUE}$VERSION_NAME${NC} (Build: ${BLUE}$VERSION_CODE${NC})"
    else
        echo -e "${RED}✗${NC} Could not read version from app/build.gradle"
        ((ERRORS++))
    fi
fi
echo ""

echo -e "${YELLOW}6. Checking Git Configuration${NC}"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"

# Check if in git repository
if git rev-parse --git-dir > /dev/null 2>&1; then
    echo -e "${GREEN}✓${NC} Git repository detected"
    
    # Check current branch
    CURRENT_BRANCH=$(git branch --show-current)
    echo -e "  Current branch: ${BLUE}$CURRENT_BRANCH${NC}"
    
    if [ "$CURRENT_BRANCH" = "app-main" ]; then
        echo -e "${GREEN}✓${NC} On app-main branch"
    else
        echo -e "${YELLOW}⚠${NC} Not on app-main branch (workflow triggers on app-main)"
        ((WARNINGS++))
    fi
    
    # Check for remote
    if git remote -v | grep -q "origin"; then
        echo -e "${GREEN}✓${NC} Remote 'origin' configured"
    else
        echo -e "${RED}✗${NC} No remote 'origin' found"
        ((ERRORS++))
    fi
else
    echo -e "${RED}✗${NC} Not a git repository"
    ((ERRORS++))
fi
echo ""

echo -e "${YELLOW}7. Checking GitHub Secrets (Manual Check Required)${NC}"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo -e "${BLUE}ℹ${NC} You need to manually verify these secrets are set in GitHub:"
echo "   1. KEYSTORE_BASE64"
echo "   2. KEYSTORE_PASSWORD"
echo "   3. KEY_ALIAS"
echo "   4. KEY_PASSWORD"
echo ""
echo "   Go to: Settings → Secrets and variables → Actions"
echo ""

echo -e "${YELLOW}8. Workflow Syntax Check${NC}"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
if command -v yamllint &> /dev/null; then
    if yamllint .github/workflows/auto-release.yml 2>&1 | grep -q "error"; then
        echo -e "${RED}✗${NC} YAML syntax errors found"
        ((ERRORS++))
    else
        echo -e "${GREEN}✓${NC} YAML syntax valid"
    fi
else
    echo -e "${YELLOW}⚠${NC} yamllint not installed (skipping syntax check)"
    echo "   Install: ${BLUE}pip install yamllint${NC}"
    ((WARNINGS++))
fi
echo ""

# Summary
echo -e "${BLUE}╔════════════════════════════════════════════╗${NC}"
echo -e "${BLUE}║              Validation Summary            ║${NC}"
echo -e "${BLUE}╚════════════════════════════════════════════╝${NC}"
echo ""

if [ $ERRORS -eq 0 ] && [ $WARNINGS -eq 0 ]; then
    echo -e "${GREEN}✅ All checks passed!${NC}"
    echo ""
    echo -e "${GREEN}Your release automation is ready to use!${NC}"
    echo ""
    echo "Next steps:"
    echo "1. Configure GitHub secrets (see section 7 above)"
    echo "2. Make a test commit and push to app-main"
    echo "3. Monitor the workflow in the Actions tab"
    echo "4. Verify the release is created successfully"
    exit 0
elif [ $ERRORS -eq 0 ]; then
    echo -e "${YELLOW}⚠ Validation completed with $WARNINGS warning(s)${NC}"
    echo ""
    echo "Review the warnings above and fix if necessary."
    exit 0
else
    echo -e "${RED}❌ Validation failed with $ERRORS error(s) and $WARNINGS warning(s)${NC}"
    echo ""
    echo "Please fix the errors above before proceeding."
    exit 1
fi
