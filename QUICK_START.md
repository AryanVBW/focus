# 🚀 Quick Start Guide

## ⚡ Immediate Steps

### 1. Install GitHub CLI (if not installed)
```bash
brew install gh
gh auth login
```

### 2. Run the Setup Script
```bash
./setup-and-monitor.sh
```

This interactive script will help you:
- ✅ Set up GitHub secrets
- ✅ Monitor workflow runs
- ✅ View logs and debug issues
- ✅ Trigger new builds

## 🎯 Key Points

### ✅ What's Fixed
- ✅ Workflow now runs on **`main` branch only**
- ✅ Keystore alias hardcoded to `focus` (no more alias errors)
- ✅ gradle-wrapper.jar is now in the repository

### 🔧 What You Need to Do

1. **Install GitHub CLI**
   ```bash
   brew install gh
   gh auth login
   ```

2. **Set up secrets** (use the setup script or manually):
   ```bash
   # Option 1: Use the interactive script
   ./setup-and-monitor.sh
   # Then choose option 1
   
   # Option 2: Manual setup
   base64 -i keystore/focus-release-key.jks | gh secret set KEYSTORE_BASE64 --repo AryanVBW/focus
   gh secret set KEYSTORE_PASSWORD --repo AryanVBW/focus
   gh secret set KEY_PASSWORD --repo AryanVBW/focus
   ```

3. **Merge changes to main branch**
   ```bash
   git checkout main
   git merge app-main
   git push origin main
   ```

4. **Monitor the workflow**
   ```bash
   ./setup-and-monitor.sh
   # Choose option 3 to watch the latest run
   ```

## 📋 Workflow Behavior

The workflow will **automatically run** when:
- ✅ You push to the `main` branch
- ✅ Changes are in code files (not just .md, docs/, .gitignore, LICENSE)

The workflow will **NOT run** on:
- ❌ `app-main` branch
- ❌ Other branches
- ❌ Changes only to documentation files

## 🐛 If Build Fails

1. **Check the logs**
   ```bash
   ./setup-and-monitor.sh
   # Choose option 4 to view failed logs
   ```

2. **Common fixes**:
   - Verify secrets are set: `gh secret list --repo AryanVBW/focus`
   - Check keystore alias: `keytool -list -v -keystore keystore/focus-release-key.jks`
   - Re-run the workflow: Use option 5 in the setup script

3. **Re-run the workflow**
   ```bash
   ./setup-and-monitor.sh
   # Choose option 5
   ```

## 📦 After Successful Build

You'll get:
- 🎉 New GitHub Release with version tag
- 📱 APK file attached to the release
- 📊 Build artifacts stored for 90 days
- 📝 Automatic changelog from commits

## 🔍 Quick Commands

```bash
# List recent workflow runs
gh run list --repo AryanVBW/focus --workflow auto-release.yml

# Watch latest run
gh run watch $(gh run list --repo AryanVBW/focus --workflow auto-release.yml --limit 1 --json databaseId --jq '.[0].databaseId') --repo AryanVBW/focus

# View failed logs
gh run view $(gh run list --repo AryanVBW/focus --workflow auto-release.yml --limit 1 --json databaseId --jq '.[0].databaseId') --repo AryanVBW/focus --log-failed

# List releases
gh release list --repo AryanVBW/focus
```

## 📚 More Information

For detailed documentation, see:
- 📖 [WORKFLOW_GUIDE.md](./WORKFLOW_GUIDE.md) - Complete workflow documentation
- 🔧 [.github/workflows/auto-release.yml](./.github/workflows/auto-release.yml) - Workflow configuration

## 🆘 Need Help?

1. Run the setup script: `./setup-and-monitor.sh`
2. Check the workflow guide: `WORKFLOW_GUIDE.md`
3. View GitHub Actions: https://github.com/AryanVBW/focus/actions
