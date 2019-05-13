package com.lamtev.xmpp.messenger.utils;

import org.jetbrains.annotations.NotNull;

import java.security.SecureRandom;
import java.util.Random;

public final class StringGenerator {
    @NotNull
    private static final String UPPER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    @NotNull
    private static final String LOWER = UPPER.toLowerCase();
    @NotNull
    private static final String DIGITS = "0123456789";
    @NotNull
    private static final String SYMBOLS = "!@#$%^&*-_+=,.:;\"'(){}[]<>~`\\/|";
    @NotNull
    private final char[] symbols = (UPPER + LOWER + UPPER + LOWER + DIGITS + SYMBOLS).toCharArray();
    @NotNull
    private final Random random = new SecureRandom();
    @NotNull
    private final char[] buf;

    public StringGenerator(final int length) {
        if (length < 1) {
            throw new IllegalArgumentException();
        }

        buf = new char[length];
    }

    @NotNull
    synchronized public String nextString() {
        for (int idx = 0; idx < buf.length; idx++) {
            buf[idx] = symbols[random.nextInt(symbols.length)];
        }

        return new String(buf);
    }
}
