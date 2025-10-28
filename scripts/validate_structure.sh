#!/bin/bash

# Focus App - Project Structure Validation Script
# This script validates that the project maintains its organized structure

set -e

echo "🔍 Validating Focus App project structure..."

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Get script directory
SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
PROJECT_ROOT="$(dirname "$SCRIPT_DIR")"

echo "📁 Project root: $PROJECT_ROOT"

# Function to check if directory exists
check_directory() {
    local dir="$1"
    local description="$2"
    
    if [ -d "$PROJECT_ROOT/$dir" ]; then
        echo -e "${GREEN}✓${NC} $description: $dir"
        return 0
    else
        echo -e "${RED}✗${NC} Missing $description: $dir"
        return 1
    fi
}

# Function to check if file exists
check_file() {
    local file="$1"
    local description="$2"
    
    if [ -f "$PROJECT_ROOT/$file" ]; then
        echo -e "${GREEN}✓${NC} $description: $file"
        return 0
    else
        echo -e "${RED}✗${NC} Missing $description: $file"
        return 1
    fi
}

# Function to check for misplaced files
check_no_files_in_root() {
    local pattern="$1"
    local description="$2"
    
    local files=$(find "$PROJECT_ROOT" -maxdepth 1 -name "$pattern" -type f)
    
    if [ -z "$files" ]; then
        echo -e "${GREEN}✓${NC} No $description in root directory"
        return 0
    else
        echo -e "${YELLOW}⚠${NC} Found $description in root directory:"
        echo "$files"
        return 1
    fi
}

echo ""
echo "📋 Checking core project structure..."

# Check main directories
ERRORS=0

check_directory "app" "Main application directory" || ((ERRORS++))
check_directory "docs" "Documentation directory" || ((ERRORS++))
check_directory "scripts" "Scripts directory" || ((ERRORS++))
check_directory "backups" "Backups directory" || ((ERRORS++))
check_directory "keystore" "Keystore directory" || ((ERRORS++))

echo ""
echo "📖 Checking documentation structure..."

check_directory "docs/implementation" "Implementation guides" || ((ERRORS++))
check_directory "docs/features" "Feature documentation" || ((ERRORS++))
check_directory "docs/troubleshooting" "Troubleshooting guides" || ((ERRORS++))
check_file "docs/INDEX.md" "Documentation index" || ((ERRORS++))
check_file "docs/RELEASE.md" "Release notes" || ((ERRORS++))

echo ""
echo "🏗️ Checking application structure..."

check_directory "app/src/main/java/com/focus/app" "Main app package" || ((ERRORS++))
check_directory "app/src/main/java/com/focus/app/detection" "Detection package" || ((ERRORS++))
check_directory "app/src/main/java/com/focus/app/blocking" "Blocking package" || ((ERRORS++))

echo ""
echo "📝 Checking core files..."

check_file "README.md" "Main README" || ((ERRORS++))
check_file "CONTRIBUTING.md" "Contributing guidelines" || ((ERRORS++))
check_file ".gitignore" "Git ignore file" || ((ERRORS++))
check_file "build.gradle" "Main build file" || ((ERRORS++))
check_file "settings.gradle" "Gradle settings" || ((ERRORS++))

echo ""
echo "🧹 Checking for misplaced files..."

# Check for markdown files that should be in docs/
check_no_files_in_root "*_IMPLEMENTATION*.md" "implementation documentation files"
check_no_files_in_root "*_SUMMARY*.md" "summary documentation files"
check_no_files_in_root "*_TROUBLESHOOTING*.md" "troubleshooting files"
check_no_files_in_root "TEST_*.md" "test documentation files"

# Check for shell scripts that should be in scripts/
if [ "$(find "$PROJECT_ROOT" -maxdepth 1 -name "*.sh" -type f | wc -l)" -gt 0 ]; then
    echo -e "${YELLOW}⚠${NC} Found shell scripts in root directory (should be in scripts/):"
    find "$PROJECT_ROOT" -maxdepth 1 -name "*.sh" -type f
else
    echo -e "${GREEN}✓${NC} No shell scripts in root directory"
fi

echo ""
echo "📊 Validation Summary"
echo "==================="

if [ $ERRORS -eq 0 ]; then
    echo -e "${GREEN}🎉 Project structure validation passed!${NC}"
    echo "All directories and files are properly organized."
    exit 0
else
    echo -e "${RED}❌ Project structure validation failed!${NC}"
    echo "Found $ERRORS issues that need to be addressed."
    echo ""
    echo "💡 Suggestions:"
    echo "  - Move misplaced documentation files to docs/ subdirectories"
    echo "  - Move shell scripts to scripts/ directory"
    echo "  - Create missing directories as needed"
    echo "  - Update documentation index after moving files"
    exit 1
fi