# Contributing to Focus App

Thank you for your interest in contributing to the Focus App! This document provides guidelines and information for contributors.

## 📁 Project Structure

Please familiarize yourself with our organized project structure:

### Code Organization
- **`app/src/main/java/com/focus/app/`** - Main application code
  - `blocking/` - Scroll blocking and content filtering
  - `detection/` - Content detection and video analysis
  - `model/` - Data models and structures
  - `ui/` - User interface components
  - `service/` - Background services
  - `database/` - Data persistence layer

### Documentation
- **`docs/`** - All project documentation
  - `implementation/` - Technical implementation guides
  - `features/` - Feature-specific documentation
  - `troubleshooting/` - Troubleshooting and testing guides
  - `INDEX.md` - Documentation index

### Utilities
- **`scripts/`** - Development and maintenance scripts
- **`backups/`** - Code backups and legacy files

## 🔧 Development Guidelines

### Code Style
1. Follow Kotlin coding conventions
2. Use meaningful variable and function names
3. Add comprehensive comments for complex logic
4. Maintain consistent indentation (4 spaces)

### Architecture Principles
1. **Separation of Concerns**: Keep detection, blocking, and UI logic separate
2. **Backward Compatibility**: Ensure changes don't break existing functionality
3. **Testability**: Write code that can be easily tested
4. **Performance**: Consider accessibility service performance impact

### Adding New Features

#### Content Detection Features
1. Add detection logic to appropriate classes in `detection/`
2. Update `ContentDetectionCoordinator` if needed
3. Add corresponding tests
4. Document the feature in `docs/features/`

#### Blocking Strategies
1. Implement new strategies in `blocking/ScrollBlockingHandler`
2. Add configuration options in `AppSettings`
3. Test with target applications
4. Document implementation in `docs/implementation/`

#### UI Changes
1. Follow Material Design guidelines
2. Ensure accessibility compliance
3. Test on different screen sizes
4. Update UI documentation

## 📝 Documentation Guidelines

### When to Add Documentation
- New features or significant changes
- Implementation guides for complex logic
- Troubleshooting procedures
- API changes or new configurations

### Documentation Structure
1. **Implementation Guides** (`docs/implementation/`)
   - Technical details
   - Code examples
   - Architecture decisions

2. **Feature Documentation** (`docs/features/`)
   - User-facing features
   - Configuration options
   - Usage examples

3. **Troubleshooting** (`docs/troubleshooting/`)
   - Common issues
   - Testing procedures
   - Debugging guides

### Documentation Format
- Use clear, descriptive headings
- Include code examples where relevant
- Add screenshots for UI changes
- Update the documentation index (`docs/INDEX.md`)

## 🧪 Testing

### Required Testing
1. **Unit Tests**: For detection and blocking logic
2. **Integration Tests**: For service interactions
3. **Manual Testing**: With target applications
4. **Accessibility Testing**: Ensure service performance

### Testing Checklist
- [ ] Code compiles without warnings
- [ ] Unit tests pass
- [ ] Manual testing with Instagram, YouTube, TikTok
- [ ] No performance degradation
- [ ] Accessibility service remains responsive
- [ ] Documentation updated

## 🚀 Submission Process

### Before Submitting
1. Run all tests
2. Update relevant documentation
3. Check code style compliance
4. Test with multiple target apps

### Pull Request Guidelines
1. **Clear Description**: Explain what changes were made and why
2. **Documentation**: Include or update relevant documentation
3. **Testing**: Describe testing performed
4. **Breaking Changes**: Clearly mark any breaking changes

### Commit Message Format
```
type(scope): brief description

Detailed explanation if needed

Closes #issue-number
```

Types: `feat`, `fix`, `docs`, `style`, `refactor`, `test`, `chore`

## 🐛 Bug Reports

### Information to Include
1. **Device Information**: Android version, device model
2. **App Version**: Focus app version
3. **Target Apps**: Which apps were being used
4. **Steps to Reproduce**: Clear reproduction steps
5. **Expected vs Actual**: What should happen vs what happened
6. **Logs**: Relevant log output if available

### Bug Report Template
```markdown
**Device Information:**
- Android Version: 
- Device Model: 
- Focus App Version: 

**Target Application:**
- App Name: 
- App Version: 

**Steps to Reproduce:**
1. 
2. 
3. 

**Expected Behavior:**

**Actual Behavior:**

**Additional Context:**
```

## 📞 Getting Help

- Check existing documentation in `docs/`
- Review troubleshooting guides
- Search existing issues
- Create a new issue with detailed information

## 🎯 Areas for Contribution

### High Priority
- Performance optimizations
- New app support (Twitter, Facebook, etc.)
- Enhanced detection algorithms
- UI/UX improvements

### Medium Priority
- Additional blocking strategies
- Better error handling
- Expanded testing coverage
- Documentation improvements

### Low Priority
- Code refactoring
- Style improvements
- Additional utility scripts

Thank you for contributing to Focus App! 🙏