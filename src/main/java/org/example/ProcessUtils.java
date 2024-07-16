package org.example;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class ProcessUtils {

    public static Process runCommand(String workPath, String command) {
        try {
            // 构建并启动进程执行 jar 命令
            ProcessBuilder processBuilder = new ProcessBuilder(command.trim().split(" "));
            processBuilder.directory(new File(workPath));
            processBuilder.redirectErrorStream(true); // 合并标准输出和标准错误流

            // 启动进程并等待其完成
            Process process = processBuilder.start();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println(line); // 打印输出内容
                }
            }
            process.waitFor();
            return process;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
