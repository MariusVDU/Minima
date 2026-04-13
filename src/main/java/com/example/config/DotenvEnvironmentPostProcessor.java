package com.example.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.Map;

public class DotenvEnvironmentPostProcessor implements EnvironmentPostProcessor, Ordered {

    private static final String DOTENV_FILE_NAME = ".env";
    private static final String PROPERTY_SOURCE_NAME = "dotenv";

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        Path dotenvPath = Paths.get(System.getProperty("user.dir"), DOTENV_FILE_NAME);

        if (!Files.isRegularFile(dotenvPath)) {
            return;
        }

        Map<String, Object> properties = loadDotenv(dotenvPath);
        if (properties.isEmpty()) {
            return;
        }

        environment.getPropertySources().addLast(new MapPropertySource(PROPERTY_SOURCE_NAME, properties));
    }

    private Map<String, Object> loadDotenv(Path dotenvPath) {
        Map<String, Object> properties = new LinkedHashMap<>();

        try (BufferedReader reader = Files.newBufferedReader(dotenvPath, StandardCharsets.UTF_8)) {
            String line;
            while ((line = reader.readLine()) != null) {
                String trimmedLine = line.trim();
                if (trimmedLine.isEmpty() || trimmedLine.startsWith("#")) {
                    continue;
                }

                int separatorIndex = trimmedLine.indexOf('=');
                if (separatorIndex < 1) {
                    continue;
                }

                String key = trimmedLine.substring(0, separatorIndex).trim();
                String value = trimmedLine.substring(separatorIndex + 1).trim();

                if (key.startsWith("export ")) {
                    key = key.substring("export ".length()).trim();
                }

                if ((value.startsWith("\"") && value.endsWith("\"")) || (value.startsWith("'") && value.endsWith("'"))) {
                    value = value.substring(1, value.length() - 1);
                }

                if (!key.isEmpty()) {
                    properties.put(key, value);
                }
            }
        } catch (IOException exception) {
            throw new IllegalStateException("Nepavyko perskaityti .env failo", exception);
        }

        return properties;
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}