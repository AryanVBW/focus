# Focus App Documentation Index

This directory contains all documentation for the Focus App project, organized by category for easy navigation.

## 📁 Directory Structure

### 📋 Main Documentation
- [`README.md`](../README.md) - Main project overview and setup instructions
- [`RELEASE.md`](RELEASE.md) - Release notes and version history
- [`PROJECT_RESTRUCTURE_SUMMARY.md`](PROJECT_RESTRUCTURE_SUMMARY.md) - Complete restructuring documentation

### 🔧 Implementation Guides
- [`BLOCKING_ACTIONS_IMPLEMENTATION.md`](implementation/BLOCKING_ACTIONS_IMPLEMENTATION.md) - Implementation details for content blocking actions
- [`DARK_MODE_IMPLEMENTATION_COMPLETE.md`](implementation/DARK_MODE_IMPLEMENTATION_COMPLETE.md) - Complete dark mode implementation guide

### ✨ Feature Documentation
- [`DASHBOARD_ENHANCEMENT_SUMMARY.md`](features/DASHBOARD_ENHANCEMENT_SUMMARY.md) - Dashboard improvements and enhancements
- [`DUAL_MODE_README.md`](features/DUAL_MODE_README.md) - Dual mode functionality documentation
- [`ENHANCED_UI_SUMMARY.md`](features/ENHANCED_UI_SUMMARY.md) - UI enhancement summary
- [`LIQUID_GLASS_THEME_SUMMARY.md`](features/LIQUID_GLASS_THEME_SUMMARY.md) - Liquid glass theme implementation
- [`MODE_ENHANCEMENT_SUMMARY.md`](features/MODE_ENHANCEMENT_SUMMARY.md) - Mode enhancement features
- [`UI_ENHANCEMENT_SUMMARY.md`](features/UI_ENHANCEMENT_SUMMARY.md) - User interface improvements

### 🔍 Troubleshooting & Testing
- [`ACCESSIBILITY_PERMISSION_FIXES.md`](troubleshooting/ACCESSIBILITY_PERMISSION_FIXES.md) - Accessibility permission troubleshooting
- [`ACCESSIBILITY_TROUBLESHOOTING.md`](troubleshooting/ACCESSIBILITY_TROUBLESHOOTING.md) - General accessibility troubleshooting guide
- [`TEST_ADULT_CONTENT_BLOCKING.md`](troubleshooting/TEST_ADULT_CONTENT_BLOCKING.md) - Adult content blocking testing procedures

## 🛠️ Scripts

Utility scripts are located in the [`scripts/`](../scripts/) directory:
- [`fix_packages.sh`](../scripts/fix_packages.sh) - Package fixing utility
- [`troubleshoot_accessibility.sh`](../scripts/troubleshoot_accessibility.sh) - Accessibility troubleshooting script

## 📱 App Structure

The main application code is organized in [`app/src/main/java/com/focus/app/`](../app/src/main/java/com/focus/app/) with the following key packages:

- **`detection/`** - Content detection and video analysis
- **`blocking/`** - Scroll blocking and content filtering
- **`model/`** - Data models and structures
- **`ui/`** - User interface components
- **`service/`** - Background services
- **`database/`** - Data persistence layer

## 🔄 Recent Enhancements

The project has been enhanced with:
- Advanced video content detection
- Intelligent scroll blocking strategies
- Enhanced directory structure
- Comprehensive documentation organization

## 📝 Contributing

When adding new documentation:
1. Place implementation guides in `implementation/`
2. Place feature documentation in `features/`
3. Place troubleshooting guides in `troubleshooting/`
4. Update this index file with new additions

---

*Last updated: $(date)*