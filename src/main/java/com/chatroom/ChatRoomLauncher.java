package com.chatroom;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.Environment;

import java.awt.Desktop;
import java.awt.GraphicsEnvironment;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URI;
import java.time.Duration;

public class ChatRoomLauncher {

    private static final Logger log = LoggerFactory.getLogger(ChatRoomLauncher.class);
    private static final Duration PORT_WAIT_TIMEOUT = Duration.ofSeconds(20);

    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(ChatRoomApplication.class);
        application.addListeners((ApplicationListener<ApplicationReadyEvent>) ChatRoomLauncher::openBrowserWhenReady);
        application.run(args);
    }

    private static void openBrowserWhenReady(ApplicationReadyEvent event) {
        Environment environment = event.getApplicationContext().getEnvironment();
        boolean enabled = environment.getProperty("chatroom.launch.open-browser", Boolean.class, true);
        if (!enabled) {
            log.info("Browser auto-open is disabled");
            return;
        }
        if (GraphicsEnvironment.isHeadless() || !Desktop.isDesktopSupported()) {
            log.info("Desktop browsing is not supported in the current environment");
            return;
        }

        int port = environment.getProperty("local.server.port", Integer.class,
                environment.getProperty("server.port", Integer.class, 8080));
        String host = environment.getProperty("chatroom.launch.host", "127.0.0.1");
        String path = normalizePath(environment.getProperty("chatroom.launch.path", "/"));
        URI uri = URI.create(String.format("http://%s:%d%s", host, port, path));

        Thread browserThread = new Thread(() -> waitForPortAndOpen(host, port, uri), "chatroom-browser-launcher");
        browserThread.setDaemon(true);
        browserThread.start();
    }

    private static void waitForPortAndOpen(String host, int port, URI uri) {
        long deadline = System.nanoTime() + PORT_WAIT_TIMEOUT.toNanos();
        while (System.nanoTime() < deadline) {
            if (isPortOpen(host, port)) {
                try {
                    Desktop.getDesktop().browse(uri);
                    log.info("Opened browser at {}", uri);
                } catch (IOException ex) {
                    log.warn("Failed to open browser at {}: {}", uri, ex.getMessage());
                }
                return;
            }
            sleepQuietly(250);
        }
        log.warn("Timed out waiting for server port {} before opening browser", port);
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

    private static String normalizePath(String path) {
        if (path == null || path.isBlank()) {
            return "/";
        }
        return path.startsWith("/") ? path : "/" + path;
    }
}
