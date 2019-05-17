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
    private static final String SPECIAL_SYMBOLS = "!@#$%^&*-_+=,.:;\"'(){}[]<>~`\\/|";
    @NotNull
    private final Random random = new SecureRandom();
    @NotNull
    private final char[] symbols;
    @NotNull
    private final char[] buf;

    public StringGenerator(final int vocabulary, final int length) {
        if (length < 1 || (vocabulary & (Mode.LETTERS | Mode.DIGITS | Mode.SPECIAL_SYMBOLS)) == 0) {
            throw new IllegalArgumentException();
        }

        var symbols = "";
        if ((vocabulary & Mode.LETTERS) != 0) {
            symbols += (UPPER + LOWER);
        }
        if ((vocabulary & Mode.DIGITS) != 0) {
            symbols += DIGITS;
        }
        if ((vocabulary & Mode.SPECIAL_SYMBOLS) != 0) {
            symbols += SPECIAL_SYMBOLS;
        }

        this.symbols = symbols.toCharArray();
        this.buf = new char[length];
    }

    @NotNull
    synchronized public String nextString() {
        for (int idx = 0; idx < buf.length; idx++) {
            buf[idx] = symbols[random.nextInt(symbols.length)];
        }

        return new String(buf);
    }

    public static final class Mode {
        public static final byte LETTERS         = 0b001;
        public static final byte DIGITS          = 0b010;
        public static final byte SPECIAL_SYMBOLS = 0b100;
    }
}
