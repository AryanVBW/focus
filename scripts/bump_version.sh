#!/bin/bash

# Script to manually bump version in build.gradle
# Usage: ./scripts/bump_version.sh [major|minor|patch]

set -e

BUILD_GRADLE="app/build.gradle"
BUMP_TYPE=${1:-patch}

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo -e "${YELLOW}🔄 Version Bump Script${NC}"
echo "================================"

# Check if build.gradle exists
if [ ! -f "$BUILD_GRADLE" ]; then
    echo -e "${RED}❌ Error: $BUILD_GRADLE not found!${NC}"
    exit 1
fi

# Get current version
CURRENT_VERSION_CODE=$(grep "versionCode" "$BUILD_GRADLE" | awk '{print $2}')
CURRENT_VERSION_NAME=$(grep "versionName" "$BUILD_GRADLE" | awk '{print $2}' | tr -d '"')

echo -e "Current Version: ${GREEN}$CURRENT_VERSION_NAME${NC} (Build: $CURRENT_VERSION_CODE)"

# Calculate new version code
NEW_VERSION_CODE=$((CURRENT_VERSION_CODE + 1))

# Parse semantic version (major.minor.patch)
IFS='.' read -ra VERSION_PARTS <<< "$CURRENT_VERSION_NAME"
MAJOR=${VERSION_PARTS[0]:-1}
MINOR=${VERSION_PARTS[1]:-0}
PATCH=${VERSION_PARTS[2]:-0}

# Bump version based on type
case $BUMP_TYPE in
    major)
        MAJOR=$((MAJOR + 1))
        MINOR=0
        PATCH=0
        ;;
    minor)
        MINOR=$((MINOR + 1))
        PATCH=0
        ;;
    patch)
        PATCH=$((PATCH + 1))
        ;;
    *)
        echo -e "${RED}❌ Error: Invalid bump type '$BUMP_TYPE'. Use: major, minor, or patch${NC}"
        exit 1
        ;;
esac

NEW_VERSION_NAME="$MAJOR.$MINOR.$PATCH"

echo -e "New Version: ${GREEN}$NEW_VERSION_NAME${NC} (Build: $NEW_VERSION_CODE)"
echo ""

# Ask for confirmation
read -p "Do you want to proceed with this version bump? (y/n) " -n 1 -r
echo ""

if [[ ! $REPLY =~ ^[Yy]$ ]]; then
    echo -e "${YELLOW}⚠️  Version bump cancelled${NC}"
    exit 0
fi

# Create backup
BACKUP_FILE="${BUILD_GRADLE}.backup"
cp "$BUILD_GRADLE" "$BACKUP_FILE"
echo -e "${GREEN}✓${NC} Created backup: $BACKUP_FILE"

# Update version code
sed -i.tmp "s/versionCode $CURRENT_VERSION_CODE/versionCode $NEW_VERSION_CODE/" "$BUILD_GRADLE"
echo -e "${GREEN}✓${NC} Updated versionCode: $CURRENT_VERSION_CODE → $NEW_VERSION_CODE"

# Update version name
sed -i.tmp "s/versionName \"$CURRENT_VERSION_NAME\"/versionName \"$NEW_VERSION_NAME\"/" "$BUILD_GRADLE"
echo -e "${GREEN}✓${NC} Updated versionName: $CURRENT_VERSION_NAME → $NEW_VERSION_NAME"

# Remove temporary files
rm -f "${BUILD_GRADLE}.tmp"

echo ""
echo -e "${GREEN}✅ Version bump completed successfully!${NC}"
echo ""
echo "Next steps:"
echo "1. Review the changes in $BUILD_GRADLE"
echo "2. Test the build: ./gradlew assembleRelease"
echo "3. Commit the changes: git add $BUILD_GRADLE && git commit -m 'chore: bump version to $NEW_VERSION_NAME'"
echo "4. Push to trigger automatic release: git push origin app-main"
echo ""
echo "To restore the backup: mv $BACKUP_FILE $BUILD_GRADLE"
