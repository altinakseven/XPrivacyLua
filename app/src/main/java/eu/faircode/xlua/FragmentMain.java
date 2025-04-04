package eu.faircode.xlua;

import androidx.fragment.app.Fragment;

/**
 * Dummy implementation of FragmentMain for compilation purposes
 */
public class FragmentMain extends Fragment {
    public enum enumShow {
        none, user, icon, all
    }
    
    public enumShow getShow() {
        return enumShow.none;
    }
    
    public void setShow(AdapterApp.enumShow show) {
        // Dummy implementation
    }
}
