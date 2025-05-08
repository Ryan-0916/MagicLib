package com.magicrealms.magiclib.common.utils;


import org.apache.commons.lang3.StringUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@SuppressWarnings("unused")
public final class Base64Util {

    private static final int BUFFER_SIZE = 8192;
    private static final String IMAGE_FORMAT = "png";
    private static final Base64.Decoder DECODER = Base64.getDecoder();
    private static final Base64.Encoder ENCODER = Base64.getEncoder();

    private Base64Util() {
        throw new AssertionError("Cannot instantiate utility class");
    }

    public static String base64ToString(String base64) {
        if (StringUtils.isBlank(base64)) {
            return null;
        }
        byte[] decodedBytes = DECODER.decode(base64);
        return new String(decodedBytes, StandardCharsets.UTF_8);
    }

    public static String stringToBase64(String text) {
        if (text == null || text.isEmpty()) {
            return null;
        }
        byte[] textBytes = text.getBytes(StandardCharsets.UTF_8);
        return ENCODER.encodeToString(textBytes);
    }

    public static String imageUrlToBase64(String url) throws IOException {
        if (StringUtils.isBlank(url)) {
            return null;
        }
        try (InputStream inputStream = new URL(url).openStream();
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[BUFFER_SIZE];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            return ENCODER.encodeToString(outputStream.toByteArray());
        }
    }

    public static BufferedImage base64ToImage(String base64) throws IOException {
        if (StringUtils.isBlank(base64)) {
            return null;
        }
        byte[] byteArray = DECODER.decode(base64);
        try (ByteArrayInputStream bai = new ByteArrayInputStream(byteArray)) {
            return ImageIO.read(bai);
        }
    }

    public static String imageToBase64(BufferedImage img) throws IOException {
        if (img == null) {
            return null;
        }
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            ImageIO.write(img, IMAGE_FORMAT, bos);
            return ENCODER.encodeToString(bos.toByteArray());
        }
    }

}
