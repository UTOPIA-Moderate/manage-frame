package com.manage.common.util;

import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class PasswordEncoder {

    private static final String SALT_PREFIX = "manage_";
    private static final String SALT_SUFFIX = "_frame";

    public static String encode(String rawPassword) {
        String salt = SALT_PREFIX + UUID.randomUUID().toString().replace("-", "") + SALT_SUFFIX;
        String hash = DigestUtils.md5DigestAsHex((salt + rawPassword).getBytes(StandardCharsets.UTF_8));
        return salt + hash;
    }

    public static boolean matches(String rawPassword, String encodedPassword) {
        if (encodedPassword == null || encodedPassword.length() < 64) {
            return false;
        }
        String salt = encodedPassword.substring(0, 58);
        String storedHash = encodedPassword.substring(58);
        String computedHash = DigestUtils.md5DigestAsHex((salt + rawPassword).getBytes(StandardCharsets.UTF_8));
        return storedHash.equals(computedHash);
    }

    public static String encodeSimple(String rawPassword) {
        return DigestUtils.md5DigestAsHex(rawPassword.getBytes(StandardCharsets.UTF_8));
    }
}
