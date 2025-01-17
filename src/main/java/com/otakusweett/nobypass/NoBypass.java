package com.otakusweeett.nobypass;

import com.google.inject.Inject;
import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.LoginEvent;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

import org.bstats.charts.AdvancedPie;
import org.bstats.charts.MultiLineChart;
import org.bstats.charts.SimplePie;
import org.bstats.charts.SingleLineChart;
import org.bstats.velocity.Metrics;
import org.slf4j.Logger;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Plugin(
        id = "nobypass",
        name = "NoBypass",
        version = "1.0",
        description = "Blocks unauthorized connections by validating allowed domains and reserved UUIDs.",
        authors = {"OtakuSweeett"}
)
public class NoBypass {

    private static final String PERMISSION_NOTIFY = "nobypass.notify";

    private final Logger logger;
    private final ConfigManager configManager;
    private final MessageManager messageManager;
    private final WebhookManager webhookManager;
    private final ReservedUUIDManager reservedUUIDManager;
    private final ProxyServer proxyServer;
    private final Metrics.Factory metricsFactory;

    @Inject
    public NoBypass(Logger logger, ProxyServer proxyServer, CommandManager commandManager, @DataDirectory Path dataDirectory, Metrics.Factory metricsFactory) {
        this.logger = logger;
        this.proxyServer = proxyServer;
        this.metricsFactory = metricsFactory;

        // Load configuration and messages
        this.configManager = new ConfigManager(dataDirectory);
        this.messageManager = new MessageManager(dataDirectory, configManager.getLanguage());

        // Initialize ReservedUUIDManager
        this.reservedUUIDManager = new ReservedUUIDManager(messageManager);
        this.reservedUUIDManager.loadReservedUUIDs(configManager.getReservedUUIDs());

        // Initialize WebhookManager
        this.webhookManager = new WebhookManager(configManager.getWebhookConfig(), messageManager);

        // Register /nobypass reload command
        commandManager.register(
                commandManager.metaBuilder("nobypass").build(),
                new ReloadCommand(configManager, messageManager, webhookManager, reservedUUIDManager, logger)
        );

        logger.info("NoBypass plugin loaded successfully!");
    }

@Subscribe
public void onProxyInitialization(ProxyInitializeEvent event) {
    try {
        int pluginId = 24487; // Replace with your actual plugin ID

        // Initialize bStats Metrics
        Metrics metrics = metricsFactory.make(this, pluginId);

        // SingleLineChart: Number of online players
        metrics.addCustomChart(new SingleLineChart("online_players", () -> proxyServer.getAllPlayers().size()));

        // SingleLineChart: Number of registered servers
        metrics.addCustomChart(new SingleLineChart("registered_servers", () -> proxyServer.getAllServers().size()));



        // SimplePie: Server version
        metrics.addCustomChart(new SimplePie("server_version", () -> proxyServer.getVersion().getName()));




    } catch (IllegalArgumentException e) {
        logger.error("Error initializing bStats Metrics: Invalid plugin ID or instance.", e);
    } catch (Exception e) {
        logger.error("Error initializing bStats Metrics: Unexpected error occurred.", e);
    }
}

    

    @Subscribe
    public void onPlayerLogin(LoginEvent event) {
        if (!configManager.isEnabled()) {
            return;
        }

        Player player = event.getPlayer();
        String virtualHost = player.getVirtualHost()
                                   .map(host -> host.getHostName().toLowerCase())
                                   .orElse(null);

        // Debug message
        if (configManager.isDebugEnabled() && virtualHost != null) {
            logDebug(player, virtualHost);
        }

        // Validate domain and reserved UUID
        validatePlayer(player, virtualHost, event);
    }

    private void validatePlayer(Player player, String domain, LoginEvent event) {
        boolean isDomainValid = domain != null && getAllowedDomains().contains(domain.toLowerCase());
        boolean isUUIDValid = reservedUUIDManager.isUUIDReserved(player.getUsername(), player.getUniqueId().toString());

        if (isDomainValid && isUUIDValid) {
            return; // Player is valid
        }

        // Determine the reason for denial
        String reasonKey = !isDomainValid ? "reason-invalid-domain" : "reason-uuid-mismatch";
        String kickMessageKey = !isDomainValid ? "kick-message" : "kick-reserved-uuid";

        String reason = messageManager.getMessage(reasonKey);
        Component kickMessage = deserializeMessage(kickMessageKey, "reason", reason);

        event.setResult(LoginEvent.ComponentResult.denied(kickMessage));

        // Notify players with permission
        Component alertMessage = deserializeMessage(
                "alert-message",
                "username", player.getUsername(),
                "domain", domain == null ? "unknown" : domain,
                "reason", reason
        );
        proxyServer.getAllPlayers().stream()
                .filter(p -> p.hasPermission(PERMISSION_NOTIFY))
                .forEach(p -> p.sendMessage(alertMessage));

        // Log the block
        logger.info("Connection blocked for player '{}' (UUID: {}) using '{}'. Reason: {}",
                player.getUsername(),
                player.getUniqueId(),
                domain == null ? "unknown" : domain,
                reason
        );

        // Send webhook notification if enabled
        if (configManager.getWebhookConfig().getOrDefault("enabled", false).equals(true)) {
            String skinUrl = "https://minotar.net/avatar/" + player.getUniqueId();
            webhookManager.sendBlockedConnection(
                    player.getUsername(),
                    domain == null ? "unknown" : domain,
                    player.getRemoteAddress().getAddress().getHostAddress(),
                    reason,
                    skinUrl
            );
        }
    }

    private void logDebug(Player player, String domain) {
        Component debugMessage = deserializeMessage("debug-message", "username", player.getUsername(), "domain", domain == null ? "unknown" : domain);
        logger.info(debugMessage.toString());
    }

    private Set<String> getAllowedDomains() {
        return new HashSet<>(configManager.getAllowedDomains());
    }

    private Component deserializeMessage(String key, String... placeholders) {
        String message = messageManager.getMessage(key, placeholders);
        return LegacyComponentSerializer.legacyAmpersand().deserialize(message);
    }
}
