package com.chatroom.config;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import redis.embedded.RedisServer;

import java.io.IOException;
import java.net.ServerSocket;

@Configuration
public class EmbeddedRedisConfig {

    private static final Logger log = LoggerFactory.getLogger(EmbeddedRedisConfig.class);

    @Value("${spring.data.redis.port:6379}")
    private int redisPort;

    private RedisServer redisServer;

    @PostConstruct
    public void startEmbeddedRedis() {
        if (isPortAvailable(redisPort)) {
            try {
                redisServer = new RedisServer(redisPort);
                redisServer.start();
                log.info("Embedded Redis started on port {}", redisPort);
            } catch (IOException e) {
                log.info("Could not start embedded Redis, assuming external Redis is used: {}", e.getMessage());
            }
        } else {
            log.info("Port {} is already in use, using existing Redis instance", redisPort);
        }
    }

    @PreDestroy
    public void stopEmbeddedRedis() {
        if (redisServer != null) {
            try {
                redisServer.stop();
                log.info("Embedded Redis stopped");
            } catch (IOException e) {
                log.warn("Error stopping embedded Redis: {}", e.getMessage());
            }
        }
    }

    private boolean isPortAvailable(int port) {
        try (ServerSocket socket = new ServerSocket(port)) {
            return true;
        } catch (IOException e) {
            return false;
        }
    }
}
