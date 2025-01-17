package com.otakusweeett.nobypass;

import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

public class ConfigManager {

    private final Path configFile;
    private Map<String, Object> config;

    public ConfigManager(Path dataDirectory) {
        this.configFile = dataDirectory.resolve("config.yml");
        loadConfig();
    }

    /**
     * Loads the configuration file. If it doesn't exist, it creates a default one.
     */
    public void loadConfig() {
        if (!Files.exists(configFile)) {
            saveDefaultConfigFromResource();
        }

        try (var reader = Files.newBufferedReader(configFile)) {
            Yaml yaml = new Yaml();
            config = yaml.load(reader);
        } catch (IOException e) {
            config = Map.of(); // Fallback to an empty configuration if loading fails
        }
    }

    /**
     * Gets the allowed domains from the configuration.
     *
     * @return A list of allowed domains.
     */
    public List<String> getAllowedDomains() {
        return (List<String>) config.getOrDefault("allowed-domains", List.of("example.net"));
    }

    /**
     * Checks if debug mode is enabled.
     *
     * @return True if debug mode is enabled, otherwise false.
     */
    public boolean isDebugEnabled() {
        return (boolean) config.getOrDefault("debug", false);
    }

    /**
     * Checks if the plugin is enabled.
     *
     * @return True if the plugin is enabled, otherwise false.
     */
    public boolean isEnabled() {
        return (boolean) config.getOrDefault("enabled", true);
    }

    /**
     * Gets the language setting from the configuration.
     *
     * @return The language code (e.g., "en", "es").
     */
    public String getLanguage() {
        return (String) config.getOrDefault("lang", "en");
    }

    /**
     * Gets the reserved UUIDs from the configuration.
     *
     * @return A map of usernames to their reserved UUIDs.
     */
    public Map<String, Map<String, String>> getReservedUUIDs() {
        return (Map<String, Map<String, String>>) config.getOrDefault("reserved-uuid", Map.of());
    }

    /**
     * Gets the webhook configuration from the configuration.
     *
     * @return A map containing the webhook configuration.
     */
    public Map<String, Object> getWebhookConfig() {
        return (Map<String, Object>) config.getOrDefault("webhook", Map.of());
    }

    /**
     * Checks if the webhook is enabled.
     *
     * @return True if the webhook is enabled, otherwise false.
     */
    public boolean isWebhookEnabled() {
        Map<String, Object> webhookConfig = getWebhookConfig();
        return (boolean) webhookConfig.getOrDefault("enabled", false);
    }

    /**
     * Returns the path to the configuration file.
     *
     * @return Path to config.yml.
     */
    public Path getConfigFile() {
        return configFile;
    }

    /**
     * Gets the entire configuration map.
     *
     * @return The configuration map.
     */
    public Map<String, Object> getConfig() {
        return config;
    }

    /**
     * Saves the default configuration file from the resources folder.
     */
    private void saveDefaultConfigFromResource() {
        try (InputStream resourceStream = getClass().getClassLoader().getResourceAsStream("config.yml")) {
            if (resourceStream == null) {
                return; // Silently fail if the default resource is missing
            }
            Files.createDirectories(configFile.getParent());
            Files.copy(resourceStream, configFile);
        } catch (IOException ignored) {
            // Silently fail if unable to save the default resource
        }
    }
}
