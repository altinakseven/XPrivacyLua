# XPrivacyLua REST API Documentation

XPrivacyLua now provides a local REST API that allows other apps on your device to programmatically control privacy settings. This API enables automation of privacy controls and integration with other privacy or security tools.

## Table of Contents

- [Enabling the API](#enabling-the-api)
- [Security](#security)
- [API Endpoints](#api-endpoints)
- [Client Examples](#client-examples)
- [Use Cases](#use-cases)
- [Troubleshooting](#troubleshooting)

## Enabling the API

To enable the REST API:

1. Open XPrivacyLua
2. Open the menu and select "REST API Settings"
3. Toggle "Enable REST API" to ON
4. Note the API key displayed (or generate a new one)
5. Optionally change the port number (default: 8271)

The API server will start automatically and run in the background as long as the setting is enabled.

## Security

The REST API implements several security measures:

1. **Local Only**: The API only binds to localhost (127.0.0.1), ensuring that only apps on the device can access it.

2. **API Key Authentication**: All requests require an API key that must be included in the `X-API-Key` header.

3. **Permission Verification**: Calling apps must be granted the custom permission `eu.faircode.xlua.CONTROL_PRIVACY`.

4. **Request Logging**: All API requests are logged and can be reviewed in the XPrivacyLua app.

### Adding the Permission to Your App

To use the API from your app, add the following to your AndroidManifest.xml:

```xml
<uses-permission android:name="eu.faircode.xlua.CONTROL_PRIVACY" />
```

Users will need to grant this permission to your app when it's installed.

## API Endpoints

All endpoints are relative to the base URL: `http://127.0.0.1:8271/api/v1`

### Get All Restrictions

```
GET /restrictions
```

Returns a list of all available restrictions.

**Response:**
```json
{
  "status": "success",
  "restrictions": [
    {
      "id": "get_location",
      "name": "Get location",
      "description": "Fake location, hide NMEA messages"
    },
    {
      "id": "get_contacts",
      "name": "Get contacts",
      "description": "Hide contacts with the pro option to allow (non) starred contacts"
    },
    ...
  ]
}
```

### Get All Apps

```
GET /apps
```

Returns a list of all apps with their restriction status.

**Response:**
```json
{
  "status": "success",
  "apps": [
    {
      "packageName": "com.example.app1",
      "appName": "Example App 1",
      "restricted": true,
      "restrictionCount": 5
    },
    {
      "packageName": "com.example.app2",
      "appName": "Example App 2",
      "restricted": false,
      "restrictionCount": 0
    },
    ...
  ]
}
```

### Get App Details

```
GET /apps/{packageName}
```

Returns detailed information about an app.

**Response:**
```json
{
  "status": "success",
  "app": {
    "packageName": "com.example.app",
    "appName": "Example App",
    "versionName": "1.0.0",
    "versionCode": 100,
    "isSystemApp": false,
    "restricted": true,
    "restrictionCount": 5,
    "restrictions": {
      "get_location": true,
      "get_contacts": false,
      ...
    }
  }
}
```

### Get App Restrictions

```
GET /apps/{packageName}/restrictions
```

Returns all restrictions applied to the specified app.

**Response:**
```json
{
  "status": "success",
  "packageName": "com.example.app",
  "restrictions": {
    "get_location": true,
    "get_contacts": false,
    "get_camera": true,
    ...
  }
}
```

### Set App Restriction

```
PUT /apps/{packageName}/restrictions/{restrictionId}
```

Enables or disables a specific restriction for an app.

**Request Body:**
```json
{
  "enabled": true
}
```

**Response:**
```json
{
  "status": "success",
  "packageName": "com.example.app",
  "restrictionId": "get_location",
  "enabled": true
}
```

### Get Location

```
GET /apps/{packageName}/location
```

Gets the current fake location settings for an app.

**Response:**
```json
{
  "status": "success",
  "packageName": "com.example.app",
  "location": {
    "latitude": 37.7749,
    "longitude": -122.4194,
    "accuracy": 10.0,
    "altitude": 0.0,
    "speed": 0.0,
    "bearing": 0.0,
    "time": 1617235200000
  }
}
```

### Set Location

```
PUT /apps/{packageName}/location
```

Sets a fake location for a specific app. The location restriction must be enabled for this to take effect.

**Request Body:**
```json
{
  "latitude": 37.7749,
  "longitude": -122.4194,
  "accuracy": 10.0,
  "altitude": 0.0,
  "speed": 0.0,
  "bearing": 0.0,
  "time": 1617235200000
}
```

All fields except `latitude` and `longitude` are optional.

**Response:**
```json
{
  "status": "success",
  "packageName": "com.example.app",
  "location": {
    "latitude": 37.7749,
    "longitude": -122.4194,
    "accuracy": 10.0,
    "altitude": 0.0,
    "speed": 0.0,
    "bearing": 0.0,
    "time": 1617235200000
  }
}
```

### Reset Location

```
DELETE /apps/{packageName}/location
```

Resets the fake location for an app to the global default.

**Response:**
```json
{
  "status": "success",
  "packageName": "com.example.app",
  "message": "Location reset to default"
}
```

## Client Examples

### Java/Android Client

See the [Java client example](rest_api_client.java) for a complete implementation that you can use in your Android apps.

Example usage:

```java
// Replace with your actual API key from XPrivacyLua settings
XPrivacyLuaClient client = new XPrivacyLuaClient("your-api-key-here");

// Set location restriction for an app
client.setAppRestriction("com.example.app", "get_location", true, new ResponseCallback() {
    @Override
    public void onSuccess(JSONObject response) {
        Log.d(TAG, "Location restriction set: " + response.toString());
        
        // Now set a fake location
        Location location = new Location("api");
        location.setLatitude(37.7749);
        location.setLongitude(-122.4194);
        location.setAccuracy(10);
        
        client.setLocation("com.example.app", location, new ResponseCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                Log.d(TAG, "Fake location set: " + response.toString());
            }
            
            @Override
            public void onError(Exception error) {
                Log.e(TAG, "Error setting location", error);
            }
        });
    }
    
    @Override
    public void onError(Exception error) {
        Log.e(TAG, "Error setting restriction", error);
    }
});
```

### Python Client

See the [Python client example](rest_api_client.py) for a command-line tool that you can use to interact with the API.

Example usage:

```bash
# Get all restrictions
python rest_api_client.py --api-key "your-api-key-here" get-restrictions

# Enable location restriction for an app
python rest_api_client.py --api-key "your-api-key-here" set-app-restriction com.example.app get_location --enabled

# Set fake location for an app
python rest_api_client.py --api-key "your-api-key-here" set-location com.example.app 37.7749 -122.4194 --accuracy 10
```

## Use Cases

The REST API enables several interesting use cases:

1. **Privacy Automation**: Create apps that automatically adjust privacy settings based on context (location, time, etc.)

2. **VPN Integration**: VPN apps can coordinate network privacy with app-level privacy

3. **Custom Privacy Dashboards**: Build unified interfaces that control multiple privacy tools

4. **Location Spoofing**: Location spoofing apps can set fake locations for multiple apps at once

5. **Testing**: Automate privacy testing for your own apps

6. **Bulk Operations**: Apply the same privacy settings to multiple apps at once

## Troubleshooting

### Common Issues

1. **Connection Refused**: Make sure the REST API is enabled in XPrivacyLua settings and the port number matches.

2. **Unauthorized (401)**: Check that you're using the correct API key in the `X-API-Key` header.

3. **Forbidden (403)**: Ensure your app has requested and been granted the `eu.faircode.xlua.CONTROL_PRIVACY` permission.

4. **Not Found (404)**: Verify the endpoint path and package name are correct.

### Debugging

1. Enable the REST API in XPrivacyLua settings.

2. Use a tool like `curl` to test the API directly:

```bash
curl -H "X-API-Key: your-api-key-here" http://127.0.0.1:8271/api/v1/restrictions
```

3. For testing from a computer, use ADB port forwarding:

```bash
adb forward tcp:8271 tcp:8271
```

Then you can access the API from your computer at `http://localhost:8271/api/v1/...`