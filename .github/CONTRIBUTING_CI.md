# Contributing with CI/CD

This guide helps you understand and work with the automated CI/CD workflows in the Focus project.

## 🚀 Quick Start

### Creating a Pull Request

1. **Create your feature branch**
   ```bash
   git checkout -b feature/your-feature-name
   ```

2. **Make your changes**
   - Edit code in `Focus-app/` for Android changes
   - Edit code in `Focus-site/` for website changes

3. **Test locally before pushing**
   
   **For Android:**
   ```bash
   cd Focus-app
   ./gradlew clean
   ./gradlew assembleDebug
   ./gradlew testDebugUnitTest
   ./gradlew lintDebug
   ```
   
   **For Website:**
   ```bash
   cd Focus-site
   npm ci
   npm run lint
   npm run build
   npm run preview
   ```

4. **Commit and push**
   ```bash
   git add .
   git commit -m "feat: your feature description"
   git push origin feature/your-feature-name
   ```

5. **Create Pull Request**
   - Go to GitHub and create a PR
   - Fill out the PR template
   - Wait for automated checks to run

## 🤖 What Happens Automatically

When you create or update a PR, the workflow automatically:

### ✅ Checks Performed

1. **Android App**
   - ☕ Sets up Java 17 environment
   - 🏗️ Builds debug APK
   - 🧪 Runs unit tests
   - 🔍 Performs lint checks
   - 📦 Uploads APK and reports

2. **Website**
   - 📦 Sets up Node.js 18 environment
   - 📥 Installs dependencies
   - 🔍 Runs ESLint
   - 🏗️ Builds production bundle
   - 📦 Uploads build artifacts

3. **Screenshots** (if website builds successfully)
   - 📸 Captures desktop view (full page)
   - 📸 Captures desktop view (viewport)
   - 📸 Captures mobile view (375x812)
   - 📸 Captures tablet view (768x1024)
   - 💬 Posts comment with download links

4. **Status Report**
   - 📊 Aggregates all results
   - 💬 Posts comprehensive summary
   - ✅ Sets final pass/fail status

### 📬 Comments You'll Receive

The bot will comment on your PR with:

- ❌ **Build failures** - If compilation fails
- ⚠️ **Test failures** - If tests don't pass
- ⚠️ **Lint issues** - If code quality issues found
- 📸 **Screenshots ready** - When UI screenshots are captured
- 📊 **Final summary** - Overall status of all checks

## 🔍 Understanding Check Results

### ✅ All Checks Passed

```
✅ Android App - Build & Test: ✅ Build and tests passed
✅ Website - Build & Test: ✅ Build passed
✅ Screenshots: Captured successfully
```

**What to do:**
- Review the code changes
- Download and test the APK if needed
- Check the screenshots for UI changes
- Request review from team members

### ❌ Some Checks Failed

```
❌ Android App - Build & Test: ❌ Build failed
✅ Website - Build & Test: ✅ Build passed
⏭️ Screenshots: Skipped (build failed)
```

**What to do:**
1. Click on the failed check to see details
2. Review the error messages
3. Fix the issues locally
4. Test the fix
5. Push the changes
6. Wait for checks to run again

## 📦 Downloading Artifacts

### From PR Comments

1. Look for the final status comment
2. Click on artifact links (e.g., "focus-app-debug.apk")
3. Download from the workflow run page

### From Actions Tab

1. Go to the "Actions" tab in GitHub
2. Find your workflow run
3. Scroll to "Artifacts" section at the bottom
4. Download the artifacts you need

### Available Artifacts

| Artifact | Description | When Available |
|----------|-------------|----------------|
| `focus-app-debug` | Debug APK | Android build succeeds |
| `android-test-results` | Test reports | Tests run (pass or fail) |
| `android-lint-results` | Lint reports | Lint check runs |
| `focus-site-dist` | Built website | Website build succeeds |
| `ui-screenshots` | UI screenshots | Screenshots captured |

## 🐛 Troubleshooting

### Build Fails in CI But Works Locally

**Possible causes:**
- Different Java/Node versions
- Cached dependencies
- Environment-specific configurations

**Solutions:**
```bash
# For Android
cd Focus-app
./gradlew clean
./gradlew --stop
rm -rf ~/.gradle/caches/
./gradlew assembleDebug

# For Website
cd Focus-site
rm -rf node_modules package-lock.json
npm install
npm run build
```

### Tests Pass Locally But Fail in CI

