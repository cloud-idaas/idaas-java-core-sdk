package com.cloud_idaas.core.util;

public class ExceptionAnalyzer {
    private static final int MAX_CAUSE_DEPTH = 5;
    public static boolean isTargetCauseExist(Throwable throwable, Class<?> causeClazz, String subMessage) {
        int currentDepth = 0;
        Throwable cause = throwable;
        while (cause != null) {
            if (causeClazz.isInstance(cause)) {
                if (cause.getMessage() != null && cause.getMessage().contains(subMessage)) {
                    return true;
                }
            }

            cause = cause.getCause();
            currentDepth ++;
            if (currentDepth >= MAX_CAUSE_DEPTH) {
                break;
            }
        }
        return false;
    }

    public static boolean isTargetCauseExist(Throwable throwable, String subMessage) {
        return throwable.getMessage().contains(subMessage);
    }
}
