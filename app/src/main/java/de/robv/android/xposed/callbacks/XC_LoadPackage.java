package de.robv.android.xposed.callbacks;

/**
 * Dummy implementation of XC_LoadPackage for compilation purposes
 */
public class XC_LoadPackage {
    public static class LoadPackageParam {
        public String packageName;
        public ClassLoader classLoader;
        public boolean isFirstApplication;
        public String processName;
        public String appInfo;
    }
}