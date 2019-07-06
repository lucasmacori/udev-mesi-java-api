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

    private static String generate(int size) {
        StringBuilder salt = new StringBuilder();

        for (int i = 0; i < size; i++) {
            char letter;
            int min = 90;
            int max = 122;
            boolean upper = (int) (Math.random() * 1 + 0) == 1;

            // Si la lettre est en majuscule
            if (upper) {
                min = 65;
                max = 90;
            }

            // Génération de la lettre
            letter = (char) ((int) (Math.random() * max + min));

            // Ajout de la lettre au sel
            salt.append(letter);
        }

        return salt.toString();
    }
}
