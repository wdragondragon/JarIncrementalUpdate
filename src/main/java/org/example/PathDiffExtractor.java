package org.example;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.security.NoSuchAlgorithmException;
import java.util.HashSet;
import java.util.Set;

public class PathDiffExtractor {
    public static void start(String sourceDir, String targetDir, String updateDir) throws IOException {
        FileUtils.clearDirectory(updateDir);
        // 获取两个目录中的文件列表
        Set<String> files1 = FileUtils.listFiles(sourceDir);
        Set<String> files2 = FileUtils.listFiles(targetDir);

        // 计算新增文件、变更文件和删除文件
        Set<String> addedFiles = new HashSet<>(files2);
        addedFiles.removeAll(files1);

        Set<String> removedFiles = new HashSet<>(files1);
        removedFiles.removeAll(files2);

        Set<String> modifiedFiles = new HashSet<>(files1);
        modifiedFiles.retainAll(files2);
        modifiedFiles.removeIf(file -> {
            try {
                return FileUtils.compareFiles(FileUtils.processingPath(targetDir, file),
                        FileUtils.processingPath(sourceDir, file));
            } catch (IOException | NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            }
        });
        System.out.println("文件数量:" + files1.size() + ":" + files2.size());

        // 输出结果
        System.out.println("新增文件:" + addedFiles.size());
        addedFiles.forEach(System.out::println);

        System.out.println("删除文件:" + removedFiles.size());
        removedFiles.forEach(System.out::println);

        System.out.println("变更文件:" + modifiedFiles.size());
        modifiedFiles.forEach(System.out::println);

        // 记录到txt文件
        String reportFile = FileUtils.processingPath(updateDir, "diff_report.txt");
        writeReport(reportFile, addedFiles, modifiedFiles, removedFiles);
        System.out.println("记录文件变更内容完成");
        // 复制新增和变更的文件到目标目录
        System.out.println("写入新增文件");
        FileUtils.copyFiles(targetDir, updateDir, addedFiles);
        System.out.println("写入新增文件完成");

        System.out.println("写入变更文件");
        FileUtils.copyFiles(targetDir, updateDir, modifiedFiles);
        System.out.println("写入变更文件完成");
    }

    private static void writeReport(String reportFile, Set<String> addedFiles, Set<String> modifiedFiles, Set<String> removedFiles) throws IOException {
        Path reportPath = Paths.get(reportFile);
        Files.createDirectories(reportPath.getParent());
        try (BufferedWriter writer = Files.newBufferedWriter(reportPath, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
            writer.write("新增文件:\n");
            for (String file : addedFiles) {
                writer.write(file + "\n");
            }

            writer.write("变更文件:\n");
            for (String file : modifiedFiles) {
                writer.write(file + "\n");
            }

            writer.write("删除文件:\n");
            for (String file : removedFiles) {
                writer.write(file + "\n");
            }
        }
    }
}
