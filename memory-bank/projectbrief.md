# XPrivacyLua Project Brief

## Project Overview
XPrivacyLua is a privacy manager for Android 6.0 and later, designed as the successor to XPrivacy. It addresses the fundamental issue that revoking Android permissions often causes apps to crash or malfunction. XPrivacyLua solves this by feeding apps fake data instead of real data, preserving functionality while protecting user privacy.

## Core Requirements

1. **Privacy Protection**: Protect user privacy by restricting access to sensitive data and providing fake data where appropriate.

2. **Stability**: Ensure apps continue to function properly when restrictions are applied, avoiding crashes that often occur with direct permission revocation.

3. **Extensibility**: Allow users to define custom hooks without developing a full Xposed module.

4. **Compatibility**: Support Android 6.0 Marshmallow and later versions.

5. **Ease of Use**: Provide a simple interface for managing privacy restrictions.

6. **Programmability**: Provide a REST API for programmatic control of privacy settings by other apps.

## Key Features

1. **Restriction Management**: Allow users to apply privacy restrictions to any user or system app.

2. **Fake Data Provision**: Instead of blocking access completely, provide fake or empty data to apps.

3. **Multi-user Support**: Support multiple user profiles on a device.

4. **Extensible Hook System**: Allow users to define custom hooks using Lua scripts.

5. **Runtime Updates**: Enable adding and updating hooks at runtime without requiring a device reboot.

6. **REST API**: Provide a local REST API on localhost (127.0.0.1) that allows other apps to programmatically control privacy settings, including setting fake locations for specific apps.

## Project Scope

### In Scope
- Privacy restrictions for sensitive data (location, contacts, identifiers, etc.)
- Fake data provision for API calls
- Custom hook definition system
- Basic restriction management UI
- Multi-user support
- Free and open-source core functionality
- REST API for programmatic control by other apps

### Out of Scope
- Network and storage restrictions (would cause app crashes)
- Complex UI features like templates
- On-demand restricting (stability issues)
- App-specific features
- Security-only features
- Crowd-sourced restrictions

## Project Status
The project is no longer actively supported as stated in the README, but the codebase remains available for use and study.

## License
GNU General Public License version 3