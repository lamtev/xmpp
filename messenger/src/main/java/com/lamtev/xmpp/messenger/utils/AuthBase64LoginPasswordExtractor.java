package com.lamtev.xmpp.messenger.utils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Base64;

public class AuthBase64LoginPasswordExtractor {
    @Nullable
    public static String[] extract(@NotNull final String base64, @NotNull final Charset charset) {
        final var decodedStr = new String(Base64.getDecoder().decode(base64), charset);

        final var separatedBy0 = decodedStr.split("\0");

        if (separatedBy0.length != 3) {
            return null;
        }

        final var result = new String[2];
        System.arraycopy(separatedBy0, 1, result, 0, 2);

        return result;
    }
}
