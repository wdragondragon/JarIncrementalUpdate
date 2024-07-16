package org.example;

import java.io.*;
import java.nio.file.*;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public class JarDiffExtractor {
    public static void start() throws IOException {
        String basePath = System.getProperty("basePath", "");

        String sourcePath = FileUtils.processingPath(basePath, "source");
        String updatePath = FileUtils.processingPath(basePath, "update");
        List<String> jarFiles = PathFinder.findJarName(sourcePath);
        System.out.println(jarFiles);
        for (String jarName : jarFiles) {
            String newJarPath = FileUtils.processingPath(sourcePath, jarName + ".jar");
            String oldJarPath = FileUtils.processingPath(sourcePath, jarName + "_old.jar");
            String newDecompressionOutPut = FileUtils.processingPath(sourcePath, jarName);
            String oldDecompressionOutPut = FileUtils.processingPath(sourcePath, jarName + "_old");

            String targetDir = FileUtils.processingPath(updatePath, jarName);
            String reportFile = FileUtils.processingPath(targetDir, "diff_report.txt");

            clear(newDecompressionOutPut, oldDecompressionOutPut, targetDir);

            // 解压两个JAR文件
            JarExtractor.decompression(newJarPath, newDecompressionOutPut);
            JarExtractor.decompression(oldJarPath, oldDecompressionOutPut);
            System.out.println("解压完成");

            // 获取两个目录中的文件列表
            Set<String> files1 = FileUtils.listFiles(newDecompressionOutPut);
            Set<String> files2 = FileUtils.listFiles(oldDecompressionOutPut);

            // 计算新增文件、变更文件和删除文件
            Set<String> addedFiles = new HashSet<>(files2);
            addedFiles.removeAll(files1);

            Set<String> removedFiles = new HashSet<>(files1);
            removedFiles.removeAll(files2);

            Set<String> modifiedFiles = new HashSet<>(files1);
            modifiedFiles.retainAll(files2);
            modifiedFiles.removeIf(file -> {
                try {
                    return FileUtils.compareFiles(FileUtils.processingPath(newDecompressionOutPut, file),
                            FileUtils.processingPath(oldDecompressionOutPut, file));
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
            writeReport(reportFile, addedFiles, modifiedFiles, removedFiles);
            System.out.println("记录文件变更内容完成");
            // 复制新增和变更的文件到目标目录
            System.out.println("写入新增文件");
            FileUtils.copyFiles(newDecompressionOutPut, targetDir, addedFiles);
            System.out.println("写入新增文件完成");

            System.out.println("写入变更文件");
            FileUtils.copyFiles(newDecompressionOutPut, targetDir, modifiedFiles);
            System.out.println("写入变更文件完成");
        }
    }

    private static void clear(String newDecompressionOutPut, String oldDecompressionOutPut, String targetDir) throws IOException {
        // 清空目标文件夹
        FileUtils.clearDirectory(newDecompressionOutPut);
        FileUtils.clearDirectory(oldDecompressionOutPut);
        FileUtils.clearDirectory(targetDir);
        System.out.println("清空文件夹完成");
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
