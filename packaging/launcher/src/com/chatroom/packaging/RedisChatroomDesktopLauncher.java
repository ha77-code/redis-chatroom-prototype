package com.chatroom.packaging;

import java.awt.Desktop;
import java.awt.GraphicsEnvironment;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class RedisChatroomDesktopLauncher {

    private static final Duration PORT_WAIT_TIMEOUT = Duration.ofSeconds(20);
    private static final String DEFAULT_HOST = "127.0.0.1";
    private static final int DEFAULT_PORT = 8080;
    private static final String DEFAULT_PATH = "/";
    private static final String APP_JAR_NAME = "redis-chatroom-prototype-1.0-SNAPSHOT.jar";

    private RedisChatroomDesktopLauncher() {
    }

    public static void main(String[] args) throws Exception {
        Path launcherJar = resolveLauncherJar();
        Path appDir = launcherJar.getParent();
        Path appJar = appDir.resolve(APP_JAR_NAME);
        Path javaExecutable = resolveBundledJava();

        List<String> command = new ArrayList<>();
        command.add(javaExecutable.toString());
        command.add("-Dfile.encoding=UTF-8");
        command.add("-Dchatroom.launch.open-browser=false");
        command.add("-jar");
        command.add(appJar.toString());
        command.addAll(Arrays.asList(args));

        ProcessBuilder builder = new ProcessBuilder(command);
        builder.directory(appDir.toFile());
        builder.inheritIO();
        Process child = builder.start();

        Thread browserThread = new Thread(() -> waitForPortAndOpen(DEFAULT_HOST, DEFAULT_PORT,
                URI.create("http://" + DEFAULT_HOST + ":" + DEFAULT_PORT + DEFAULT_PATH)), "browser-opener");
        browserThread.setDaemon(true);
        browserThread.start();

        int exitCode = child.waitFor();
        System.exit(exitCode);
    }

    private static Path resolveLauncherJar() throws URISyntaxException {
        return Paths.get(RedisChatroomDesktopLauncher.class.getProtectionDomain()
                .getCodeSource()
                .getLocation()
                .toURI());
    }

    private static Path resolveBundledJava() {
        String javaHome = System.getProperty("java.home");
        boolean windows = System.getProperty("os.name", "").toLowerCase().contains("win");
        String executable = windows ? "java.exe" : "java";
        return Paths.get(javaHome, "bin", executable);
    }

    private static void waitForPortAndOpen(String host, int port, URI uri) {
        if (GraphicsEnvironment.isHeadless() || !Desktop.isDesktopSupported()) {
            return;
        }

        long deadline = System.nanoTime() + PORT_WAIT_TIMEOUT.toNanos();
        while (System.nanoTime() < deadline) {
            if (isPortOpen(host, port)) {
                try {
                    Desktop.getDesktop().browse(uri);
                } catch (IOException ignored) {
                    return;
                }
                return;
            }
            sleepQuietly(250);
        }
    }

    private static boolean isPortOpen(String host, int port) {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(host, port), 500);
            return true;
        } catch (IOException ex) {
            return false;
        }
    }

    private static void sleepQuietly(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }
}
