# GitHub Actions Workflows

This directory contains automated workflows for the Focus project.

## 📋 Available Workflows

### PR Check Workflow (`pr-check.yml`)

Automatically runs comprehensive checks on every pull request to ensure code quality and functionality.

#### 🎯 Purpose

The PR Check workflow provides automated validation for pull requests by:
- Building both the Android app and website
- Running tests and linting
- Capturing UI screenshots
- Providing detailed feedback on PR status

#### 🚀 Triggers

The workflow runs automatically when:
- A pull request is **opened**
- A pull request is **synchronized** (new commits pushed)
- A pull request is **reopened**

Target branches: `main`, `develop`

#### 🏗️ Workflow Jobs

##### 1. Android App - Build & Test

**What it does:**
- Sets up JDK 17 with Gradle caching
- Cleans and builds the debug APK
- Runs unit tests
- Performs lint checks
- Uploads test results and APK as artifacts

**Outputs:**
- Debug APK file
- Test results (HTML reports)
- Lint results (HTML reports)

**Error Handling:**
- Posts detailed comment if build fails
- Posts warning if tests fail
- Includes links to logs and artifacts

##### 2. Website - Build & Test

**What it does:**
- Sets up Node.js 18 with npm caching
- Installs dependencies
- Runs ESLint for code quality
- Builds the website using Vite
- Uploads build artifacts

**Outputs:**
- Built website (dist folder)
- Lint results

**Error Handling:**
- Posts detailed comment if build fails
- Posts warning if lint issues found
- Provides troubleshooting steps

##### 3. Capture UI Screenshots

**What it does:**
- Downloads the built website
- Starts a preview server
- Uses Playwright to capture screenshots in multiple viewports:
  - Desktop (1920x1080) - Full page
  - Desktop (1920x1080) - Viewport only
  - Mobile (375x812) - iPhone view
  - Tablet (768x1024) - iPad view
- Uploads screenshots as artifacts

**Requirements:**
- Only runs if website build succeeds
- Uses Playwright with Chromium browser

**Outputs:**
- PNG screenshots for all viewports
- Comment with download instructions

##### 4. Final Status Report

**What it does:**
- Aggregates results from all jobs
- Posts comprehensive status report
- Provides links to all artifacts
- Suggests next steps

**Report includes:**
- Status table for all components
- Overall pass/fail status
- Links to artifacts (APK, website, screenshots)
- Actionable next steps

#### 📦 Artifacts

All artifacts are retained for **7 days** (screenshots for **30 days**):

| Artifact Name | Description | Retention |
|---------------|-------------|-----------|
| `focus-app-debug` | Debug APK of Android app | 7 days |
| `android-test-results` | Unit test results and reports | 7 days |
| `android-lint-results` | Lint check results | 7 days |
| `focus-site-dist` | Built website files | 7 days |
| `ui-screenshots` | UI screenshots (multiple viewports) | 30 days |

#### 💬 PR Comments

The workflow automatically posts comments on your PR:

1. **Build Failure** - If Android or website build fails
2. **Test Failure** - If unit tests fail
3. **Lint Issues** - If ESLint finds problems
4. **Screenshots Ready** - When screenshots are captured
5. **Final Status** - Comprehensive summary of all checks

#### 🔧 Configuration

##### Environment Variables

```yaml
JAVA_VERSION: '17'    # Java version for Android builds
NODE_VERSION: '18'    # Node.js version for website builds
```

##### Customization

To modify the workflow:

1. **Change Java version**: Update `JAVA_VERSION` in `env` section
2. **Change Node version**: Update `NODE_VERSION` in `env` section
3. **Add more screenshot viewports**: Edit the `capture-screenshots.js` script
4. **Change artifact retention**: Modify `retention-days` in upload steps
5. **Add more tests**: Add steps in the respective job sections

#### 🐛 Troubleshooting

##### Build Fails Locally But Passes in CI

- Ensure you're using the same Java/Node versions
- Clear local caches: `./gradlew clean` or `npm clean-install`
- Check for environment-specific configurations

##### Screenshots Not Captured

- Verify website build succeeded
- Check if preview server started correctly
- Review Playwright installation logs

##### Workflow Not Triggering

- Ensure PR targets `main` or `develop` branch
- Check if workflow file is in the correct location
- Verify YAML syntax is correct

##### Permission Issues

- Workflow uses `GITHUB_TOKEN` automatically
- No additional secrets needed for basic functionality
- For custom integrations, add secrets in repository settings

#### 📊 Status Checks

The workflow creates the following status checks:

- ✅ `Android App - Build & Test` - Must pass for merge
- ✅ `Website - Build & Test` - Must pass for merge
- ℹ️ `Capture UI Screenshots` - Optional (skipped if build fails)
- ℹ️ `Final Status Report` - Always runs

#### 🎯 Best Practices

1. **Review artifacts before merging**
   - Download and test the APK
   - Review screenshots for UI changes
   - Check test reports for any warnings

2. **Fix issues promptly**
   - Address build failures immediately
   - Don't ignore lint warnings
   - Fix failing tests before requesting review

3. **Keep PRs focused**
   - Smaller PRs = faster CI runs
   - Separate Android and website changes when possible
   - Group related changes together

4. **Monitor workflow runs**
   - Check the Actions tab regularly
   - Review logs for warnings
   - Update workflow as project evolves

#### 🔄 Workflow Updates

To update the workflow:

1. Edit `.github/workflows/pr-check.yml`
2. Test changes in a feature branch
3. Create a PR to review workflow changes
4. Merge after verification

#### 📚 Additional Resources

- [GitHub Actions Documentation](https://docs.github.com/en/actions)
- [Gradle Build Scans](https://scans.gradle.com/)
- [Playwright Documentation](https://playwright.dev/)
- [Vite Documentation](https://vitejs.dev/)

#### 🆘 Getting Help

If you encounter issues with the workflow:

1. Check the workflow run logs
2. Review this documentation
3. Search existing GitHub issues
4. Create a new issue with:
   - Workflow run link
   - Error messages
   - Steps to reproduce

---

## 🔐 Security Notes

- Workflow uses `GITHUB_TOKEN` with minimal permissions
- No secrets are exposed in logs
- Artifacts are only accessible to repository members
- Screenshots don't contain sensitive data

## 📝 Maintenance

This workflow should be reviewed and updated:
- When upgrading Java/Node versions
- When adding new build steps
- When project structure changes
- Quarterly for dependency updates

---

*Last updated: 2025-10-07*

