# 🎉 Release Automation Setup Complete

This document provides a summary of the automated release workflow that has been set up for the Focus Android app.

## 📦 What Was Created

### 1. GitHub Actions Workflow
**File**: `.github/workflows/auto-release.yml`

A comprehensive workflow that automatically:
- ✅ Bumps version with every commit to `app-main`
- ✅ Builds signed release APK (or debug fallback)
- ✅ Generates detailed release notes from commit history
- ✅ Creates GitHub releases with version tags
- ✅ Uploads APK as downloadable artifact
- ✅ Commits version changes back to repository

### 2. Version Bump Script
**File**: `scripts/bump_version.sh`

A manual script for bumping major/minor versions:
```bash
./scripts/bump_version.sh [major|minor|patch]
```

Features:
- Interactive confirmation
- Automatic backup creation
- Color-coded output
- Clear next-step instructions

### 3. Documentation
**Files**:
- `docs/RELEASE_WORKFLOW.md` - Complete workflow documentation
- `docs/SETUP_RELEASE_AUTOMATION.md` - Step-by-step setup guide
- `.github/RELEASE_CHECKLIST.md` - Quick reference checklist
- `.github/workflows/README.md` - Workflow directory documentation

## 🚀 How It Works

### Automatic Process (Every Commit)

```
Commit to app-main
    ↓
Workflow Triggered
    ↓
Version Bumped (patch++)
    ↓
APK Built & Signed
    ↓
Release Notes Generated
    ↓
GitHub Release Created
    ↓
Version Committed Back
```

### Version Numbering

**Current Version**: 2.0 (Build 2)

**Automatic Bumping**:
- `versionCode`: Increments by 1 (2 → 3 → 4...)
- `versionName`: Patch version increments (2.0.0 → 2.0.1 → 2.0.2...)

**Manual Bumping** (using script):
- **Patch**: 2.0.5 → 2.0.6
- **Minor**: 2.0.5 → 2.1.0
- **Major**: 2.0.5 → 3.0.0

## ⚙️ Configuration Required

### GitHub Secrets (Required for Signed Builds)

You need to configure these secrets in GitHub:

1. **KEYSTORE_BASE64**: Base64-encoded keystore file
2. **KEYSTORE_PASSWORD**: Keystore password
3. **KEY_ALIAS**: Key alias name
4. **KEY_PASSWORD**: Key password

### How to Set Up Secrets

1. Navigate to: `Settings → Secrets and variables → Actions`
2. Click "New repository secret"
3. Add each secret with its value

**To encode your keystore**:
```bash
# macOS/Linux
base64 -i keystore.jks > keystore_base64.txt

# Windows PowerShell
[Convert]::ToBase64String([IO.File]::ReadAllBytes("keystore.jks")) > keystore_base64.txt
```

### If You Don't Have a Keystore

Create one using:
```bash
keytool -genkey -v -keystore keystore.jks -keyalg RSA -keysize 2048 -validity 10000 -alias focus-key
```

**Note**: Without keystore secrets, the workflow will build a debug APK instead.

## 📋 Quick Start Guide

### First-Time Setup

1. **Configure Secrets** (see above)
2. **Verify Branch**: Ensure your default branch is `app-main`
3. **Test Workflow**: Make a commit and push
4. **Monitor**: Check Actions tab for workflow progress
5. **Verify**: Check Releases page for new release

### Daily Usage

**Just commit and push to `app-main`** - everything else is automatic!

```bash
git add .
git commit -m "feat: add new feature"
git push origin app-main
```

The workflow will:
1. Bump version automatically
2. Build the APK
3. Create a release
4. You'll get a notification when done

### Manual Version Bumps

When you need to bump minor or major versions:

```bash
# For minor version bump (2.0.x → 2.1.0)
./scripts/bump_version.sh minor
git push origin app-main

# For major version bump (2.x.x → 3.0.0)
./scripts/bump_version.sh major
git push origin app-main
```

## 📊 What You'll Get

### After Each Commit to app-main

1. **GitHub Release**
   - Version tag (e.g., `v2.0.1`)
   - Detailed release notes
   - APK file download
   - Installation instructions

2. **Updated Code**
   - `app/build.gradle` with new version
   - Automatic commit: "chore: bump version to X.Y.Z"

