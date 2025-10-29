# 🚀 Workflow Setup & Monitoring Guide

## Overview
This guide explains how to set up, monitor, and troubleshoot the automated release workflow for the Focus app.

## 📋 Prerequisites

1. **GitHub CLI installed**
   ```bash
   # Install GitHub CLI (if not already installed)
   brew install gh
   
   # Authenticate with GitHub
   gh auth login
   ```

2. **Required GitHub Secrets**
   You need to set up the following secrets in your GitHub repository:
   
   - `KEYSTORE_BASE64`: Base64 encoded keystore file
   - `KEYSTORE_PASSWORD`: Password for the keystore
   - `KEY_PASSWORD`: Password for the key
   
   **Note:** The key alias is hardcoded to `focus` in the workflow.

## 🔧 Setting Up Secrets

### 1. Encode your keystore to base64

```bash
# Navigate to your project directory
cd /Volumes/DATA_vivek/GITHUB/focus

# Encode the keystore
base64 -i keystore/focus-release-key.jks | pbcopy
```

This copies the base64 string to your clipboard.

### 2. Add secrets to GitHub

```bash
# Set KEYSTORE_BASE64 (paste from clipboard)
gh secret set KEYSTORE_BASE64 --repo AryanVBW/focus

# Set KEYSTORE_PASSWORD
gh secret set KEYSTORE_PASSWORD --repo AryanVBW/focus

# Set KEY_PASSWORD
gh secret set KEY_PASSWORD --repo AryanVBW/focus
```

### 3. Verify secrets are set

```bash
gh secret list --repo AryanVBW/focus
```

## 🎯 Workflow Trigger

The workflow **only runs on the `main` branch** when:
- You push commits to `main`
- Changes are NOT in: `*.md`, `docs/`, `.gitignore`, or `LICENSE`

## 🔍 Monitoring Workflows

### Option 1: Using the monitoring script

```bash
# Make the script executable
chmod +x monitor-workflow.sh

# Run the monitor
./monitor-workflow.sh
```

This script will:
- Show the latest 5 workflow runs
- Watch the most recent run in real-time
- Display logs if the workflow fails
- Show success message if it completes

### Option 2: Manual monitoring with GitHub CLI

```bash
# List recent workflow runs
gh run list --repo AryanVBW/focus --workflow auto-release.yml --limit 5

# Watch a specific run (replace RUN_ID)
gh run watch RUN_ID --repo AryanVBW/focus

# View logs for a failed run
gh run view RUN_ID --repo AryanVBW/focus --log-failed

# View all logs
gh run view RUN_ID --repo AryanVBW/focus --log
```

### Option 3: GitHub Web Interface

Visit: https://github.com/AryanVBW/focus/actions

## 🧪 Testing the Workflow

### 1. Trigger a workflow run manually

```bash
# Make a small change and push to main
git checkout main
git pull origin main

# Make a test commit
echo "# Test" >> test.txt
git add test.txt
git commit -m "test: trigger workflow"
git push origin main

# Monitor the run
./monitor-workflow.sh
```

### 2. Trigger workflow via GitHub CLI

```bash
gh workflow run auto-release.yml --repo AryanVBW/focus --ref main
```

## 🐛 Common Issues & Fixes

### Issue 1: "No key with alias found"
**Cause:** The keystore alias doesn't match.
**Fix:** The workflow now uses the hardcoded alias `focus`. Ensure your keystore has this alias.

```bash
# Check your keystore alias
keytool -list -v -keystore keystore/focus-release-key.jks | grep "Alias name"
```

### Issue 2: "Could not find or load main class GradleWrapperMain"
**Cause:** `gradle-wrapper.jar` is missing from the repository.
**Fix:** Already fixed - the jar file is now committed to the repo.

### Issue 3: Workflow doesn't trigger
**Cause:** You're pushing to the wrong branch or only changed ignored files.
**Fix:** 
- Ensure you're pushing to `main` branch
- Make sure changes aren't only in `.md`, `docs/`, `.gitignore`, or `LICENSE` files

### Issue 4: Build fails with signing errors
**Cause:** Incorrect keystore password or missing secrets.
**Fix:**
```bash
# Verify secrets are set
gh secret list --repo AryanVBW/focus

# Re-set the secrets if needed
gh secret set KEYSTORE_PASSWORD --repo AryanVBW/focus
gh secret set KEY_PASSWORD --repo AryanVBW/focus
```

## 📦 What the Workflow Does

1. **Checks out code** from the main branch
2. **Sets up JDK 17** with Gradle caching
3. **Bumps version** automatically (patch version)
4. **Commits version bump** back to the repository
5. **Decodes keystore** from secrets
6. **Creates keystore.properties** with correct alias
7. **Builds release APK** (or debug if keystore missing)
8. **Creates Git tag** for the version
9. **Creates GitHub Release** with release notes
10. **Uploads APK** as both release asset and artifact

## 📊 Workflow Outputs

After a successful run, you'll get:
- **GitHub Release** with version tag (e.g., `v2.0.2`)
- **APK file** attached to the release
- **Build artifact** stored for 90 days
- **Automatic changelog** from commits

## 🔄 Continuous Monitoring Setup

To continuously monitor and auto-fix issues, you can set up a cron job:

```bash
# Edit crontab
crontab -e

# Add this line to check every 30 minutes
*/30 * * * * cd /Volumes/DATA_vivek/GITHUB/focus && ./monitor-workflow.sh >> workflow-monitor.log 2>&1
```

## 📝 Quick Reference Commands

```bash
# Check workflow status
gh run list --repo AryanVBW/focus --workflow auto-release.yml

# Watch latest run
./monitor-workflow.sh

# View specific run logs
gh run view <RUN_ID> --repo AryanVBW/focus --log

# Re-run failed workflow
gh run rerun <RUN_ID> --repo AryanVBW/focus

# Cancel running workflow
gh run cancel <RUN_ID> --repo AryanVBW/focus

# Download artifacts
gh run download <RUN_ID> --repo AryanVBW/focus

# List releases
gh release list --repo AryanVBW/focus

# View latest release
gh release view --repo AryanVBW/focus
```

## 🎓 Best Practices

1. **Always test locally first** before pushing to main
2. **Review workflow logs** after each run
3. **Keep secrets updated** if you regenerate keystores
4. **Monitor the first few runs** after setup
5. **Check release artifacts** to ensure APK is properly signed

## 🆘 Getting Help

If you encounter issues:

1. Check the workflow logs: `gh run view <RUN_ID> --log-failed`
2. Verify secrets are set: `gh secret list`
3. Check keystore alias: `keytool -list -v -keystore keystore/focus-release-key.jks`
4. Review this guide for common issues
5. Check GitHub Actions tab in the repository

## 📞 Support

For more help, check:
- [GitHub Actions Documentation](https://docs.github.com/en/actions)
- [GitHub CLI Documentation](https://cli.github.com/manual/)
- [Gradle Documentation](https://docs.gradle.org/)
