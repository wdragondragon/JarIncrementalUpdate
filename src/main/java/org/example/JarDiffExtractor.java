package org.example;

import java.io.*;
import java.util.*;

public class JarDiffExtractor {
    public static void start(String basePath) throws IOException {
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

            clear(newDecompressionOutPut, oldDecompressionOutPut);
            // 解压两个JAR文件
            JarExtractor.decompression(newJarPath, newDecompressionOutPut);
            JarExtractor.decompression(oldJarPath, oldDecompressionOutPut);
            System.out.println("解压完成");

            // 获取两个目录中的文件列表
            PathDiffExtractor.start(oldDecompressionOutPut, newDecompressionOutPut, targetDir);
        }
    }

    private static void clear(String newDecompressionOutPut, String oldDecompressionOutPut) throws IOException {
        // 清空目标文件夹
        FileUtils.clearDirectory(newDecompressionOutPut);
        FileUtils.clearDirectory(oldDecompressionOutPut);
        System.out.println("清空解压文件夹完成");
    }
}
