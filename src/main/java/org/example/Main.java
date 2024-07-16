package org.example;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        String mode = System.getProperty("mode");
        if ("update".equalsIgnoreCase(mode)) {
            JarDiffUpdater.start();
        } else {
            JarDiffExtractor.start();
        }
    }
}
