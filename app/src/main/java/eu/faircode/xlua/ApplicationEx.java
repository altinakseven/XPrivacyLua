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

    Copyright 2017-2019 Marcel Bokhorst (M66B)
 */

package eu.faircode.xlua;

import android.app.Application;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

public class ApplicationEx extends Application {
    private static final String TAG = "XLua.App";

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "Create version=" + BuildConfig.VERSION_NAME);
        
        // Initialize REST API key if not set
        initializeRestApiKey();
        
        // Start REST API service if enabled
        startRestApiServiceIfEnabled();
    }
    
    private void initializeRestApiKey() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if (!prefs.contains("rest_api_key")) {
            String apiKey = XRestApiService.generateApiKey();
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("rest_api_key", apiKey);
            editor.apply();
            Log.i(TAG, "Generated initial REST API key");
        }
    }
    
    private void startRestApiServiceIfEnabled() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        boolean enabled = prefs.getBoolean("rest_api_enabled", false);
        
        if (enabled) {
            Intent intent = new Intent(this, XRestApiService.class);
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                startForegroundService(intent);
            } else {
                startService(intent);
            }
            Log.i(TAG, "Started REST API service");
        }
    }
}