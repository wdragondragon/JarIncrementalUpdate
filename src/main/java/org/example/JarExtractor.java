package org.example;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class JarExtractor {

    public static void decompression(String jarPath, String outputPath) {
        try {
            // 创建目标目录
            Files.createDirectories(Paths.get(outputPath));
            Process process = ProcessUtils.runCommand(outputPath, "jar xvf " + jarPath);

            // 检查执行结果
            int exitCode = process.exitValue();
            if (exitCode == 0) {
                System.out.println("JAR 文件解压成功.");
            } else {
                System.err.println("JAR 文件解压失败.");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void compression(String inputPath, String outputJarPath) {
        try {
            // 创建 ProcessBuilder 实例
            Process process = ProcessUtils.runCommand(inputPath, "jar cvf0M " + outputJarPath + " .");
            int exitCode = process.exitValue();
            // 检查进程的退出码
            if (exitCode != 0) {
                throw new IOException("创建 JAR 文件失败，退出码：" + exitCode);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    private static void unzipJar(String jarPath, String outputDir) throws IOException {
        try (ZipInputStream zis = new ZipInputStream(Files.newInputStream(Paths.get(jarPath)))) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                File file = new File(outputDir, entry.getName());
                if (entry.isDirectory()) {
                    file.mkdirs();
                } else {
                    file.getParentFile().mkdirs();
                    try (FileOutputStream fos = new FileOutputStream(file)) {
                        byte[] buffer = new byte[1024];
                        int len;
                        while ((len = zis.read(buffer)) > 0) {
                            fos.write(buffer, 0, len);
                        }
                    }
                    System.out.println("解压文件：" + file.getAbsolutePath());
                }
            }
        }
    }
}
