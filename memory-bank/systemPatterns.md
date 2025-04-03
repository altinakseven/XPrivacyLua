# XPrivacyLua System Patterns

## System Architecture

XPrivacyLua follows a modular architecture centered around the Xposed framework and Lua scripting engine. The system consists of several key components working together:

1. **Xposed Module**: The core component that integrates with the Xposed framework to hook into Android API calls.

2. **Lua Script Engine**: Uses LuaJ to execute Lua scripts that define the behavior when hooks are triggered.

3. **Hook Definition System**: JSON-based definitions that specify which methods to hook and which Lua scripts to execute.

4. **REST API Service**: A lightweight HTTP server running on localhost (127.0.0.1) that provides programmatic access to XPrivacyLua's functionality.

5. **UI Layer**: Simple interface for managing restrictions and settings.

6. **Pro Companion App**: Optional component that provides additional features like backup/restore and custom settings.

## Key Technical Decisions

1. **Lua for Hook Scripts**: Using Lua as the scripting language provides flexibility and allows for runtime updates without requiring recompilation or device reboots.

2. **JSON for Hook Definitions**: Hook definitions are stored in JSON format, making them easy to create, modify, and share.

3. **Fake Data Instead of Blocking**: The fundamental design decision to provide fake data rather than blocking access entirely helps maintain app stability.

4. **Method-Level Hooking**: Hooks are defined at the method level, allowing for precise control over which API calls are intercepted.

5. **Group-Based Restriction Management**: Restrictions are organized into logical groups (like "Location" or "Contacts") for easier management.

6. **Optional Hooks**: Some hooks are marked as optional to prevent errors when hooking into methods that might not exist on all devices or Android versions.

7. **Local REST API**: Implementing a REST API that only binds to localhost for security, allowing controlled programmatic access to XPrivacyLua's functionality.

## Design Patterns

1. **Hook Pattern**: The core pattern used throughout XPrivacyLua, where method calls are intercepted and modified.

2. **Before/After Execution Pattern**: Hooks can execute code before and/or after the original method, with different behaviors for each phase.

3. **Factory Pattern**: Used for creating appropriate hook implementations based on hook definitions.

4. **Observer Pattern**: Used for notifications when restrictions are applied.

5. **Repository Pattern**: Used for storing and retrieving hook definitions and settings.

6. **API Gateway Pattern**: Used in the REST API to provide a unified interface to XPrivacyLua's functionality.

## Component Relationships

```
┌─────────────────┐     ┌─────────────────┐     ┌─────────────────┐
│  XPrivacyLua    │     │  Xposed         │     │  Android        │
│  Module         │────▶│  Framework      │────▶│  API            │
└─────────────────┘     └─────────────────┘     └─────────────────┘
        │                                               ▲
        │                                               │
        ▼                                               │
┌─────────────────┐     ┌─────────────────┐     ┌─────────────────┐
│  Hook           │     │  Lua            │     │  Target         │
│  Definitions    │────▶│  Scripts        │────▶│  Apps           │
└─────────────────┘     └─────────────────┘     └─────────────────┘

┌─────────────────┐     ┌─────────────────┐     ┌─────────────────┐
│  External       │     │  REST API       │     │  XPrivacyLua    │
│  Apps           │────▶│  Service        │────▶│  Core           │
└─────────────────┘     └─────────────────┘     └─────────────────┘
```

## Hook Execution Flow

1. An app calls an Android API method that accesses sensitive data.
2. The Xposed framework intercepts this call and redirects it to XPrivacyLua.
3. XPrivacyLua checks if there's a hook definition for this method and if restrictions are enabled for the calling app.
4. If restrictions are enabled, XPrivacyLua executes the corresponding Lua script.
5. The Lua script can:
   - Execute code before the original method (using the `before` function)
   - Modify method arguments
   - Replace the method entirely by setting a result before execution
   - Execute code after the original method (using the `after` function)
   - Modify the return value
6. The (potentially modified) result is returned to the calling app.

## Data Management

1. **Hook Definitions**: Stored as JSON files, either built-in or user-defined.
2. **Restriction Settings**: Stored in the system folder `/data/system/xlua` to persist across app updates.
3. **Custom Settings**: Additional settings that can be accessed by Lua scripts using `param:getSetting()`.

## Extension Mechanisms

1. **Custom Hook Definitions**: Users can create their own hook definitions to extend functionality.
2. **Hook Definition Repository**: Users can share and download hook definitions through the pro companion app.
3. **Settings System**: Hooks can define custom settings that users can configure.

## Error Handling

1. **Optional Hooks**: Hooks can be marked as optional to prevent errors when methods don't exist.
2. **Error Notifications**: Errors in hook definitions or Lua scripts trigger status bar notifications.
3. **Graceful Degradation**: If a hook fails, the system falls back to allowing the original method to execute normally.

## Performance Considerations

1. **Selective Hooking**: Only hooks methods that are actually restricted for at least one app.
2. **Efficient Lua Scripts**: Scripts are designed to be lightweight and efficient.
3. **Caching**: Uses caching to improve performance for frequently accessed data.

## REST API Architecture

1. **Lightweight HTTP Server**: A minimal HTTP server that only binds to localhost (127.0.0.1).

2. **JSON-based Communication**: All API requests and responses use JSON for data exchange.

3. **Authentication System**: API key-based authentication to ensure only authorized apps can access the API.

4. **Permission Verification**: Verification that calling apps have been granted the necessary custom permission.

5. **Resource-Oriented Design**: API endpoints are organized around resources (apps, restrictions, locations).

6. **Request Logging**: All API requests are logged for security and debugging purposes.

See [restapi.md](restapi.md) for detailed documentation of the REST API.