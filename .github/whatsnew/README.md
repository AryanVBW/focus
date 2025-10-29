# Release Notes for Google Play Console

This directory contains "What's New" release notes that are automatically uploaded to Google Play Console during deployment.

## File Naming Convention

Files should be named using the format: `whatsnew-<locale>`

Examples:
- `whatsnew-en-US` - English (United States)
- `whatsnew-es-ES` - Spanish (Spain)
- `whatsnew-fr-FR` - French (France)
- `whatsnew-de-DE` - German (Germany)
- `whatsnew-ja-JP` - Japanese (Japan)
- `whatsnew-zh-CN` - Chinese (Simplified)

## Content Guidelines

### Character Limits
- **Maximum**: 500 characters per language
- **Recommended**: 200-300 characters for better readability
- **Minimum**: At least one meaningful change

### Best Practices

1. **Be Concise**: Users scan quickly, keep it brief
2. **Highlight Key Changes**: Focus on user-facing improvements
3. **Use Bullet Points**: Makes content scannable
4. **Start with Action Verbs**: "Fixed", "Added", "Improved"
5. **Avoid Technical Jargon**: Use user-friendly language

### Good Examples

✅ **Good**:
```
• Fixed crash when opening blocked apps
• Added dark mode support
• Improved battery efficiency
• Enhanced blocking accuracy
```

❌ **Bad**:
```
• Refactored AccessibilityService implementation
• Updated Gradle dependencies to latest versions
• Fixed NPE in BlockingManager.kt line 234
• Migrated to Kotlin coroutines Flow API
```

## Updating Release Notes

### For Each Release

1. **Update the content** in `whatsnew-en-US` (and other locales if available)
2. **Commit the changes**:
   ```bash
   git add .github/whatsnew/
   git commit -m "docs: update release notes for v2.0.5"
   git push
   ```
3. **Deploy** using the workflow - release notes will be automatically uploaded

### Template

Copy this template for new releases:

```
• [Major feature or fix]
• [Important improvement]
• [Notable change]
• Bug fixes and performance improvements
```

## Supported Locales

Common locale codes for Google Play:

| Locale Code | Language |
|-------------|----------|
| `en-US` | English (United States) |
| `en-GB` | English (United Kingdom) |
| `es-ES` | Spanish (Spain) |
| `es-419` | Spanish (Latin America) |
| `fr-FR` | French (France) |
| `de-DE` | German (Germany) |
| `it-IT` | Italian (Italy) |
| `pt-BR` | Portuguese (Brazil) |
| `pt-PT` | Portuguese (Portugal) |
| `ru-RU` | Russian (Russia) |
| `ja-JP` | Japanese (Japan) |
| `ko-KR` | Korean (South Korea) |
| `zh-CN` | Chinese (Simplified) |
| `zh-TW` | Chinese (Traditional) |
| `ar` | Arabic |
| `hi-IN` | Hindi (India) |

## Automation

The GitHub Actions workflow automatically:
1. Reads files from this directory
2. Uploads them to the appropriate release track
3. Associates them with the new app version

No manual upload to Play Console is needed!

## Tips

- Keep a changelog in your repository for detailed technical changes
- Use release notes for user-facing changes only
- Update notes before triggering deployment
- Consider A/B testing different messaging styles
- Monitor user reviews to see if changes are well-received

---

**Last Updated**: 2025-10-29

