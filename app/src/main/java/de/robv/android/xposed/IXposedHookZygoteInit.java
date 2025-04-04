package de.robv.android.xposed;

/**
 * Dummy implementation of IXposedHookZygoteInit for compilation purposes
 */
public interface IXposedHookZygoteInit {
    void initZygote(StartupParam startupParam) throws Throwable;
    
    class StartupParam {
        public String modulePath;
    }
}