package cz.ascariaquynn.packagedelivery;

import java.lang.reflect.Method;

public class Util {

    /**
     * Returns name of executable file, or fallback when it can't be decoded.
     * @param fallback
     * @return
     */
    public static String getAppFileName(String fallback) {
        String url = Application.class.getProtectionDomain().getCodeSource().getLocation().toString();
        String[] parts = url.split("!/");
        String part = parts[0];
        parts = part.split("/");
        part = parts[parts.length - 1];
        return part.contains(".") ? part : fallback;
    }

    public static Method findMethodWithArgType(Object haystack, Object needle) {
        Method[] declaredMethods = haystack.getClass().getDeclaredMethods();
        // First try to find exact type
        for (Method declaredMethod : declaredMethods) {
            Class<?>[] parameterTypes = declaredMethod.getParameterTypes();
            if (parameterTypes[0] == needle.getClass()) {
                return declaredMethod;
            }
        }
        return null;
    }

    public static Method findMethodWithArgAssignableFrom(Object haystack, Object needle) {
        Method[] declaredMethods = haystack.getClass().getDeclaredMethods();
        // First try to find exact type
        for (Method declaredMethod : declaredMethods) {
            Class<?>[] parameterTypes = declaredMethod.getParameterTypes();
            if (parameterTypes[0].isAssignableFrom(needle.getClass())) {
                return declaredMethod;
            }
        }
        return null;
    }

    public static Method findMethod(Object haystack, Object needle) {
        Method methodWithArg = Util.findMethodWithArgType(haystack, needle);
        return null != methodWithArg ? methodWithArg : Util.findMethodWithArgAssignableFrom(haystack, needle);
    }
}
