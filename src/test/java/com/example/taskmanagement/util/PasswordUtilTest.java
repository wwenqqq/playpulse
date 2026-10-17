package com.example.taskmanagement.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PasswordUtilTest {

    @Test
    void hashesAndVerifiesNewPasswords() {
        String encoded = PasswordUtil.encrypt("quiet-farm-42");

        assertTrue(encoded.startsWith("pbkdf2$"));
        assertTrue(PasswordUtil.verify("quiet-farm-42", encoded));
        assertFalse(PasswordUtil.verify("wrong-password", encoded));
        assertFalse(PasswordUtil.needsUpgrade(encoded));
    }

    @Test
    void verifiesLegacyMd5PasswordsForSeamlessMigration() {
        String legacy = "5f4dcc3b5aa765d61d8327deb882cf99";

        assertTrue(PasswordUtil.verify("password", legacy));
        assertTrue(PasswordUtil.needsUpgrade(legacy));
    }
}
