package de.robv.android.xposed;

import java.lang.reflect.Member;

public class XposedDummy {
    public static class XC_MethodHook {
        public static class MethodHookParam {
            public Object thisObject;
            public Object[] args;
            public Object result;
            public Throwable throwable;
            public Member method;
            
            public void setResult(Object result) {
                this.result = result;
            }
            
            public void setThrowable(Throwable throwable) {
                this.throwable = throwable;
            }
        }
        
        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {}
        protected void afterHookedMethod(MethodHookParam param) throws Throwable {}
    }
    
    public static class XC_LoadPackage {
        public static class LoadPackageParam {
            public String packageName;
            public ClassLoader classLoader;
        }
    }
    
    public static class XSharedPreferences {
        public XSharedPreferences(String packageName) {}
        public XSharedPreferences(String packageName, String prefFileName) {}
        public boolean getBoolean(String key, boolean defaultValue) { return defaultValue; }
        public String getString(String key, String defaultValue) { return defaultValue; }
    }
}

class XposedBridge {
    public static void log(String message) {}
    public static void log(Throwable t) {}
    
    public static void hookMethod(Member method, XposedDummy.XC_MethodHook callback) {}
}

class XposedHelpers {
    public static Object callMethod(Object obj, String methodName, Object... args) { return null; }
    public static Object callStaticMethod(Class<?> clazz, String methodName, Object... args) { return null; }
    public static Object getObjectField(Object obj, String fieldName) { return null; }
    public static void setObjectField(Object obj, String fieldName, Object value) {}
    public static Class<?> findClass(String className, ClassLoader classLoader) { return null; }
}

interface IXposedHookLoadPackage {
    void handleLoadPackage(XposedDummy.XC_LoadPackage.LoadPackageParam lpparam) throws Throwable;
}

interface IXposedHookZygoteInit {
    void initZygote(StartupParam startupParam) throws Throwable;
    
    class StartupParam {
        public String modulePath;
    }
}