package com.cloud_idaas.core.util;

import com.cloud_idaas.core.exception.CredentialException;
import com.cloud_idaas.core.exception.EncodingException;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.SimpleTimeZone;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicLong;

public class RequestUtil {

    private static AtomicLong seqId = new AtomicLong(0L);

    public static String getISO8601Time(Date date) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        df.setTimeZone(new SimpleTimeZone(0, "UTC"));
        return df.format(date);
    }

    public static Date getUTCDate(String date) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        df.setTimeZone(new SimpleTimeZone(0, "UTC"));

        try {
            return df.parse(date);
        } catch (ParseException e) {
            throw new CredentialException(e.getMessage(), e);
        }
    }

    public static String getUniqueNonce() {
        long threadId = Thread.currentThread().getId();
        long currentTime = System.currentTimeMillis();
        ThreadLocalRandom random = ThreadLocalRandom.current();
        long seq = seqId.getAndIncrement();
        long rand = random.nextLong();
        StringBuffer sb = new StringBuffer();
        sb.append(System.currentTimeMillis()).append('-').append(threadId).append('-').append(currentTime).append('-').append(seq).append('-').append(rand);

        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            byte[] msg = sb.toString().getBytes();
            sb.setLength(0);

            for(byte b : digest.digest(msg)) {
                String hex = Integer.toHexString(b & 255);
                if (hex.length() < 2) {
                    sb.append(0);
                }

                sb.append(hex);
            }
        } catch (NoSuchAlgorithmException e) {
            throw new CredentialException(e.getMessage(), e);
        }

        return sb.toString();
    }

    public static String composeUrl(String endpoint, String path, Map<String, String> queries, String protocol) {
        StringBuilder urlBuilder = new StringBuilder("");
        urlBuilder.append(protocol);
        urlBuilder.append("://").append(endpoint);
        if (path != null) {
            urlBuilder.append(path);
        }
        urlBuilder.append("?");
        StringBuilder builder = new StringBuilder("");

        try {
            for(Map.Entry<String, String> entry : queries.entrySet()) {
                String key = URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8.name());
                String val = URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8.name());
                if (val != null) {
                    builder.append(key);
                    builder.append("=").append(val);
                    builder.append("&");
                }
            }
        } catch (UnsupportedEncodingException e){
            throw new EncodingException(e.getMessage());
        }

        String query = builder.toString();
        if (query.endsWith("&")) {
            query = query.substring(0, query.length() - 1);
        }

        return urlBuilder.append(query).toString();
    }
}
