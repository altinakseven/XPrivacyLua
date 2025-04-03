/*
    This file is part of XPrivacyLua.

    XPrivacyLua is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    XPrivacyLua is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with XPrivacyLua.  If not, see <http://www.gnu.org/licenses/>.

    Copyright 2017-2025 Marcel Bokhorst (M66B)
 */

package eu.faircode.xlua;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import fi.iki.elonen.NanoHTTPD;

public class XRestApiService extends Service {
    private static final String TAG = "XLua.RestApi";
    
    private static final String PREF_REST_API_ENABLED = "rest_api_enabled";
    private static final String PREF_REST_API_PORT = "rest_api_port";
    private static final String PREF_REST_API_KEY = "rest_api_key";
    
    private static final int DEFAULT_PORT = 8271;
    private static final String PERMISSION_CONTROL_PRIVACY = "eu.faircode.xlua.CONTROL_PRIVACY";
    private static final String HEADER_API_KEY = "X-API-Key";
    
    private static final int NOTIFICATION_ID = 4;
    
    private RestApiServer server = null;
    private final IBinder binder = new LocalBinder();
    
    // Cache for fake locations
    private final Map<String, Location> fakeLocations = new ConcurrentHashMap<>();
    
    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "Service created");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "Service started");
        
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        boolean enabled = prefs.getBoolean(PREF_REST_API_ENABLED, false);
        
        if (enabled) {
            startServer();
            startForeground();
        } else {
            stopServer();
            stopSelf();
        }
        
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "Service destroyed");
        stopServer();
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }
    
    public class LocalBinder extends Binder {
        XRestApiService getService() {
            return XRestApiService.this;
        }
    }
    
    private void startServer() {
        if (server != null) {
            Log.i(TAG, "Server already running");
            return;
        }
        
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        int port = prefs.getInt(PREF_REST_API_PORT, DEFAULT_PORT);
        
        try {
            server = new RestApiServer(this, port);
            server.start();
            Log.i(TAG, "Server started on port " + port);
        } catch (IOException e) {
            Log.e(TAG, "Failed to start server: " + e.getMessage());
            server = null;
        }
    }
    
    private void stopServer() {
        if (server != null) {
            server.stop();
            server = null;
            Log.i(TAG, "Server stopped");
        }
    }
    
    private void startForeground() {
        // Create notification channel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    "rest_api", "REST API", NotificationManager.IMPORTANCE_LOW);
            channel.setDescription("XPrivacyLua REST API Service");
            channel.setSound(null, null);
            
            NotificationManager nm = getSystemService(NotificationManager.class);
            nm.createNotificationChannel(channel);
        }
        
        // Create notification
        Intent intent = new Intent(this, ActivityMain.class);
        PendingIntent pi = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        
        Notification notification = new NotificationCompat.Builder(this, "rest_api")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("XPrivacyLua REST API")
                .setContentText("REST API is running")
                .setContentIntent(pi)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .build();
        
        startForeground(NOTIFICATION_ID, notification);
    }
    
    public boolean isServerRunning() {
        return server != null;
    }
    
    public void setFakeLocation(String packageName, Location location) {
        if (location == null) {
            fakeLocations.remove(packageName);
        } else {
            fakeLocations.put(packageName, location);
        }
    }
    
    public Location getFakeLocation(String packageName) {
        return fakeLocations.get(packageName);
    }
    
    public static String generateApiKey() {
        SecureRandom random = new SecureRandom();
        return UUID.randomUUID().toString() + "-" + Long.toHexString(random.nextLong());
    }
    
    private class RestApiServer extends NanoHTTPD {
        private final Context context;
        
        public RestApiServer(Context context, int port) {
            super("127.0.0.1", port);
            this.context = context;
        }
        
        @Override
        public Response serve(IHTTPSession session) {
            String uri = session.getUri();
            Method method = session.getMethod();
            Map<String, String> headers = session.getHeaders();
            Map<String, String> params = session.getParms();
            
            Log.i(TAG, "Request: " + method + " " + uri);
            
            // Check API key
            String apiKey = headers.get(HEADER_API_KEY);
            if (!isValidApiKey(apiKey)) {
                return newErrorResponse(Response.Status.UNAUTHORIZED, "Invalid API key");
            }
            
            // Check permission
            String callingPackage = getCallingPackage();
            if (callingPackage != null) {
                if (context.checkCallingPermission(PERMISSION_CONTROL_PRIVACY) != PackageManager.PERMISSION_GRANTED) {
                    return newErrorResponse(Response.Status.FORBIDDEN, 
                            "Permission " + PERMISSION_CONTROL_PRIVACY + " not granted to " + callingPackage);
                }
            }
            
            try {
                if (uri.startsWith("/api/v1")) {
                    uri = uri.substring(7); // Remove /api/v1
                    
                    // Get all restrictions
                    if (uri.equals("/restrictions") && method == Method.GET) {
                        return handleGetRestrictions();
                    }
                    
                    // Get all apps
                    else if (uri.equals("/apps") && method == Method.GET) {
                        return handleGetApps();
                    }
                    
                    // Get app details
                    else if (uri.matches("/apps/[^/]+") && method == Method.GET) {
                        String packageName = uri.substring(6); // Remove /apps/
                        return handleGetApp(packageName);
                    }
                    
                    // Get app restrictions
                    else if (uri.matches("/apps/[^/]+/restrictions") && method == Method.GET) {
                        String packageName = uri.substring(6, uri.lastIndexOf("/restrictions")); // Extract package name
                        return handleGetAppRestrictions(packageName);
                    }
                    
                    // Set app restriction
                    else if (uri.matches("/apps/[^/]+/restrictions/[^/]+") && method == Method.PUT) {
                        String packageName = uri.substring(6, uri.lastIndexOf("/restrictions/")); // Extract package name
                        String restrictionId = uri.substring(uri.lastIndexOf("/") + 1); // Extract restriction ID
                        String body = getBody(session);
                        return handleSetAppRestriction(packageName, restrictionId, body);
                    }
                    
                    // Get location
                    else if (uri.matches("/apps/[^/]+/location") && method == Method.GET) {
                        String packageName = uri.substring(6, uri.lastIndexOf("/location")); // Extract package name
                        return handleGetLocation(packageName);
                    }
                    
                    // Set location
                    else if (uri.matches("/apps/[^/]+/location") && method == Method.PUT) {
                        String packageName = uri.substring(6, uri.lastIndexOf("/location")); // Extract package name
                        String body = getBody(session);
                        return handleSetLocation(packageName, body);
                    }
                    
                    // Reset location
                    else if (uri.matches("/apps/[^/]+/location") && method == Method.DELETE) {
                        String packageName = uri.substring(6, uri.lastIndexOf("/location")); // Extract package name
                        return handleResetLocation(packageName);
                    }
                }
                
                return newErrorResponse(Response.Status.NOT_FOUND, "Endpoint not found");
                
            } catch (Exception e) {
                Log.e(TAG, "Error handling request: " + e.getMessage());
                return newErrorResponse(Response.Status.INTERNAL_ERROR, "Internal server error: " + e.getMessage());
            }
        }
        
        private Response handleGetRestrictions() throws JSONException {
            JSONObject response = new JSONObject();
            JSONArray restrictions = new JSONArray();
            
            // Get all restriction groups
            String[] groupNames = context.getResources().getStringArray(R.array.restriction_groups);
            for (String groupName : groupNames) {
                int resId = context.getResources().getIdentifier("group_" + groupName, "string", context.getPackageName());
                if (resId != 0) {
                    String name = context.getString(resId);
                    
                    JSONObject restriction = new JSONObject();
                    restriction.put("id", groupName);
                    restriction.put("name", name);
                    restrictions.put(restriction);
                }
            }
            
            response.put("status", "success");
            response.put("restrictions", restrictions);
            
            return newJsonResponse(response);
        }
        
        private Response handleGetApps() throws JSONException {
            JSONObject response = new JSONObject();
            JSONArray apps = new JSONArray();
            
            // Get all apps with restrictions
            List<XApp> xApps = XProvider.getApps(context);
            for (XApp app : xApps) {
                JSONObject appJson = new JSONObject();
                appJson.put("packageName", app.packageName);
                appJson.put("appName", app.label);
                
                boolean restricted = false;
                int restrictionCount = 0;
                for (XAssignment assignment : app.assignments) {
                    if (assignment.restricted) {
                        restricted = true;
                        restrictionCount++;
                    }
                }
                
                appJson.put("restricted", restricted);
                appJson.put("restrictionCount", restrictionCount);
                apps.put(appJson);
            }
            
            response.put("status", "success");
            response.put("apps", apps);
            
            return newJsonResponse(response);
        }
        
        private Response handleGetApp(String packageName) throws JSONException {
            JSONObject response = new JSONObject();
            
            // Get app info
            try {
                PackageManager pm = context.getPackageManager();
                PackageInfo packageInfo = pm.getPackageInfo(packageName, 0);
                ApplicationInfo appInfo = packageInfo.applicationInfo;
                
                JSONObject appJson = new JSONObject();
                appJson.put("packageName", packageName);
                appJson.put("appName", pm.getApplicationLabel(appInfo).toString());
                appJson.put("versionName", packageInfo.versionName);
                appJson.put("versionCode", packageInfo.versionCode);
                appJson.put("isSystemApp", (appInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0);
                
                // Get restrictions
                XApp xApp = XProvider.getApp(context, packageName);
                if (xApp != null) {
                    boolean restricted = false;
                    int restrictionCount = 0;
                    JSONObject restrictions = new JSONObject();
                    
                    for (XAssignment assignment : xApp.assignments) {
                        String groupName = assignment.hook.getGroup();
                        restrictions.put(groupName, assignment.restricted);
                        
                        if (assignment.restricted) {
                            restricted = true;
                            restrictionCount++;
                        }
                    }
                    
                    appJson.put("restricted", restricted);
                    appJson.put("restrictionCount", restrictionCount);
                    appJson.put("restrictions", restrictions);
                }
                
                response.put("status", "success");
                response.put("app", appJson);
                
            } catch (PackageManager.NameNotFoundException e) {
                return newErrorResponse(Response.Status.NOT_FOUND, "App not found: " + packageName);
            }
            
            return newJsonResponse(response);
        }
        
        private Response handleGetAppRestrictions(String packageName) throws JSONException {
            JSONObject response = new JSONObject();
            
            try {
                // Get app restrictions
                XApp xApp = XProvider.getApp(context, packageName);
                if (xApp == null) {
                    return newErrorResponse(Response.Status.NOT_FOUND, "App not found: " + packageName);
                }
                
                JSONObject restrictions = new JSONObject();
                for (XAssignment assignment : xApp.assignments) {
                    String groupName = assignment.hook.getGroup();
                    restrictions.put(groupName, assignment.restricted);
                }
                
                response.put("status", "success");
                response.put("packageName", packageName);
                response.put("restrictions", restrictions);
                
            } catch (Exception e) {
                return newErrorResponse(Response.Status.INTERNAL_ERROR, "Error getting restrictions: " + e.getMessage());
            }
            
            return newJsonResponse(response);
        }
        
        private Response handleSetAppRestriction(String packageName, String restrictionId, String body) throws JSONException {
            JSONObject response = new JSONObject();
            
            try {
                // Parse request body
                JSONObject request = new JSONObject(body);
                boolean enabled = request.getBoolean("enabled");
                
                // Get app
                XApp xApp = XProvider.getApp(context, packageName);
                if (xApp == null) {
                    return newErrorResponse(Response.Status.NOT_FOUND, "App not found: " + packageName);
                }
                
                // Find and update restriction
                boolean found = false;
                for (XAssignment assignment : xApp.assignments) {
                    String groupName = assignment.hook.getGroup();
                    if (groupName.equals(restrictionId)) {
                        assignment.restricted = enabled;
                        found = true;
                    }
                }
                
                if (!found) {
                    return newErrorResponse(Response.Status.NOT_FOUND, "Restriction not found: " + restrictionId);
                }
                
                // Save changes
                XProvider.setApp(context, xApp);
                
                response.put("status", "success");
                response.put("packageName", packageName);
                response.put("restrictionId", restrictionId);
                response.put("enabled", enabled);
                
            } catch (JSONException e) {
                return newErrorResponse(Response.Status.BAD_REQUEST, "Invalid request body: " + e.getMessage());
            } catch (Exception e) {
                return newErrorResponse(Response.Status.INTERNAL_ERROR, "Error setting restriction: " + e.getMessage());
            }
            
            return newJsonResponse(response);
        }
        
        private Response handleGetLocation(String packageName) throws JSONException {
            JSONObject response = new JSONObject();
            
            Location location = getFakeLocation(packageName);
            if (location == null) {
                return newErrorResponse(Response.Status.NOT_FOUND, "No fake location set for: " + packageName);
            }
            
            JSONObject locationJson = new JSONObject();
            locationJson.put("latitude", location.getLatitude());
            locationJson.put("longitude", location.getLongitude());
            locationJson.put("accuracy", location.getAccuracy());
            locationJson.put("altitude", location.getAltitude());
            locationJson.put("speed", location.getSpeed());
            locationJson.put("bearing", location.getBearing());
            locationJson.put("time", location.getTime());
            
            response.put("status", "success");
            response.put("packageName", packageName);
            response.put("location", locationJson);
            
            return newJsonResponse(response);
        }
        
        private Response handleSetLocation(String packageName, String body) throws JSONException {
            JSONObject response = new JSONObject();
            
            try {
                // Parse request body
                JSONObject request = new JSONObject(body);
                
                // Create location
                Location location = new Location("api");
                location.setLatitude(request.getDouble("latitude"));
                location.setLongitude(request.getDouble("longitude"));
                
                if (request.has("accuracy")) {
                    location.setAccuracy((float) request.getDouble("accuracy"));
                }
                if (request.has("altitude")) {
                    location.setAltitude(request.getDouble("altitude"));
                }
                if (request.has("speed")) {
                    location.setSpeed((float) request.getDouble("speed"));
                }
                if (request.has("bearing")) {
                    location.setBearing((float) request.getDouble("bearing"));
                }
                if (request.has("time")) {
                    location.setTime(request.getLong("time"));
                } else {
                    location.setTime(System.currentTimeMillis());
                }
                
                // Set fake location
                setFakeLocation(packageName, location);
                
                // Ensure location restriction is enabled
                XApp xApp = XProvider.getApp(context, packageName);
                if (xApp != null) {
                    boolean updated = false;
                    for (XAssignment assignment : xApp.assignments) {
                        String groupName = assignment.hook.getGroup();
                        if (groupName.equals("get_location") && !assignment.restricted) {
                            assignment.restricted = true;
                            updated = true;
                        }
                    }
                    
                    if (updated) {
                        XProvider.setApp(context, xApp);
                    }
                }
                
                // Create response
                JSONObject locationJson = new JSONObject();
                locationJson.put("latitude", location.getLatitude());
                locationJson.put("longitude", location.getLongitude());
                locationJson.put("accuracy", location.getAccuracy());
                locationJson.put("altitude", location.getAltitude());
                locationJson.put("speed", location.getSpeed());
                locationJson.put("bearing", location.getBearing());
                locationJson.put("time", location.getTime());
                
                response.put("status", "success");
                response.put("packageName", packageName);
                response.put("location", locationJson);
                
            } catch (JSONException e) {
                return newErrorResponse(Response.Status.BAD_REQUEST, "Invalid request body: " + e.getMessage());
            } catch (Exception e) {
                return newErrorResponse(Response.Status.INTERNAL_ERROR, "Error setting location: " + e.getMessage());
            }
            
            return newJsonResponse(response);
        }
        
        private Response handleResetLocation(String packageName) throws JSONException {
            JSONObject response = new JSONObject();
            
            setFakeLocation(packageName, null);
            
            response.put("status", "success");
            response.put("packageName", packageName);
            response.put("message", "Location reset to default");
            
            return newJsonResponse(response);
        }
        
        private boolean isValidApiKey(String apiKey) {
            if (TextUtils.isEmpty(apiKey)) {
                return false;
            }
            
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            String storedKey = prefs.getString(PREF_REST_API_KEY, null);
            
            return !TextUtils.isEmpty(storedKey) && storedKey.equals(apiKey);
        }
        
        private String getCallingPackage() {
            // This is a simplified approach and may not work in all cases
            // A more robust solution would require additional permissions
            return null;
        }
        
        private String getBody(IHTTPSession session) throws IOException {
            Map<String, String> files = new HashMap<>();
            session.parseBody(files);
            return files.get("postData");
        }
        
        private Response newJsonResponse(JSONObject json) {
            return newFixedLengthResponse(Response.Status.OK, "application/json", json.toString());
        }
        
        private Response newErrorResponse(Response.Status status, String message) {
            try {
                JSONObject error = new JSONObject();
                error.put("status", "error");
                error.put("code", status.getRequestStatus());
                error.put("message", message);
                return newFixedLengthResponse(status, "application/json", error.toString());
            } catch (JSONException e) {
                return newFixedLengthResponse(status, "text/plain", message);
            }
        }
    }
}