package com.udev.mesi.services;

import at.favre.lib.crypto.bcrypt.BCrypt;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;

public class AuthService {

    public static boolean authenticate(String password, String hash) {
        return hash.equals(hash(password));
    }

    public static String hash(String password) {
        String hash;
        hash = new String(BCrypt.with(new SecureRandom()).hash(6, password.getBytes(StandardCharsets.UTF_8)), StandardCharsets.UTF_8);
        return hash;
    }
}
