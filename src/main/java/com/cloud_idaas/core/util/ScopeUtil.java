package com.cloud_idaas.core.util;

import com.cloud_idaas.core.domain.constants.ErrorCode;
import com.cloud_idaas.core.exception.ConfigException;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ScopeUtil {

    private static final Pattern SCOPE_PATTERN = Pattern.compile("^[^|]+\\|[^|]+$");

    public static void validateScope(String scope){
        List<String> scopes = splitScope(scope);
        if (scopes.isEmpty()) {
            throw new ConfigException(ErrorCode.INVALID_SCOPE.getCode(), "Scope is empty");
        }
        Set<String> audiences = new HashSet<>();
        for (String scopeItem : scopes) {
            if (!isValidScope(scopeItem)) {
                throw new ConfigException(ErrorCode.INVALID_SCOPE.getCode(), String.format("Invalid scope: %s", scopeItem));
            }
            String audience = scopeItem.split("\\|")[0];
            audiences.add(audience);
        }
        if (audiences.size() > 1) {
            throw new ConfigException(ErrorCode.MULTIPLE_AUDIENCE_NOT_SUPPORTED.getCode(), "Multiple audiences are not supported");
        }
    }

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
        if (StringUtil.isBlank(scope)) {
            return false;
        }
        return SCOPE_PATTERN.matcher(scope).matches();
    }
}
