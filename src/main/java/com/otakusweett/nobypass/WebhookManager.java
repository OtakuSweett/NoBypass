package com.otakusweeett.nobypass;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import okhttp3.*;

import java.io.IOException;
import java.util.Map;

public class WebhookManager {

    private final OkHttpClient httpClient = new OkHttpClient();
    private Map<String, Object> webhookConfig;
    private final MessageManager messageManager;

    public WebhookManager(Map<String, Object> webhookConfig, MessageManager messageManager) {
        this.webhookConfig = webhookConfig;
        this.messageManager = messageManager;
    }

    /**
     * Sends a blocked connection notification via Discord webhook.
     *
     * @param username The player's username.
     * @param domain   The domain the player used.
     * @param ip       The player's IP address.
     * @param reason   The reason for blocking the player.
     * @param skinUrl  The URL of the player's skin.
     */
    public void sendBlockedConnection(String username, String domain, String ip, String reason, String skinUrl) {
        boolean enabled = (boolean) webhookConfig.getOrDefault("enabled", false);
        String webhookUrl = (String) webhookConfig.getOrDefault("url", "");

        if (!enabled || webhookUrl.isEmpty()) {
            return; // Webhook is disabled or URL is not set, silently skip
        }

        // Retrieve and format the title and description from the message manager
        String title = messageManager.getMessage("webhook-title");
        String descriptionTemplate = messageManager.getMessage("webhook-description");
        String description = descriptionTemplate
                .replace("{username}", username)
                .replace("{domain}", domain)
                .replace("{ip}", ip)
                .replace("{reason}", reason);

        // Create the embed object
        JsonObject embed = new JsonObject();
        embed.addProperty("title", title);
        embed.addProperty("description", description);
        embed.addProperty("color", ((Number) webhookConfig.getOrDefault("color", 16711680)).intValue());

        JsonObject thumbnail = new JsonObject();
        thumbnail.addProperty("url", skinUrl);
        embed.add("thumbnail", thumbnail);

        JsonArray embeds = new JsonArray();
        embeds.add(embed);

        JsonObject payload = new JsonObject();
        payload.add("embeds", embeds);

        RequestBody body = RequestBody.create(payload.toString(), MediaType.get("application/json"));
        Request request = new Request.Builder()
                .url(webhookUrl)
                .post(body)
                .build();

        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // Optionally log or handle the failure
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                response.close();
            }
        });
    }

    /**
     * Updates the webhook configuration.
     *
     * @param newConfig The new webhook configuration.
     */
    public void updateConfig(Map<String, Object> newConfig) {
        if (newConfig != null) {
            this.webhookConfig = newConfig;
        }
    }
}
