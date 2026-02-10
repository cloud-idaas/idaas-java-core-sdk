package com.cloud_idaas.core.util;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ScopeUtil {

    private static final Pattern SCOPE_PATTERN = Pattern.compile("^[^|]+\\|[^|]+$");

    public static List<String> splitScope(String scope) {
        return StringUtils.isEmpty(scope)
                ? new ArrayList<>()
                : Arrays.stream(scope.split("\\s"))
                        .map(StringUtils::trim)
                        .filter(StringUtils::isNotEmpty)
                        .sorted()
                        .collect(Collectors.toList());
    }

    public static boolean isValidScope(String scope) {
        return SCOPE_PATTERN.matcher(scope).matches();
    }
}
