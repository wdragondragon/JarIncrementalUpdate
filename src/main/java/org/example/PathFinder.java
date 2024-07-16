package org.example;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class PathFinder {
    public static List<String> findJarName(String directoryPath) {
        List<String> jarFiles = new ArrayList<>();

        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(Paths.get(directoryPath), "*.jar")) {
            for (Path path : directoryStream) {
                if (Files.isRegularFile(path)) {
                    String fileName = path.getFileName().toString();
                    if (!fileName.endsWith("_old.jar")) { // 排除以 "_old.jar" 结尾的文件
                        jarFiles.add(path.getFileName().toString().replace(".jar", ""));
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return jarFiles;
    }

    public static List<String> findUpdateName(String directoryPath) {
        List<String> directories = new ArrayList<>();

        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(Paths.get(directoryPath))) {
            for (Path path : directoryStream) {
                if (Files.isDirectory(path)) {
                    directories.add(path.getFileName().toString());
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return directories;
    }
}
