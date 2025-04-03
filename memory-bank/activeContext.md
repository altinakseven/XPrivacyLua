# XPrivacyLua Active Context

## Current Work Focus

While the README notes that **"This project is not supported anymore"**, we are currently implementing a new feature to enhance its functionality:

1. **REST API Implementation**: Developing a local REST API that runs on localhost (127.0.0.1) to allow other apps on the device to programmatically control XPrivacyLua's privacy settings, including the ability to set fake locations for specific apps.

Other focus areas include:

1. **Understanding the Existing Codebase**: Gaining a comprehensive understanding of how XPrivacyLua works, its architecture, and its limitations.

2. **Documentation**: Creating and maintaining this Memory Bank to document the project thoroughly for future reference.

3. **Potential Fork Considerations**: Evaluating whether to fork the project to continue development or to create a new project based on similar principles.

## Recent Changes

Since the project is no longer supported, there are no recent changes to the codebase. The last updates would have been:

1. **Compatibility Updates**: The last updates likely focused on ensuring compatibility with newer Android versions (up to Android 12 as mentioned in the README).

2. **LSPosed Support**: Adding support for LSPosed as an alternative to EdXposed for Android 8-12.

## Next Steps

We are actively working on:

1. **REST API Implementation**: Completing and testing the local REST API feature that will allow programmatic control of XPrivacyLua. See [restapi.md](restapi.md) for detailed documentation.

2. **API Security**: Ensuring the REST API is secure and can only be accessed by authorized apps.

3. **API Documentation**: Creating comprehensive documentation for developers who want to integrate with XPrivacyLua.

Additional steps if work were to continue on this project:

1. **Android Compatibility**: Update the module to support newer Android versions (13+).

2. **Modern Framework Support**: Ensure compatibility with the latest versions of Xposed alternatives like LSPosed.

3. **New Hook Definitions**: Develop new hook definitions for newer Android APIs and popular apps.

4. **Performance Optimization**: Improve the performance of the Lua script execution and hook system.

5. **UI Modernization**: Update the user interface to follow modern Android design guidelines.

6. **Enhanced Pro Features**: Develop additional pro features that could provide value to users.

7. **API Extensions**: Expand the REST API with additional endpoints and functionality based on user feedback.

## Active Decisions and Considerations

### Technical Decisions

1. **Lua vs. Other Scripting Languages**: Continue using Lua or consider alternatives like JavaScript or Kotlin scripting for hook definitions.

2. **Hook Definition Format**: Maintain the current JSON format or consider alternatives that might be more user-friendly or powerful.

3. **Xposed Alternatives**: Evaluate newer Xposed alternatives and their compatibility with the module.

### User Experience Decisions

1. **Simplicity vs. Power**: Balance maintaining the simple interface while potentially adding more advanced features.

2. **Default Restrictions**: Determine which restrictions should be enabled by default for new installations.

3. **Notification System**: Consider improvements to the notification system for restriction events.

### Project Management Decisions

1. **Open Source Collaboration**: Establish guidelines for contributions if the project were to be revived.

2. **Version Strategy**: Define a versioning strategy for potential future releases.

3. **Documentation**: Improve documentation for both users and developers.

## Current Challenges

1. **Android Evolution**: Android continues to evolve, potentially breaking existing hooks or requiring new ones.

2. **Xposed Framework Future**: The future of Xposed and its alternatives is uncertain, which could impact the project's viability.

3. **Privacy Landscape**: The privacy landscape on Android is changing, with Google adding more built-in privacy features that might overlap with XPrivacyLua's functionality.

4. **Maintenance Burden**: Without active support, users may encounter issues that won't be fixed.

## Immediate Focus Areas

If work were to resume on this project, the immediate focus areas should be:

1. **Compatibility Testing**: Test with the latest Android versions and Xposed alternatives.

2. **Bug Fixes**: Address any known issues, particularly the ones mentioned in the README.

3. **Documentation Update**: Update documentation to reflect the current state of the project and Android ecosystem.

4. **Community Engagement**: Re-engage with the community to understand current needs and priorities.

## Long-term Vision

A long-term vision for XPrivacyLua or a successor project could include:

1. **Comprehensive Privacy Solution**: Expand beyond the current restrictions to provide a more comprehensive privacy solution.

2. **Integration with Other Privacy Tools**: Integrate with other privacy tools like firewalls, VPNs, etc.

3. **User Education**: Include features that educate users about privacy risks and best practices.

4. **Privacy Profiles**: Allow users to create and share privacy profiles for different use cases.

5. **API Ecosystem**: Foster an ecosystem of apps that leverage XPrivacyLua's REST API to provide enhanced privacy controls and automation.