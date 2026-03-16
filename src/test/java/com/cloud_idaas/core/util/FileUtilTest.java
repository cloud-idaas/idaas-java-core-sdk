package com.cloud_idaas.core.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

/**
 * FileUtil 单元测试
 */
class FileUtilTest {

    @TempDir
    Path tempDir;

    // ==================== readFile 测试 ====================

    @Test
    @DisplayName("readFile: 成功读取文件内容")
    void readFile_WithExistingFile_ShouldReturnContent() throws IOException {
        String content = "Hello, World!";
        Path filePath = tempDir.resolve("test.txt");
        Files.write(filePath, content.getBytes());

        String result = FileUtil.readFile(filePath.toString());

        assertEquals(content, result.trim());
    }

    @Test
    @DisplayName("readFile: 读取多行文件")
    void readFile_WithMultiLineFile_ShouldReturnAllContent() throws IOException {
        String content = "Line 1\nLine 2\nLine 3";
        Path filePath = tempDir.resolve("multiline.txt");
        Files.write(filePath, content.getBytes());

        String result = FileUtil.readFile(filePath.toString());

        assertTrue(result.contains("Line 1"));
        assertTrue(result.contains("Line 2"));
        assertTrue(result.contains("Line 3"));
    }

    @Test
    @DisplayName("readFile: 读取 UTF-8 编码文件")
    void readFile_WithUtf8Content_ShouldReturnCorrectContent() throws IOException {
        String content = "中文字符测试 UTF-8 内容";
        Path filePath = tempDir.resolve("utf8.txt");
        Files.write(filePath, content.getBytes(java.nio.charset.StandardCharsets.UTF_8));

        String result = FileUtil.readFile(filePath.toString());

        assertEquals(content, result.trim());
    }

    @Test
    @DisplayName("readFile: 文件不存在应抛出异常")
    void readFile_WithNonExistingFile_ShouldThrowException() {
        String nonExistingPath = tempDir.resolve("non-existing.txt").toString();

        assertThrows(IOException.class, () ->
                FileUtil.readFile(nonExistingPath)
        );
    }

    // ==================== writeFile 测试 ====================

    @Test
    @DisplayName("writeFile: 写入新文件")
    void writeFile_WithNewFile_ShouldCreateFile() {
        Path filePath = tempDir.resolve("output.txt");
        String content = "Test content";

        FileUtil.writeFile(filePath.toString(), content);

        assertTrue(Files.exists(filePath));
    }

    @Test
    @DisplayName("writeFile: 写入内容应正确保存")
    void writeFile_WithContent_ShouldSaveCorrectly() throws IOException {
        Path filePath = tempDir.resolve("content.txt");
        String content = "Hello, World!";

        FileUtil.writeFile(filePath.toString(), content);

        String readContent = new String(Files.readAllBytes(filePath), java.nio.charset.StandardCharsets.UTF_8);
        assertEquals(content, readContent);
    }

    @Test
    @DisplayName("writeFile: 覆盖现有文件")
    void writeFile_WithExistingFile_ShouldOverwrite() throws IOException {
        Path filePath = tempDir.resolve("existing.txt");
        Files.write(filePath, "Old content".getBytes());
        String newContent = "New content";

        FileUtil.writeFile(filePath.toString(), newContent);

        String readContent = new String(Files.readAllBytes(filePath), java.nio.charset.StandardCharsets.UTF_8);
        assertEquals(newContent, readContent);
    }

    @Test
    @DisplayName("writeFile: 自动创建父目录")
    void writeFile_WithNestedPath_ShouldCreateParentDirs() {
        Path nestedPath = tempDir.resolve("dir1/dir2/nested.txt");
        String content = "Nested content";

        FileUtil.writeFile(nestedPath.toString(), content);

        assertTrue(Files.exists(nestedPath));
        assertTrue(Files.exists(nestedPath.getParent()));
    }

    @Test
    @DisplayName("writeFile: 写入 UTF-8 内容")
    void writeFile_WithUtf8Content_ShouldSaveCorrectly() throws IOException {
        Path filePath = tempDir.resolve("utf8-output.txt");
        String content = "中文字符测试";

        FileUtil.writeFile(filePath.toString(), content);

        String readContent = new String(Files.readAllBytes(filePath), java.nio.charset.StandardCharsets.UTF_8);
        assertEquals(content, readContent);
    }

    @Test
    @DisplayName("writeFile: 写入空内容")
    void writeFile_WithEmptyContent_ShouldCreateEmptyFile() {
        Path filePath = tempDir.resolve("empty.txt");

        FileUtil.writeFile(filePath.toString(), "");

        assertTrue(Files.exists(filePath));
    }

    // ==================== 读写集成测试 ====================

    @Test
    @DisplayName("读写集成: 写入后读取应返回相同内容")
    void readWriteIntegration_ShouldReturnSameContent() throws IOException {
        Path filePath = tempDir.resolve("integration.txt");
        String originalContent = "Integration test content with special chars: !@#$%^&*()";

        FileUtil.writeFile(filePath.toString(), originalContent);
        String readContent = FileUtil.readFile(filePath.toString());

        assertEquals(originalContent, readContent.trim());
    }
}
