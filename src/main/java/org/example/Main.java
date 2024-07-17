package org.example;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        String mode = System.getProperty("mode");
        String basePath = System.getProperty("basePath", "");
        if ("update".equalsIgnoreCase(mode)) {
            JarDiffUpdater.start(basePath);
        } else {
            JarDiffExtractor.start(basePath);
        }
    }
}
