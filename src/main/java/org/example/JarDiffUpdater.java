package org.example;

import java.io.*;
import java.util.*;

public class JarDiffUpdater {

    public static void start() throws IOException {
        String basePath = System.getProperty("basePath", "");
        String updatePath = FileUtils.processingPath(basePath, "update");
        String targetPath = FileUtils.processingPath(basePath, "target");
        List<String> updateNameList = PathFinder.findUpdateName(updatePath);
        System.out.println(updateNameList);
        for (String updateName : updateNameList) {
            System.out.println("更新 " + updateName);
            String reportFile = FileUtils.processingPath(updatePath, updateName, "diff_report.txt");
            String sourceDir = FileUtils.processingPath(updatePath, updateName);
            String targetDir = FileUtils.processingPath(targetPath, updateName);

            // 读取差异报告
            DiffReport diffReport = readDiffReport(reportFile);

            // 更新目标环境文件夹
            updateTarget(diffReport, sourceDir, targetDir);

            // 生成jar在target目录
            JarExtractor.compression(targetDir, FileUtils.processingPath(targetPath, updateName + ".jar"));

            // 输出更新结果
            System.out.println("完成 " + updateName);
        }
    }


    private static DiffReport readDiffReport(String reportFile) throws IOException {
        DiffReport diffReport = new DiffReport();
        try (BufferedReader reader = new BufferedReader(new FileReader(reportFile))) {
            String line;
            String section = "";
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("新增文件:")) {
                    section = "added";
                } else if (line.startsWith("变更文件:")) {
                    section = "modified";
                } else if (line.startsWith("删除文件:")) {
                    section = "removed";
                } else if (!line.trim().isEmpty()) {
                    switch (section) {
                        case "added":
                            diffReport.addedFiles.add(line.trim());
                            break;
                        case "modified":
                            diffReport.modifiedFiles.add(line.trim());
                            break;
                        case "removed":
                            diffReport.removedFiles.add(line.trim());
                            break;
                    }
                }
            }
        }
        return diffReport;
    }

    private static void updateTarget(DiffReport diffReport, String sourceDir, String targetDir) throws IOException {
        // 处理新增和变更的文件
        for (String file : diffReport.addedFiles) {
            System.out.println("处理新增文件：" + file);
            FileUtils.copyFile(sourceDir, targetDir, file);
        }
        for (String file : diffReport.modifiedFiles) {
            System.out.println("处理变更文件：" + file);
            FileUtils.copyFile(sourceDir, targetDir, file);
        }

        // 处理删除的文件
        for (String file : diffReport.removedFiles) {
            System.out.println("处理删除文件：" + file);
            FileUtils.deleteFile(targetDir, file);
        }
    }

    private static class DiffReport {
        Set<String> addedFiles = new HashSet<>();
        Set<String> modifiedFiles = new HashSet<>();
        Set<String> removedFiles = new HashSet<>();
    }
}
