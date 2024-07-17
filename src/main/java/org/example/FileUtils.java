package org.example;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

public class FileUtils {

    public static Set<String> listFiles(String directory) throws IOException {
        Set<String> fileList = new HashSet<>();
        Path dirPath = Paths.get(directory);
        try (Stream<Path> walk = Files.walk(dirPath)) {
            walk.forEach(path -> {
                if (!Files.isDirectory(path)) {
                    fileList.add(dirPath.relativize(path).toString());
                }
            });
            return fileList;
        }
    }

    public static boolean compareFiles(String filePath1, String filePath2) throws IOException, NoSuchAlgorithmException {
        byte[] fileHash1 = getFileHash(filePath1);
        byte[] fileHash2 = getFileHash(filePath2);
        return Arrays.equals(fileHash1, fileHash2);
    }


    public static byte[] getFileHash(String filePath) throws IOException, NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        try (InputStream fis = Files.newInputStream(Paths.get(filePath))) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                digest.update(buffer, 0, bytesRead);
            }
        }
        return digest.digest();
    }

    public static void clearDirectory(String directory) throws IOException {
        Path dirPath = Paths.get(directory);
        if (Files.exists(dirPath)) {
            try (Stream<Path> walk = Files.walk(dirPath)) {
                walk.sorted(Comparator.reverseOrder())
                        .map(Path::toFile)
                        .forEach(e -> {
                            boolean delete = e.delete();
                            if (delete) {
                                System.out.println("删除文件成功：" + e.getAbsolutePath());
                            }
                        });
            }
        }
        Files.createDirectories(dirPath);
    }

    public static void copyFiles(String sourceDir, String targetDir, Set<String> files) throws IOException {
        for (String file : files) {
            copyFile(sourceDir, targetDir, file);
        }
    }

    public static void copyFile(String sourceDir, String targetDir, String file) throws IOException {
        Path sourcePath = Paths.get(sourceDir, file);
        Path targetPath = Paths.get(targetDir, file);
        Files.createDirectories(targetPath.getParent());
        Files.copy(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);
    }

    public static void deleteFile(String targetDir, String file) throws IOException {
        Path targetPath = Paths.get(targetDir, file);
        Files.deleteIfExists(targetPath);
    }

    public static void moveFileOrDirectory(String sourcePath, String targetPath) throws IOException {
        Path source = Paths.get(sourcePath);
        Path target = Paths.get(targetPath);

        // 确保目标目录存在
        if (Files.notExists(target.getParent())) {
            Files.createDirectories(target.getParent());
        }

        Files.move(source, target, StandardCopyOption.REPLACE_EXISTING);
    }

    /**
     * 格式路径
     */
    public static String processingPath(String path, String fileName) {
        if (path == null || path.isEmpty()) {
            return fileName;
        }
        path = path.replace("\\", "/");
        boolean end = path.endsWith("/");
        boolean start = fileName.startsWith("/");
        if (start && end) {
            return path + fileName.substring(1);
        } else if (start || end) {
            return path + fileName;
        } else {
            return path + "/" + fileName;
        }
    }

    public static String processingPath(String path, String... fileNames) {
        for (String fileName : fileNames) {
            path = processingPath(path, fileName);
        }
        return path;
    }
}
