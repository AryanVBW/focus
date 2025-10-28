# 🛠️ Setup Guide: Release Automation

This guide will help you set up the automated release workflow for the Focus Android app.

## ✅ Prerequisites

- GitHub repository with admin access
- Android keystore file (for signed releases)
- Git installed locally
- Basic understanding of GitHub Actions

## 📋 Step-by-Step Setup

### Step 1: Verify Workflow Files

Ensure these files exist in your repository:

```
.github/
  └── workflows/
      └── auto-release.yml          # Main workflow file
scripts/
  └── bump_version.sh               # Manual version bump script
docs/
  ├── RELEASE_WORKFLOW.md           # Workflow documentation
  └── SETUP_RELEASE_AUTOMATION.md   # This file
```

### Step 2: Make Scripts Executable

```bash
chmod +x scripts/bump_version.sh
git add scripts/bump_version.sh
git commit -m "chore: make bump_version script executable"
```

### Step 3: Configure GitHub Secrets

#### 3.1 Generate or Locate Your Keystore

**If you already have a keystore:**
- Locate your `keystore.jks` file
- Note down the passwords and alias

**If you need to create a new keystore:**

```bash
keytool -genkey -v \
  -keystore keystore.jks \
  -keyalg RSA \
  -keysize 2048 \
  -validity 10000 \
  -alias focus-key
```

You'll be prompted for:
- Keystore password (remember this!)
- Key password (remember this!)
- Your name and organization details

**Save these details securely:**
- Keystore password: `____________`
- Key alias: `____________`
- Key password: `____________`

#### 3.2 Convert Keystore to Base64

**macOS/Linux:**
```bash
base64 -i keystore.jks > keystore_base64.txt
```

**Windows (PowerShell):**
```powershell
[Convert]::ToBase64String([IO.File]::ReadAllBytes("keystore.jks")) > keystore_base64.txt
```

#### 3.3 Add Secrets to GitHub

1. Go to your GitHub repository
2. Click **Settings** → **Secrets and variables** → **Actions**
3. Click **New repository secret**
4. Add each of the following secrets:

| Secret Name | Value | Example |
|-------------|-------|---------|
| `KEYSTORE_BASE64` | Contents of `keystore_base64.txt` | (long base64 string) |
| `KEYSTORE_PASSWORD` | Your keystore password | `MySecurePassword123` |
| `KEY_ALIAS` | Your key alias | `focus-key` |
| `KEY_PASSWORD` | Your key password | `MyKeyPassword456` |

**Important:** 
- Copy the entire base64 string (it may be very long)
- Don't include any extra spaces or newlines
- Double-check passwords for typos

### Step 4: Verify Branch Name

The workflow is configured for the `app-main` branch. Verify your default branch:

```bash
git branch --show-current
```

**If your branch is named differently:**

Option A: Rename your branch to `app-main`
```bash
git branch -m old-branch-name app-main
git push origin app-main
git push origin --delete old-branch-name
```

Option B: Update the workflow file
Edit `.github/workflows/auto-release.yml` line 5:
```yaml
branches:
  - your-branch-name  # Change this
```

### Step 5: Set Default Branch on GitHub

1. Go to **Settings** → **Branches**
2. Set `app-main` as the default branch
3. Click **Update**

### Step 6: Test the Workflow

#### 6.1 Make a Test Commit

```bash
# Make a small change
echo "# Test" >> README.md

# Commit and push
git add README.md
git commit -m "test: verify automated release workflow"
git push origin app-main
```

#### 6.2 Monitor the Workflow

1. Go to the **Actions** tab in your GitHub repository
2. You should see a new workflow run starting
3. Click on it to view the progress
4. Wait for all steps to complete (usually 5-10 minutes)

#### 6.3 Verify the Release

1. Go to the **Releases** page
2. You should see a new release with:
   - Version tag (e.g., `v2.0.1`)
   - Release notes
   - APK file attached

### Step 7: Configure Branch Protection (Optional but Recommended)

1. Go to **Settings** → **Branches**
2. Click **Add rule**
3. Branch name pattern: `app-main`
4. Enable:
   - ✅ Require a pull request before merging
   - ✅ Require status checks to pass before merging
   - ✅ Require branches to be up to date before merging
5. Click **Create**

## 🔍 Verification Checklist

Use this checklist to ensure everything is set up correctly:

