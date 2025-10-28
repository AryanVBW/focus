# 📋 Release Checklist

Quick reference for the automated release process.

## 🚀 Automatic Release (Default)

Every commit to `app-main` automatically:
- ✅ Bumps patch version (e.g., 2.0.0 → 2.0.1)
- ✅ Increments build number
- ✅ Builds signed APK
- ✅ Creates GitHub release
- ✅ Generates release notes
- ✅ Creates version tag

**No manual action required!**

## 🔧 Manual Version Bump (Major/Minor)

When you need to bump major or minor versions:

### Bump Minor Version (2.0.5 → 2.1.0)
```bash
./scripts/bump_version.sh minor
git push origin app-main
```

### Bump Major Version (2.1.0 → 3.0.0)
```bash
./scripts/bump_version.sh major
git push origin app-main
```

### Bump Patch Version (2.1.0 → 2.1.1)
```bash
./scripts/bump_version.sh patch
git push origin app-main
```

## 📝 Commit Message Guidelines

Use conventional commits for better release notes:

```
feat: add new feature
fix: resolve bug
chore: update dependencies
docs: update documentation
refactor: code improvements
perf: performance optimization
test: add tests
```

## ⚙️ Required GitHub Secrets

Ensure these are configured in **Settings → Secrets**:

- [ ] `KEYSTORE_BASE64` - Base64 encoded keystore
- [ ] `KEYSTORE_PASSWORD` - Keystore password
- [ ] `KEY_ALIAS` - Key alias name
- [ ] `KEY_PASSWORD` - Key password

## 🔍 Pre-Release Checklist

Before pushing to `app-main`:

- [ ] Code compiles without errors
- [ ] All tests pass
- [ ] No critical bugs
- [ ] Features tested on multiple devices
- [ ] Permissions are correctly declared
- [ ] ProGuard rules updated (if needed)
- [ ] Changelog/commit messages are clear

## 📦 Post-Release Verification

After workflow completes:

- [ ] Check Actions tab for successful run
- [ ] Verify release appears in Releases page
- [ ] Download and test APK
- [ ] Verify version number is correct
- [ ] Check release notes are accurate
- [ ] Test installation on clean device

## 🐛 Troubleshooting

### Workflow Failed
1. Check Actions tab for error logs
2. Verify all secrets are set
3. Ensure keystore is valid
4. Check build.gradle syntax

### Version Conflict
```bash
git pull origin app-main
# Resolve conflicts
git push origin app-main
```

### Need to Rollback
```bash
# Delete the release and tag from GitHub
git tag -d v2.0.1
git push origin :refs/tags/v2.0.1

# Revert the version bump commit
git revert HEAD
git push origin app-main
```

## 📊 Monitoring

- **Workflow Status**: Actions tab
- **Release Downloads**: Releases page → Assets
- **Build Artifacts**: Actions → Workflow run → Artifacts

## 🔗 Quick Links

- [Full Documentation](../docs/RELEASE_WORKFLOW.md)
- [Setup Guide](../docs/SETUP_RELEASE_AUTOMATION.md)
- [Actions Dashboard](../../actions)
- [Releases Page](../../releases)

---

**Last Updated**: 2025-01-29
