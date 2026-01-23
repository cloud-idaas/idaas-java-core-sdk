package com.cloud_idaas.core.util;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FileUtil {

    public static String readFile(String filePath) throws IOException {
        if (Files.notExists(Paths.get(filePath))) {
            throw new IOException("File does not exist");
        }
        byte[] buffer;
        try (FileInputStream in = new FileInputStream(filePath)) {
            ByteArrayOutputStream result = new ByteArrayOutputStream();
            buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                result.write(buffer, 0, bytesRead);
            }
            return result.toString(StandardCharsets.UTF_8.name());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void writeFile(String filePath, String content) {
        if (Files.notExists(Paths.get(filePath))) {
            try {
                Files.createDirectories(Paths.get(filePath).getParent());
                Files.createFile(Paths.get(filePath));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try (FileOutputStream out = new FileOutputStream(filePath)) {
            out.write(content.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
