# XPrivacyLua Product Context

## Why This Project Exists

XPrivacyLua was created to address a critical gap in Android's permission system. While Android allows users to revoke permissions from apps, doing so often causes apps to crash or malfunction because they're not designed to handle permission denial gracefully. XPrivacyLua provides an alternative approach by feeding apps fake data instead of blocking access entirely, allowing apps to function normally while protecting user privacy.

As the successor to XPrivacy (which supported Android 4.0.3 to 5.1.1), XPrivacyLua was built from the ground up to support Android 6.0 and later versions, with a focus on stability, simplicity, and extensibility.

## Problems It Solves

1. **App Crashes from Permission Denial**: By providing fake data instead of denying permissions outright, XPrivacyLua prevents the crashes that commonly occur when permissions are revoked.

2. **Privacy Leakage**: Many apps collect excessive personal data, including location, device identifiers, contacts, and more. XPrivacyLua restricts access to this sensitive information.

3. **Tracking and Profiling**: Apps often use device identifiers and other data to track users across services. XPrivacyLua can provide fake identifiers to prevent accurate tracking.

4. **Analytics and Telemetry**: XPrivacyLua can restrict access to analytics services like Google Analytics, Firebase Analytics, and Crashlytics, preventing data collection.

5. **Limited Customization**: Unlike standard permission systems, XPrivacyLua allows fine-grained control over what data apps can access and what fake data they receive.

## How It Should Work

1. **Xposed Framework Integration**: XPrivacyLua operates as an Xposed module, hooking into Android's API calls to intercept requests for sensitive data.

2. **Lua Scripting Engine**: When an app requests sensitive data, XPrivacyLua executes Lua scripts that determine whether to allow access or return fake data.

3. **Hook Definition System**: Users can define custom hooks using JSON definitions and Lua scripts, specifying which methods to intercept and how to handle them.

4. **Per-App Restrictions**: Users can apply restrictions selectively to specific apps, with different settings for each app if desired.

5. **Runtime Updates**: Hook definitions can be added or updated at runtime, requiring only that the target app be restarted (not a full device reboot).

6. **REST API**: A local REST API running on localhost (127.0.0.1) allows other apps to programmatically control XPrivacyLua's privacy settings, including setting fake locations for specific apps.

7. **Pro Features**: Additional convenience features are available through a pro companion app, including backup/restore, custom settings, and more.

## User Experience Goals

1. **Simplicity**: Provide a straightforward interface for managing privacy restrictions without overwhelming users with technical details.

2. **Stability**: Ensure that restricted apps continue to function properly, avoiding crashes and unexpected behavior.

3. **Transparency**: Make it clear when restrictions are being applied, with optional notifications for restriction events.

4. **Customization**: Allow advanced users to extend functionality through custom hook definitions.

5. **Minimal Overhead**: Minimize performance impact on the device and restricted apps.

6. **Privacy by Default**: Provide sensible default restrictions that protect privacy without requiring extensive configuration.

7. **Programmability**: Enable integration with other privacy tools through the REST API, allowing for automated and coordinated privacy protection.

## Target Users

1. **Privacy-Conscious Android Users**: People who care about their digital privacy and want more control over what data apps can access.

2. **Advanced Android Users**: Users comfortable with tools like Xposed Framework and willing to make system-level modifications.

3. **Developers**: Those interested in understanding how apps access sensitive data or in creating custom privacy restrictions.

## Limitations

1. **Requires Xposed Framework**: XPrivacyLua requires Xposed (or similar frameworks like LSPosed) to function, limiting its accessibility to users willing to install these tools.

2. **Cannot Restrict Root Apps**: Apps with root access can bypass XPrivacyLua's restrictions.

3. **Some Restrictions Not Possible**: Certain restrictions (like network access) would cause apps to crash and are therefore not implemented.

4. **Native Code Limitations**: XPrivacyLua cannot hook into native code, so some functionality (like OpenSL ES for audio recording) cannot be restricted.

## REST API Integration

The REST API feature enables XPrivacyLua to become part of a broader privacy protection ecosystem:

1. **Local-Only Access**: The API only binds to localhost (127.0.0.1), ensuring that only apps on the device can access it.

2. **Authentication**: API key authentication ensures that only authorized apps can control privacy settings.

3. **Permission Control**: A custom permission (`eu.faircode.xlua.CONTROL_PRIVACY`) provides an additional layer of security.

4. **Use Cases**:
   - Privacy automation apps can adjust settings based on context (location, time, etc.)
   - VPN apps can coordinate network privacy with app-level privacy
   - Custom privacy dashboards can provide unified interfaces
   - Location spoofing apps can set fake locations for multiple apps at once

5. **Developer Experience**: The API is designed to be simple and intuitive, with comprehensive documentation and examples.