3. **Workflow Artifact**
   - APK stored for 90 days
   - Accessible from Actions tab

### Release Notes Include

- Build number and type (release/debug)
- APK size
- Commit history since last release
- Installation instructions
- Required permissions
- Important notes

## 🔍 Monitoring & Verification

### Check Workflow Status
1. Go to **Actions** tab
2. View "Auto Release and APK Build" runs
3. Click on a run to see detailed logs

### Download APKs
- **Public**: From Releases page
- **Artifacts**: From Actions → Workflow run

### View Releases
- Go to **Releases** page
- See all versions with download counts
- Access release notes and APKs

## 🎯 Best Practices

### Commit Messages
Use conventional commit format for better release notes:

```
feat: add new blocking feature
fix: resolve crash on startup
chore: update dependencies
docs: improve documentation
refactor: optimize service
perf: improve battery usage
```

### Before Pushing
- ✅ Test locally: `./gradlew assembleRelease`
- ✅ Verify no critical bugs
- ✅ Ensure tests pass
- ✅ Review commit messages

### Version Strategy
- **Patch** (automatic): Bug fixes, minor updates
- **Minor** (manual): New features, improvements
- **Major** (manual): Breaking changes, major redesigns

## 🐛 Troubleshooting

### Workflow Fails
1. Check Actions tab for error logs
2. Verify secrets are set correctly
3. Test build locally
4. Check workflow file syntax

### Build Succeeds but No Release
- Verify tag doesn't already exist
- Check GitHub token permissions
- Review release creation step logs

### Version Conflicts
```bash
git pull origin app-main
# Resolve conflicts
git push origin app-main
```

### Need Help?
- Read: `docs/RELEASE_WORKFLOW.md`
- Check: `.github/RELEASE_CHECKLIST.md`
- Review: `docs/SETUP_RELEASE_AUTOMATION.md`

## 📁 File Structure

```
focus/
├── .github/
│   ├── workflows/
│   │   ├── auto-release.yml          # Main workflow
│   │   └── README.md                 # Workflow docs
│   └── RELEASE_CHECKLIST.md          # Quick reference
├── scripts/
│   └── bump_version.sh               # Manual version bump
├── docs/
│   ├── RELEASE_WORKFLOW.md           # Complete documentation
│   └── SETUP_RELEASE_AUTOMATION.md   # Setup guide
├── app/
│   └── build.gradle                  # Version info here
└── RELEASE_AUTOMATION_SUMMARY.md     # This file
```

## 🔗 Important Links

- **Actions Dashboard**: `https://github.com/YOUR_USERNAME/focus/actions`
- **Releases Page**: `https://github.com/YOUR_USERNAME/focus/releases`
- **Settings/Secrets**: `https://github.com/YOUR_USERNAME/focus/settings/secrets/actions`

## ✅ Next Steps

1. **Configure GitHub Secrets** (if not done)
   - KEYSTORE_BASE64
   - KEYSTORE_PASSWORD
   - KEY_ALIAS
   - KEY_PASSWORD

2. **Test the Workflow**
   - Make a test commit
   - Push to app-main
   - Monitor Actions tab
   - Verify release creation

3. **Update README** (optional)
   - Add build status badge
   - Link to releases page
   - Mention automated releases

4. **Set Up Branch Protection** (recommended)
   - Require PR reviews
   - Require status checks
   - Prevent force pushes

## 🎊 Success Criteria

Your automation is working correctly when:

- ✅ Every push to `app-main` triggers the workflow
- ✅ Version is automatically bumped
- ✅ APK is built successfully
- ✅ GitHub release is created with APK
- ✅ Release notes are generated
- ✅ Version changes are committed back
- ✅ Git tag is created

## 📞 Support

For issues or questions:
1. Check the documentation files
2. Review workflow logs in Actions tab
3. Test build locally
4. Open a GitHub issue if needed

---

## 🎉 Congratulations!

Your Focus app now has a fully automated release pipeline! Every commit to `app-main` will automatically:

1. ✅ Bump the version
2. ✅ Build a signed APK
3. ✅ Create a GitHub release
4. ✅ Generate release notes
5. ✅ Tag the release

**No manual intervention required!**

Just focus on writing great code, and let the automation handle the releases. 🚀

---

**Created**: 2025-01-29  
**Version**: 1.0.0  
**Status**: Ready for use
