# XPrivacyLua Technical Context

## Technologies Used

### Core Technologies

1. **Java**: The primary programming language used for the Android application.

2. **Lua**: Used for scripting the behavior of hooks. XPrivacyLua uses the LuaJ implementation of Lua for Java.

3. **JSON**: Used for hook definitions and API responses, allowing for structured data that can be easily parsed and modified.

4. **Xposed Framework**: The foundation that allows XPrivacyLua to hook into Android API methods.

5. **REST API**: A lightweight HTTP server implementation that provides programmatic access to XPrivacyLua's functionality.

5. **Android SDK**: XPrivacyLua targets Android 6.0 (API level 23) and higher.

### Libraries and Dependencies

1. **LuaJ**: A Java implementation of Lua used for executing hook scripts.
   - Version: As referenced in the README, "LuaJ. Copyright 2007-2013 LuaJ. All rights reserved."

2. **Glide**: Used for image loading and caching.
   - Version: As referenced in the README, "Glide. Copyright 2014 Google, Inc. All rights reserved."

3. **Android Support Library**: Provides backward compatibility for newer Android features.
   - Version: As referenced in the README, "Android Support Library. Copyright (C) 2011 The Android Open Source Project."

4. **NanoHTTPD**: A lightweight HTTP server library used for implementing the REST API.
   - Version: Latest compatible version

## Development Setup

### Requirements

1. **Android Studio**: The official IDE for Android development.

2. **JDK**: Java Development Kit for compiling Java code.

3. **Android SDK**: With API level 23 (Android 6.0) or higher.

4. **Xposed Framework**: For testing the module during development.

### Build Process

The project uses Gradle for build automation. The main build configuration is defined in `build.gradle` files at the project and app module levels.

```
XPrivacyLua/
├── build.gradle          # Project-level build file
├── app/
│   ├── build.gradle      # App-level build file
│   └── ...
└── ...
```

Building XPrivacyLua from source code is straightforward with Android Studio, as mentioned in the README.

## Technical Constraints

### Platform Constraints

1. **Android Version**: XPrivacyLua only supports Android 6.0 Marshmallow and later. For Android 4.0.3 KitKat to Android 5.1.1 Lollipop, users must use the original XPrivacy (which is no longer supported).

2. **Device Type**: XPrivacyLua is supported only for smartphones and tablets, not for emulators.

3. **Xposed Framework**: XPrivacyLua requires the Xposed Framework (or alternatives like LSPosed) to function.
   - Android 6-7: Requires Xposed
   - Android 8-12: Requires LSPosed or EdXposed

### Technical Limitations

1. **Native Code**: Xposed cannot hook into native code, so some functionality (like OpenSL ES for audio recording) cannot be restricted.

2. **System Apps**: Persistent system apps require a reboot after hook definition changes, unlike regular apps.

3. **Root Apps**: Apps with root access can bypass XPrivacyLua's restrictions.

4. **Obfuscated Code**: The tracking restrictions only work if the code of the target app was not obfuscated.

5. **Known Issues**: Some specific hooks, like ActivityRecognitionResult.extractResult, are known to fail with certain apps (Google Maps, Netflix) for unknown reasons.

## Project Structure

```
XPrivacyLua/
├── app/                  # Main application module
│   ├── src/              # Source code
│   │   ├── main/
│   │   │   ├── assets/   # Contains hook definitions and Lua scripts
│   │   │   ├── java/     # Java source code
│   │   │   └── res/      # Android resources
│   │   └── ...
│   └── ...
├── examples/             # Example hook definitions
├── repo/                 # Hook definition repository
├── tools/                # Development tools
└── ...
```

### Key Files and Directories

1. **app/src/main/assets/hooks.json**: Contains the built-in hook definitions.

2. **app/src/main/assets/*.lua**: Lua script files for hooks.

3. **examples/**: Contains example hook definitions for reference.

4. **repo/**: Contains hook definitions that can be downloaded through the pro companion app.

## Data Storage

1. **System Folder**: All XPrivacyLua data is stored in `/data/system/xlua`, making it inaccessible to regular backup apps.

2. **Pro Companion App**: Provides backup and restore functionality for all restrictions and settings.

## API Integration

XPrivacyLua hooks into various Android APIs to provide privacy protection:

1. **Location APIs**: For faking location data.

2. **Contacts Provider**: For hiding contacts.

3. **Telephony APIs**: For faking device identifiers like IMEI.

4. **Sensor APIs**: For hiding sensor data.

5. **Package Manager**: For hiding installed apps.

6. **And many more**: As detailed in the restrictions list in the README.

## Deployment

XPrivacyLua is distributed through:

1. **Xposed Repository**: The official supported version.

2. **GitHub Releases**: Through the Xposed-Modules-Repo.

Installation requires:

1. Installing Magisk
2. Installing and activating Xposed/LSPosed/EdXposed
3. Installing and activating XPrivacyLua

## Internationalization

XPrivacyLua supports multiple languages through translations:

1. **Crowdin**: In-app texts can be translated through Crowdin.

2. **Pro Version**: Pro version strings are available for translation separately.

## REST API

XPrivacyLua provides a REST API that allows other apps to programmatically control privacy settings:

1. **API Endpoint**: The API is accessible only on localhost (127.0.0.1) on port 8271 (configurable).

2. **Authentication**: All API requests require an API key that must be configured in XPrivacyLua settings.

3. **Permission Model**: Apps must be granted the custom permission `eu.faircode.xlua.CONTROL_PRIVACY` to use the API.

4. **Data Format**: All API requests and responses use JSON format.

5. **Key Functionality**:
   - Get/set app restrictions
   - Set fake location for specific apps
   - Query app status and available restrictions
   - Reset settings to defaults

6. **Security Considerations**:
   - API only accessible from the device itself (localhost)
   - API key authentication
   - Permission verification
   - Request logging

See [restapi.md](restapi.md) for detailed documentation of the REST API.