- [ ] Workflow file exists at `.github/workflows/auto-release.yml`
- [ ] Scripts directory contains `bump_version.sh`
- [ ] `bump_version.sh` is executable (`chmod +x`)
- [ ] All 4 GitHub secrets are configured
- [ ] Default branch is set to `app-main`
- [ ] Test commit triggers the workflow
- [ ] Workflow completes successfully
- [ ] Release is created with APK attached
- [ ] Version in `app/build.gradle` is bumped
- [ ] Git tag is created

## 🐛 Common Issues and Solutions

### Issue: Workflow doesn't trigger

**Solution:**
- Verify you pushed to the correct branch (`app-main`)
- Check if changes are in ignored paths (`.md` files, etc.)
- Ensure workflow file is in `.github/workflows/` directory

### Issue: Build fails with "Keystore not found"

**Solution:**
- Verify `KEYSTORE_BASE64` secret is set correctly
- Ensure the base64 string is complete (no truncation)
- Check that secret names match exactly (case-sensitive)

### Issue: Signing fails

**Solution:**
- Verify all keystore secrets (`KEYSTORE_PASSWORD`, `KEY_ALIAS`, `KEY_PASSWORD`)
- Ensure passwords don't contain special characters that need escaping
- Test keystore locally: `keytool -list -v -keystore keystore.jks`

### Issue: Version conflict

**Solution:**
```bash
git pull origin app-main
# Resolve any conflicts
git push origin app-main
```

### Issue: Permission denied on scripts

**Solution:**
```bash
chmod +x scripts/bump_version.sh
git add scripts/bump_version.sh
git commit -m "fix: add execute permission to scripts"
git push origin app-main
```

### Issue: Release already exists

**Solution:**
- Delete the existing release and tag from GitHub
- Or manually bump the version before pushing

## 📊 Understanding the Workflow Output

After a successful run, you'll see:

### 1. Updated Version in Code
Check `app/build.gradle`:
```gradle
versionCode 3        // Incremented
versionName "2.0.1"  // Patch version bumped
```

### 2. New Git Commit
```
chore: bump version to 2.0.1 (build 3)
```

### 3. New Git Tag
```
v2.0.1
```

### 4. GitHub Release
- Title: Focus v2.0.1
- Tag: v2.0.1
- Assets: Focus-v2.0.1-release.apk
- Release notes with commit history

### 5. Workflow Artifact
- Available in Actions tab
- Retained for 90 days
- Downloadable by repository members

## 🎯 Next Steps

After successful setup:

1. **Update README**: Add a badge showing build status
   ```markdown
   ![Build Status](https://github.com/YOUR_USERNAME/focus/actions/workflows/auto-release.yml/badge.svg)
   ```

2. **Create Release Template**: Add `.github/RELEASE_TEMPLATE.md` for consistent release notes

3. **Set Up Notifications**: Configure Slack/Discord webhooks for release notifications

4. **Monitor Usage**: Track download statistics in the Releases page

5. **Plan Versioning**: Decide when to bump minor/major versions

## 📚 Additional Configuration

### Custom Release Notes Format

Edit the "Generate Release Notes" step in `auto-release.yml` to customize the format.

### Multiple Build Variants

Add steps to build different flavors:
```yaml
- name: Build Production Release
  run: ./gradlew assembleProductionRelease
  
- name: Build Staging Release
  run: ./gradlew assembleStagingRelease
```

### Pre-release Builds

Add a condition for pre-release:
```yaml
- name: Create GitHub Release
  uses: softprops/action-gh-release@v1
  with:
    prerelease: ${{ contains(github.ref, 'beta') }}
```

## 🆘 Getting Help

If you encounter issues:

1. **Check Workflow Logs**: Actions tab → Failed run → View logs
2. **Review Documentation**: Read `RELEASE_WORKFLOW.md`
3. **Test Locally**: Run `./gradlew assembleRelease` locally
4. **Verify Secrets**: Ensure all secrets are correctly set
5. **Open an Issue**: Create a GitHub issue with error details

## 📞 Support Resources

- [GitHub Actions Documentation](https://docs.github.com/en/actions)
- [Android Signing Documentation](https://developer.android.com/studio/publish/app-signing)
- [Focus Repository Issues](https://github.com/AryanVBW/focus/issues)

---

**Setup Complete!** 🎉

Your automated release workflow is now configured. Every commit to `app-main` will automatically:
- Bump the version
- Build a signed APK
- Create a GitHub release
- Generate detailed release notes

Happy releasing! 🚀
