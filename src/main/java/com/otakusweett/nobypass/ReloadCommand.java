package com.otakusweeett.nobypass;

import com.velocitypowered.api.command.SimpleCommand;
import net.kyori.adventure.text.Component;
import org.slf4j.Logger;

import java.util.Map;

public class ReloadCommand implements SimpleCommand {

    private final ConfigManager configManager;
    private final MessageManager messageManager;
    private final WebhookManager webhookManager;
    private final ReservedUUIDManager reservedUUIDManager;
    private final Logger logger;

    public ReloadCommand(ConfigManager configManager, MessageManager messageManager, WebhookManager webhookManager, ReservedUUIDManager reservedUUIDManager, Logger logger) {
        this.configManager = configManager;
        this.messageManager = messageManager;
        this.webhookManager = webhookManager;
        this.reservedUUIDManager = reservedUUIDManager;
        this.logger = logger;
    }

    @Override
    public void execute(Invocation invocation) {
        if (!invocation.source().hasPermission("nobypass.reload")) {
            invocation.source().sendMessage(Component.text("You don't have permission to execute this command!"));
            return;
        }

        // Recargar configuración principal
        configManager.loadConfig();

        // Recargar mensajes con el idioma configurado
        String lang = configManager.getLanguage();
        messageManager.loadMessages(lang, configManager.getConfigFile().getParent());

        // Actualizar configuración del webhook
        Map<String, Object> webhookSettings = configManager.getWebhookConfig();
        webhookManager.updateConfig(webhookSettings);

        // Recargar UUIDs reservados
        reservedUUIDManager.loadReservedUUIDs(configManager.getReservedUUIDs());

        // Notificar al administrador que la configuración se recargó
        invocation.source().sendMessage(Component.text(messageManager.getMessage("reload-success")));
        logger.info("Configuration, messages, webhook settings, and reserved UUIDs reloaded successfully.");
    }
}
