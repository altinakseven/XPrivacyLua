package de.robv.android.xposed;

import java.lang.reflect.Member;

/**
 * Dummy implementation of XposedBridge for compilation purposes
 */
public class XposedBridge {
    public static void log(String message) {
        // Dummy implementation
    }
    
    public static void log(Throwable t) {
        // Dummy implementation
    }
    
    public static void hookMethod(Member method, XC_MethodHook callback) {
        // Dummy implementation
    }
}