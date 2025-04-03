/*
    This is an example client for the XPrivacyLua REST API.
    It demonstrates how to interact with the API from another Android app.
*/

import android.content.Context;
import android.location.Location;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class XPrivacyLuaClient {
    private static final String TAG = "XPrivacyLuaClient";
    
    private static final String BASE_URL = "http://127.0.0.1:8271/api/v1";
    private static final String HEADER_API_KEY = "X-API-Key";
    
    private final String apiKey;
    private final Executor executor = Executors.newSingleThreadExecutor();
    
    public interface ResponseCallback {
        void onSuccess(JSONObject response);
        void onError(Exception error);
    }
    
    public XPrivacyLuaClient(String apiKey) {
        this.apiKey = apiKey;
    }
    
    /**
     * Get all available restrictions
     */
    public void getRestrictions(final ResponseCallback callback) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject response = makeGetRequest("/restrictions");
                    callback.onSuccess(response);
                } catch (Exception e) {
                    callback.onError(e);
                }
            }
        });
    }
    
    /**
     * Get all apps with their restriction status
     */
    public void getApps(final ResponseCallback callback) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject response = makeGetRequest("/apps");
                    callback.onSuccess(response);
                } catch (Exception e) {
                    callback.onError(e);
                }
            }
        });
    }
    
    /**
     * Get details about a specific app
     */
    public void getApp(final String packageName, final ResponseCallback callback) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject response = makeGetRequest("/apps/" + packageName);
                    callback.onSuccess(response);
                } catch (Exception e) {
                    callback.onError(e);
                }
            }
        });
    }
    
    /**
     * Get restrictions for a specific app
     */
    public void getAppRestrictions(final String packageName, final ResponseCallback callback) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject response = makeGetRequest("/apps/" + packageName + "/restrictions");
                    callback.onSuccess(response);
                } catch (Exception e) {
                    callback.onError(e);
                }
            }
        });
    }
    
    /**
     * Set a restriction for a specific app
     */
    public void setAppRestriction(final String packageName, final String restrictionId, final boolean enabled, final ResponseCallback callback) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject body = new JSONObject();
                    body.put("enabled", enabled);
                    
                    JSONObject response = makePutRequest("/apps/" + packageName + "/restrictions/" + restrictionId, body);
                    callback.onSuccess(response);
                } catch (Exception e) {
                    callback.onError(e);
                }
            }
        });
    }
    
    /**
     * Get the fake location for a specific app
     */
    public void getLocation(final String packageName, final ResponseCallback callback) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject response = makeGetRequest("/apps/" + packageName + "/location");
                    callback.onSuccess(response);
                } catch (Exception e) {
                    callback.onError(e);
                }
            }
        });
    }
    
    /**
     * Set a fake location for a specific app
     */
    public void setLocation(final String packageName, final Location location, final ResponseCallback callback) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject body = new JSONObject();
                    body.put("latitude", location.getLatitude());
                    body.put("longitude", location.getLongitude());
                    body.put("accuracy", location.getAccuracy());
                    body.put("altitude", location.getAltitude());
                    body.put("speed", location.getSpeed());
                    body.put("bearing", location.getBearing());
                    body.put("time", location.getTime());
                    
                    JSONObject response = makePutRequest("/apps/" + packageName + "/location", body);
                    callback.onSuccess(response);
                } catch (Exception e) {
                    callback.onError(e);
                }
            }
        });
    }
    
    /**
     * Reset the fake location for a specific app
     */
    public void resetLocation(final String packageName, final ResponseCallback callback) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject response = makeDeleteRequest("/apps/" + packageName + "/location");
                    callback.onSuccess(response);
                } catch (Exception e) {
                    callback.onError(e);
                }
            }
        });
    }
    
    private JSONObject makeGetRequest(String endpoint) throws IOException, JSONException {
        URL url = new URL(BASE_URL + endpoint);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty(HEADER_API_KEY, apiKey);
        
        return readResponse(connection);
    }
    
    private JSONObject makePutRequest(String endpoint, JSONObject body) throws IOException, JSONException {
        URL url = new URL(BASE_URL + endpoint);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("PUT");
        connection.setRequestProperty(HEADER_API_KEY, apiKey);
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);
        
        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = body.toString().getBytes("utf-8");
            os.write(input, 0, input.length);
        }
        
        return readResponse(connection);
    }
    
    private JSONObject makeDeleteRequest(String endpoint) throws IOException, JSONException {
        URL url = new URL(BASE_URL + endpoint);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("DELETE");
        connection.setRequestProperty(HEADER_API_KEY, apiKey);
        
        return readResponse(connection);
    }
    
    private JSONObject readResponse(HttpURLConnection connection) throws IOException, JSONException {
        int responseCode = connection.getResponseCode();
        
        if (responseCode >= 200 && responseCode < 300) {
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            
            while ((line = in.readLine()) != null) {
                response.append(line);
            }
            in.close();
            
            return new JSONObject(response.toString());
        } else {
            BufferedReader err = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
            StringBuilder errorResponse = new StringBuilder();
            String line;
            
            while ((line = err.readLine()) != null) {
                errorResponse.append(line);
            }
            err.close();
            
            try {
                return new JSONObject(errorResponse.toString());
            } catch (JSONException e) {
                throw new IOException("HTTP Error: " + responseCode + " - " + errorResponse.toString());
            }
        }
    }
    
    /**
     * Example usage in an Android app
     */
    public static void exampleUsage(Context context) {
        // Replace with your actual API key from XPrivacyLua settings
        XPrivacyLuaClient client = new XPrivacyLuaClient("your-api-key-here");
        
        // Get all restrictions
        client.getRestrictions(new ResponseCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                Log.d(TAG, "Restrictions: " + response.toString());
            }
            
            @Override
            public void onError(Exception error) {
                Log.e(TAG, "Error getting restrictions", error);
            }
        });
        
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
    }
}