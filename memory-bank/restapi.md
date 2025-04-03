# XPrivacyLua REST API Documentation

## Overview

XPrivacyLua provides a local REST API that runs on `127.0.0.1` (localhost), allowing other apps on the device to programmatically control privacy settings. This API enables automation of privacy controls and integration with other privacy or security tools.

## Security Considerations

Since the API runs only on localhost, it can only be accessed by apps running on the same device. However, to prevent unauthorized access:

1. **API Key Authentication**: All requests require an API key that must be configured in XPrivacyLua settings.
2. **Permission Verification**: XPrivacyLua verifies that the calling app has been granted the custom permission `eu.faircode.xlua.CONTROL_PRIVACY`.
3. **Request Logging**: All API requests are logged and can be reviewed in the XPrivacyLua app.

## API Endpoints

### Base URL

```
http://127.0.0.1:8271/api/v1
```

The port (8271) can be configured in the XPrivacyLua settings.

### Authentication

All requests must include the API key in the header:

```
X-API-Key: your_api_key_here
```

### Endpoints

#### Get All Restrictions

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
      "id": "location",
      "name": "Get location",
      "description": "Fake location, hide NMEA messages"
    },
    {
      "id": "contacts",
      "name": "Get contacts",
      "description": "Hide contacts with the pro option to allow (non) starred contacts"
    },
    ...
  ]
}
```

#### Get App Restrictions

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
    "location": true,
    "contacts": false,
    "camera": true,
    ...
  }
}
```

#### Set App Restriction

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
  "restrictionId": "location",
  "enabled": true
}
```

#### Set Location

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

#### Get Location

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

#### Reset Location

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

#### Get All Apps

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

#### Get App Details

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
      "location": true,
      "contacts": false,
      ...
    }
  }
}
```

### Error Handling

All endpoints return standard HTTP status codes:
- 200: Success
- 400: Bad request (invalid parameters)
- 401: Unauthorized (invalid or missing API key)
- 403: Forbidden (missing permission)
- 404: Not found (app or restriction not found)
- 500: Internal server error

Error responses follow this format:

```json
{
  "status": "error",
  "code": 404,
  "message": "App not found: com.example.nonexistent"
}
```

## Implementation Details

The REST API is implemented using a lightweight HTTP server that runs as a service within XPrivacyLua. The service:

1. Only starts when the API feature is enabled in settings
2. Only binds to localhost (127.0.0.1)
3. Uses minimal resources
4. Automatically stops when not in use for a configurable period

## Usage Examples

### Using curl (from ADB shell)

Enable location restriction for an app:
```
curl -X PUT -H "X-API-Key: your_api_key" -d '{"enabled":true}' http://127.0.0.1:8271/api/v1/apps/com.example.app/restrictions/location
```

Set fake location:
```
curl -X PUT -H "X-API-Key: your_api_key" -d '{"latitude":37.7749,"longitude":-122.4194}' http://127.0.0.1:8271/api/v1/apps/com.example.app/location
```

### Using the API from another Android app

```java
// Example code for setting a fake location
OkHttpClient client = new OkHttpClient();

JSONObject locationData = new JSONObject();
locationData.put("latitude", 37.7749);
locationData.put("longitude", -122.4194);

Request request = new Request.Builder()
    .url("http://127.0.0.1:8271/api/v1/apps/com.example.app/location")
    .put(RequestBody.create(MediaType.parse("application/json"), locationData.toString()))
    .addHeader("X-API-Key", "your_api_key")
    .build();

client.newCall(request).enqueue(new Callback() {
    @Override
    public void onResponse(Call call, Response response) {
        // Handle response
    }

    @Override
    public void onFailure(Call call, IOException e) {
        // Handle failure
    }
});