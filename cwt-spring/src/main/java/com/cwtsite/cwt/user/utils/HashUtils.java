package com.cwtsite.cwt.user.utils;

import org.springframework.beans.factory.annotation.Value;

import java.security.MessageDigest;

public class HashUtils {

    private static final String SALT = "3bffac5a24a2033cc4dda64af8654f40cf5c7201";

    public static String createHash(String value) {
        if (value == null) {
            return "";
        }

        value = SALT + value;
        char[] chars = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

        try {
            MessageDigest engine = MessageDigest.getInstance("SHA-1");
            byte[] result = engine.digest(value.getBytes("UTF-8"));
            StringBuilder buffer = new StringBuilder(result.length * 2);
            for (byte aData : result) {
                int value1 = (int) aData & 0xFF;
                buffer.append(chars[value1 / 16]);
                buffer.append(chars[value1 & 0x0F]);
            }
            return buffer.toString().toLowerCase();
        } catch (Exception e) {
            throw new RuntimeException("No hash implementation.", e);
        }
    }
}
