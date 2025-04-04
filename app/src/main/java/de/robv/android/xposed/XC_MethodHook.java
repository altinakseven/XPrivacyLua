package de.robv.android.xposed;

import java.lang.reflect.Member;

/**
 * Dummy implementation of XC_MethodHook for compilation purposes
 */
public class XC_MethodHook {
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
    
    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
        // Dummy implementation
    }
    
    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
        // Dummy implementation
    }
}