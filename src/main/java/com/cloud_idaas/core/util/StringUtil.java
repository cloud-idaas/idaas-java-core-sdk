package com.cloud_idaas.core.util;

import org.apache.commons.lang3.StringUtils;

public class StringUtil extends StringUtils {

    public static boolean equals(String str1, String str2) {
        return str1 == null ? str2 == null : str1.equals(str2);
    }
}