**Possible causes:**
- Timezone differences
- File path differences (case sensitivity)
- Missing test dependencies

**Solutions:**
- Check test logs in the workflow run
- Ensure tests don't depend on local environment
- Use relative paths in tests
- Mock external dependencies

### Screenshots Not Generated

**Possible causes:**
- Website build failed
- Preview server didn't start
- Playwright installation failed

**Solutions:**
- Ensure website builds successfully
- Check if `npm run preview` works locally
- Review screenshot job logs

### Workflow Not Triggering

**Possible causes:**
- PR targets wrong branch
- Workflow file has syntax errors
- GitHub Actions disabled

**Solutions:**
- Ensure PR targets `main` or `develop`
- Validate YAML syntax
- Check repository settings → Actions

## 🎯 Best Practices

### Before Creating PR

1. **Run all checks locally**
   ```bash
   # Android
   cd Focus-app && ./gradlew clean assembleDebug testDebugUnitTest lintDebug
   
   # Website
   cd Focus-site && npm ci && npm run lint && npm run build
   ```

2. **Fix all warnings**
   - Don't ignore lint warnings
   - Address test warnings
   - Clean up console logs

3. **Test on multiple devices/browsers**
   - Android: Test on different API levels
   - Website: Test on Chrome, Firefox, Safari

### During PR Review

1. **Monitor workflow runs**
   - Check Actions tab regularly
   - Review all comments from the bot
   - Download and test artifacts

2. **Respond to failures quickly**
   - Fix issues as soon as they're reported
   - Don't wait for reviewer feedback on CI failures
   - Push fixes promptly

3. **Keep PR updated**
   - Rebase on target branch if needed
   - Resolve merge conflicts
   - Re-run checks after updates

### After PR Approval

1. **Ensure all checks pass**
   - Green checkmarks on all jobs
   - No pending workflows
   - All comments addressed

2. **Final verification**
   - Download and test final APK
   - Review final screenshots
   - Verify all changes included

## 🔧 Advanced Usage

### Re-running Failed Checks

1. Go to the "Checks" tab in your PR
2. Find the failed check
3. Click "Re-run jobs" → "Re-run failed jobs"

### Skipping CI (Not Recommended)

If you need to skip CI for documentation-only changes:
```bash
git commit -m "docs: update README [skip ci]"
```

**Note:** This is not recommended as it bypasses quality checks.

### Debugging Workflow Issues

1. **Enable debug logging**
   - Go to repository Settings → Secrets
   - Add `ACTIONS_STEP_DEBUG` = `true`
   - Re-run the workflow

2. **Review workflow logs**
   - Expand each step in the workflow run
   - Look for error messages
   - Check environment variables

3. **Test workflow changes**
   - Create a test branch
   - Modify workflow file
   - Create a draft PR to test

## 📊 Workflow Metrics

### Typical Run Times

- **Android Build & Test**: 5-8 minutes
- **Website Build & Test**: 2-3 minutes
- **Screenshot Capture**: 2-3 minutes
- **Final Status Report**: < 1 minute

**Total**: ~10-15 minutes for a complete run

### Resource Usage

- **Disk Space**: ~2 GB (including caches)
- **Memory**: ~4 GB peak
- **CPU**: 2 cores utilized

## 🆘 Getting Help

### If Checks Fail

1. **Read the error message** - Usually tells you what's wrong
2. **Check the logs** - Detailed information in workflow run
3. **Search existing issues** - Someone may have faced this before
4. **Ask for help** - Comment on your PR or create an issue

### Useful Commands

```bash
# View workflow runs
gh run list

# View specific run
gh run view <run-id>

# Download artifacts
gh run download <run-id>

# Re-run failed jobs
gh run rerun <run-id> --failed
```

## 📚 Additional Resources

- [GitHub Actions Documentation](https://docs.github.com/en/actions)
- [Workflow File](.github/workflows/pr-check.yml)
- [Workflow Diagram](.github/workflows/WORKFLOW_DIAGRAM.md)
- [Workflow README](.github/workflows/README.md)

---

## 🎓 Learning Path

### Beginner
1. Understand what triggers the workflow
2. Learn to read check results
3. Know how to download artifacts
4. Fix basic build errors

### Intermediate
1. Understand each job's purpose
2. Debug failing tests
3. Interpret lint results
4. Optimize local testing

### Advanced
1. Modify workflow configuration
2. Add new checks or jobs
3. Optimize workflow performance
4. Create custom actions

---

*Happy Contributing! 🚀*

