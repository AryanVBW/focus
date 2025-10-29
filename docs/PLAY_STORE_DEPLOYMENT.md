# 🚀 Google Play Store Deployment Guide

This guide explains how to automatically deploy the Focus Android app to Google Play Console using GitHub Actions.

## 📋 Table of Contents

- [Overview](#overview)
- [Prerequisites](#prerequisites)
- [Initial Setup](#initial-setup)
- [Deployment Methods](#deployment-methods)
- [Release Tracks](#release-tracks)
- [Secrets Configuration](#secrets-configuration)
- [Troubleshooting](#troubleshooting)
- [Best Practices](#best-practices)

## 🎯 Overview

The automated Play Store deployment workflow provides:

- ✅ **Automatic AAB/APK building** with proper signing
- ✅ **Multi-track deployment** (internal, alpha, beta, production)
- ✅ **Staged rollouts** for production releases
- ✅ **ProGuard mapping upload** for crash analysis
- ✅ **Build caching** for faster execution
- ✅ **Secure credential management** via GitHub Secrets

## 📦 Prerequisites

Before setting up the workflow, ensure you have:

1. **Google Play Console Account**
   - Active Google Play Developer account ($25 one-time fee)
   - App created in Play Console with package name: `com.aryanvbw.focus`

2. **Service Account Credentials**
   - Google Cloud project linked to Play Console
   - Service account with Play Console API access
   - Service account JSON key file

3. **Signing Keystore**
   - Release keystore file (`.jks` or `.keystore`)
   - Keystore password, key alias, and key password
   - Same keystore used for all releases (cannot be changed later!)

4. **GitHub Repository Access**
   - Admin access to configure secrets
   - Ability to create and push to branches

## 🔧 Initial Setup

### Step 1: Create Google Play Service Account

1. **Go to Google Cloud Console**
   - Visit: https://console.cloud.google.com/
   - Create a new project or select existing one

2. **Enable Google Play Developer API**
   ```
   Navigation: APIs & Services → Library
   Search: "Google Play Android Developer API"
   Click: Enable
   ```

3. **Create Service Account**
   ```
   Navigation: IAM & Admin → Service Accounts
   Click: Create Service Account
   Name: "GitHub Actions Play Store Deploy"
   Description: "Service account for automated Play Store deployments"
   Click: Create and Continue
   ```

4. **Download Service Account Key**
   ```
   Click on the created service account
   Go to: Keys tab
   Click: Add Key → Create new key
   Format: JSON
   Click: Create (downloads JSON file)
   ```

5. **Grant Play Console Access**
   ```
   Go to: Google Play Console → Settings → API access
   Click: Link to the Google Cloud project (if not linked)
   Find your service account in the list
   Click: Grant access
   Permissions: 
     ✅ Admin (Releases)
     ✅ View app information
     ✅ Manage production releases
     ✅ Manage testing track releases
   Click: Invite user → Send invitation
   ```

### Step 2: Prepare Signing Keystore

If you already have a keystore, skip to encoding. Otherwise, create one:

```bash
# Create a new keystore (only if you don't have one!)
keytool -genkey -v \
  -keystore focus-release-key.jks \
  -keyalg RSA \
  -keysize 2048 \
  -validity 10000 \
  -alias focus-key

# You'll be prompted for:
# - Keystore password (remember this!)
# - Key password (remember this!)
# - Your name and organization details
```

**⚠️ CRITICAL: Backup your keystore securely!**
- Store it in a password manager
- Keep offline backups
- Never commit it to Git
- If lost, you cannot update your app on Play Store!

### Step 3: Encode Keystore to Base64

**macOS/Linux:**
```bash
base64 -i focus-release-key.jks > keystore_base64.txt
# Or copy to clipboard:
base64 -i focus-release-key.jks | pbcopy
```

**Windows (PowerShell):**
```powershell
[Convert]::ToBase64String([IO.File]::ReadAllBytes("focus-release-key.jks")) > keystore_base64.txt
# Or copy to clipboard:
[Convert]::ToBase64String([IO.File]::ReadAllBytes("focus-release-key.jks")) | Set-Clipboard
```

### Step 4: Configure GitHub Secrets

1. **Navigate to Repository Settings**
   ```
   GitHub Repository → Settings → Secrets and variables → Actions
   ```

2. **Add Required Secrets**

   Click "New repository secret" for each:

   | Secret Name | Description | Example Value |
   |-------------|-------------|---------------|
   | `KEYSTORE_BASE64` | Base64-encoded keystore file | (long base64 string from Step 3) |
   | `KEYSTORE_PASSWORD` | Keystore password | `MySecurePassword123!` |
   | `KEY_ALIAS` | Key alias from keystore | `focus-key` |
   | `KEY_PASSWORD` | Key password | `MyKeyPassword456!` |
   | `PLAY_STORE_SERVICE_ACCOUNT_JSON` | Service account JSON content | (entire JSON file content from Step 1) |

3. **Verify Secrets**
   - All 5 secrets should be listed
   - Secret values are hidden (cannot be viewed after creation)
   - If you made a mistake, delete and recreate the secret

### Step 5: Verify Workflow File

Ensure `.github/workflows/play-store-deploy.yml` exists in your repository.

```bash
# Check if workflow file exists
ls -la .github/workflows/play-store-deploy.yml

# If it doesn't exist, it should have been created with this guide
```

## 🚀 Deployment Methods

### Method 1: Manual Deployment (Recommended for Testing)

Use GitHub Actions UI to manually trigger deployment:

1. **Go to Actions Tab**
   ```
   GitHub Repository → Actions → Deploy to Google Play Console
   ```

2. **Click "Run workflow"**
   - Select branch (usually `main` or `app-main`)
   - Choose release track: `internal`, `alpha`, `beta`, or `production`
   - Set rollout percentage (for production only, 1-100)
   - Click "Run workflow"

3. **Monitor Progress**
   - Watch the workflow execution in real-time
   - Check for any errors in the logs
   - Verify deployment in Play Console

### Method 2: Branch-Based Deployment (Automatic)

Push to specific branches to trigger automatic deployment:

```bash
# Deploy to internal testing
git checkout -b release/internal
git push origin release/internal

# Deploy to alpha testing
git checkout -b release/alpha
git push origin release/alpha

# Deploy to beta testing
git checkout -b release/beta
git push origin release/beta
```

### Method 3: Tag-Based Production Deployment

Create a version tag to deploy to production:

```bash
# Ensure version is bumped in app/build.gradle first!
# Then create and push a tag:
git tag -a v2.0.5 -m "Release version 2.0.5"
git push origin v2.0.5

# This automatically deploys to production track
```

## 📊 Release Tracks

Google Play Console supports multiple release tracks:

### Internal Testing
- **Purpose**: Quick testing with internal team
- **Audience**: Up to 100 testers
- **Review**: No Google review required
- **Deployment**: Instant
- **Use Case**: Daily builds, feature testing

### Alpha Testing
- **Purpose**: Early adopter testing
- **Audience**: Unlimited testers (via opt-in link)
- **Review**: No Google review required
- **Deployment**: Instant
- **Use Case**: Weekly builds, beta features

### Beta Testing
- **Purpose**: Pre-release testing with larger audience
- **Audience**: Unlimited testers (via opt-in link or email list)
- **Review**: No Google review required
- **Deployment**: Instant
- **Use Case**: Release candidates, final testing

### Production
- **Purpose**: Public release to all users
- **Audience**: All app users
- **Review**: Full Google review (can take hours to days)
- **Deployment**: After review approval
- **Use Case**: Stable releases only

### Staged Rollouts (Production Only)

Gradually release to a percentage of users:

```yaml
# In workflow_dispatch, set rollout_percentage:
# 10% → 25% → 50% → 100%
```

Benefits:
- Monitor crash rates with small user base first
- Catch critical bugs before full rollout
- Ability to halt rollout if issues detected

## 🔐 Secrets Configuration Details

### KEYSTORE_BASE64
- **Format**: Base64-encoded binary file
- **Source**: Your release signing keystore
- **Validation**: Should be a very long string (several KB)
- **Common Issues**: 
  - Truncated during copy/paste
  - Extra whitespace or newlines
  - Wrong file encoded

### KEYSTORE_PASSWORD
- **Format**: Plain text password
- **Source**: Password you set when creating keystore
- **Validation**: Must match keystore exactly
- **Common Issues**:
  - Typos in password
  - Special characters not escaped
  - Wrong password used

### KEY_ALIAS
- **Format**: Plain text alias name
- **Source**: Alias specified when creating key
- **Validation**: Must exist in keystore
- **Common Issues**:
  - Case sensitivity
  - Wrong alias name
  - Alias doesn't exist in keystore

### KEY_PASSWORD
- **Format**: Plain text password
- **Source**: Password for the specific key
- **Validation**: Must match key password
- **Common Issues**:
  - Confused with keystore password
  - Typos in password

### PLAY_STORE_SERVICE_ACCOUNT_JSON
- **Format**: Complete JSON file content (plain text, NOT base64 encoded)
- **Source**: Downloaded from Google Cloud Console
- **Usage**: Passed directly to the upload action via `serviceAccountJsonPlainText` parameter
- **Validation**: Must be valid JSON with all fields (use `jq` or `python -m json.tool` to validate)
- **Common Issues**:
  - Partial JSON copied (missing opening/closing braces)
  - JSON formatting broken (invalid escape sequences)
  - Wrong service account (check client_email matches)
  - Service account not granted Play Console access (wait 5-10 minutes after granting)
  - Accidentally base64 encoded (should be plain JSON text)

## 🐛 Troubleshooting

### Build Fails: "Keystore not found"

**Cause**: KEYSTORE_BASE64 secret is missing or invalid

**Solution**:
```bash
# Re-encode keystore
base64 -i your-keystore.jks > keystore_base64.txt

# Copy entire content (including any line breaks)
cat keystore_base64.txt | pbcopy  # macOS
cat keystore_base64.txt | xclip   # Linux

# Update GitHub secret with new value
```

### Build Fails: "Incorrect keystore password"

**Cause**: KEYSTORE_PASSWORD doesn't match actual password

**Solution**:
```bash
# Verify keystore password locally
keytool -list -v -keystore your-keystore.jks
# Enter password when prompted

# If correct, update GitHub secret
# If forgotten, you must create new keystore (and new app!)
```

### Deploy Fails: "Service account not authorized"

**Cause**: Service account lacks Play Console permissions

**Solution**:
1. Go to Play Console → Settings → API access
2. Find your service account
3. Click "Grant access" or "Edit access"
4. Ensure these permissions are checked:
   - ✅ View app information
   - ✅ Manage production releases  
   - ✅ Manage testing track releases
5. Save changes
6. Wait 5-10 minutes for permissions to propagate
7. Retry deployment

### Deploy Fails: "Version code already exists"

**Cause**: Version code in build.gradle already uploaded

**Solution**:
```bash
# Bump version code in app/build.gradle
# Change: versionCode 6
# To:     versionCode 7

git add app/build.gradle
git commit -m "chore: bump version code"
git push
```

### Deploy Fails: "APK/AAB not signed correctly"

**Cause**: Signing configuration mismatch

**Solution**:
1. Verify all keystore secrets are correct
2. Test signing locally:
   ```bash
   ./gradlew assembleRelease
   # Check if APK is created successfully
   ```
3. Ensure keystore.properties is not committed to Git
4. Re-check KEY_ALIAS matches keystore

## ✅ Best Practices

### Version Management
```bash
# Always bump version before deploying
# versionCode: Must increase with each upload
# versionName: Semantic versioning (2.0.5)

# Use the bump script:
./scripts/bump_version.sh patch  # 2.0.4 → 2.0.5
./scripts/bump_version.sh minor  # 2.0.5 → 2.1.0
./scripts/bump_version.sh major  # 2.1.0 → 3.0.0
```

### Testing Strategy
```
1. Internal → Test with team (1-2 days)
2. Alpha → Test with early adopters (3-5 days)  
3. Beta → Test with larger group (1-2 weeks)
4. Production → Staged rollout (10% → 25% → 50% → 100%)
```

### Release Notes
Create `.github/whatsnew/` directory with release notes:

```bash
mkdir -p .github/whatsnew

# Create release notes for each language
echo "• Fixed crash on Android 14
• Improved blocking accuracy
• Performance optimizations" > .github/whatsnew/whatsnew-en-US

# Commit and push
git add .github/whatsnew/
git commit -m "docs: add release notes"
```

### Monitoring
After deployment:
1. Check Play Console for review status
2. Monitor crash reports in Play Console
3. Watch user reviews and ratings
4. Track rollout percentage (if staged)
5. Be ready to halt rollout if critical issues found

### Security
- ✅ Never commit keystore files to Git
- ✅ Never commit service account JSON to Git
- ✅ Use GitHub Secrets for all sensitive data
- ✅ Regularly rotate service account keys (annually)
- ✅ Backup keystore in secure location
- ✅ Use strong passwords for keystore

## 📚 Additional Resources

- [Google Play Console](https://play.google.com/console)
- [Play Console API Documentation](https://developers.google.com/android-publisher)
- [Android App Signing](https://developer.android.com/studio/publish/app-signing)
- [GitHub Actions Documentation](https://docs.github.com/en/actions)
- [Semantic Versioning](https://semver.org/)

## 🆘 Support

If you encounter issues:

1. Check workflow logs in Actions tab
2. Review this documentation thoroughly
3. Verify all secrets are correctly configured
4. Test build locally: `./gradlew bundleRelease`
5. Check Play Console for specific error messages
6. Open an issue in the repository with:
   - Workflow run URL
   - Error message (redact sensitive info)
   - Steps to reproduce

---

**Last Updated**: 2025-10-29  
**Workflow Version**: 1.0.0  
**Maintained by**: Focus Development Team

