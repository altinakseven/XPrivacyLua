package eu.faircode.xlua;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Dummy implementation of ActivityMain for compilation purposes
 */
public class ActivityMain extends AppCompatActivity {
    public static final String EXTRA_SEARCH_PACKAGE = "package";
    
    private void menuHelp() {
        startActivity(new Intent(this, ActivityHelp.class));
    }
    
    private void menuRestApi() {
        startActivity(new Intent(this, ActivityRestApiSettings.class));
    }
}
