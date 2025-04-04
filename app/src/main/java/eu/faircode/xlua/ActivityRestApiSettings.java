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

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.preference.EditTextPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.SwitchPreference;


public class ActivityRestApiSettings extends ActivityBase {
    private static final String TAG = "XLua.RestApiSettings";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.pref_rest_api_enabled);
        
        getSupportFragmentManager().beginTransaction()
                .replace(android.R.id.content, new RestApiPreferenceFragment())
                .commit();
    }
public static class RestApiPreferenceFragment extends PreferenceFragmentCompat
        implements SharedPreferences.OnSharedPreferenceChangeListener {
        
        private SwitchPreference prefEnabled;
        private EditTextPreference prefPort;
        private EditTextPreference prefApiKey;
        private Preference prefGenerateKey;
        
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.preferences_rest_api, rootKey);
            
            prefEnabled = (SwitchPreference) findPreference("rest_api_enabled");
            prefPort = (EditTextPreference) findPreference("rest_api_port");
            prefApiKey = (EditTextPreference) findPreference("rest_api_key");
            prefGenerateKey = findPreference("generate_api_key");
            
            // Set up API key generation
            prefGenerateKey.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    generateNewApiKey();
                    return true;
                }
            });
        }
        
        @Override
        public void onResume() {
            super.onResume();
            
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
            prefs.registerOnSharedPreferenceChangeListener(this);
            
            // Update summaries
            updatePortSummary(prefs);
            updateApiKeySummary(prefs);
        }
        
        @Override
        public void onPause() {
            super.onPause();
            
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
            prefs.unregisterOnSharedPreferenceChangeListener(this);
        }
        
        @Override
        public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
            if ("rest_api_enabled".equals(key)) {
                boolean enabled = prefs.getBoolean(key, false);
                
                // Start or stop the service
                Intent intent = new Intent(getActivity(), XRestApiService.class);
                if (enabled) {
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                        getActivity().startForegroundService(intent);
                    } else {
                        getActivity().startService(intent);
                    }
                } else {
                    getActivity().stopService(intent);
                }
            } else if ("rest_api_port".equals(key)) {
                updatePortSummary(prefs);
                
                // Restart service if enabled
                if (prefs.getBoolean("rest_api_enabled", false)) {
                    restartService();
                }
            } else if ("rest_api_key".equals(key)) {
                updateApiKeySummary(prefs);
            }
        }
        
        private void updatePortSummary(SharedPreferences prefs) {
            String port = prefs.getString("rest_api_port", "8271");
            prefPort.setSummary("Port: " + port);
        }
        
        private void updateApiKeySummary(SharedPreferences prefs) {
            String apiKey = prefs.getString("rest_api_key", "");
            if (apiKey.length() > 8) {
                // Show only first 8 characters for security
                prefApiKey.setSummary(apiKey.substring(0, 8) + "...");
            } else {
                prefApiKey.setSummary("Not set");
            }
        }
        
        private void generateNewApiKey() {
            String newKey = XRestApiService.generateApiKey();
            
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("rest_api_key", newKey);
            editor.apply();
            
            updateApiKeySummary(prefs);
            
            Toast.makeText(getActivity(), R.string.msg_rest_api_key_generated, Toast.LENGTH_SHORT).show();
        }
        
        private void restartService() {
            Intent intent = new Intent(getActivity(), XRestApiService.class);
            getActivity().stopService(intent);
            
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                getActivity().startForegroundService(intent);
            } else {
                getActivity().startService(intent);
            }
        }
    }
}