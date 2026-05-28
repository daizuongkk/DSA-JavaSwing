package util;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public final class EncodingUtils {
    private EncodingUtils() {
    }

    public static String toBase64(byte[] data) {
        return Base64.getMimeEncoder(76, "\n".getBytes(StandardCharsets.UTF_8)).encodeToString(data);
    }

    public static byte[] fromBase64(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("Dữ liệu Base64 đang trống.");
        }
        return Base64.getMimeDecoder().decode(value.replaceAll("\\s+", ""));
    }

    public static String toHex(byte[] data) {
        StringBuilder builder = new StringBuilder(data.length * 2);
        for (byte value : data) {
            builder.append(String.format("%02x", value));
        }
        return builder.toString();
    }
}
