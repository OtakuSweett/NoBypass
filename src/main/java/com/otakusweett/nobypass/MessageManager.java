package com.otakusweeett.nobypass;

import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class MessageManager {

    private final Map<String, String> messages = new HashMap<>();

    public MessageManager(Path dataDirectory, String lang) {
        loadMessages(lang, dataDirectory);
    }

    /**
     * Carga el archivo de mensajes para el idioma especificado.
     * Si el archivo no existe, lo crea desde el recurso predeterminado.
     *
     * @param lang El código de idioma (por ejemplo, "en", "es", "fr").
     * @param dataDirectory El directorio donde se almacenan los archivos de configuración.
     */
    public void loadMessages(String lang, Path dataDirectory) {
        Path messageFile = dataDirectory.resolve("messages_" + lang + ".yml");

        if (!Files.exists(messageFile)) {
            saveDefaultMessagesFromResource(lang, messageFile);
        }

        try (var reader = Files.newBufferedReader(messageFile)) {
            Yaml yaml = new Yaml();
            Map<String, String> loadedMessages = yaml.load(reader);
            if (loadedMessages != null) {
                messages.putAll(loadedMessages);
            }
        } catch (IOException e) {
            // Manejar la excepción o registrar el error
        }
    }

    /**
     * Recupera un mensaje por su clave y reemplaza los marcadores de posición.
     *
     * @param key La clave del mensaje.
     * @param placeholders Pares clave-valor para reemplazar en el mensaje.
     * @return El mensaje formateado.
     */
    public String getMessage(String key, String... placeholders) {
        String message = messages.getOrDefault(key, key);
        for (int i = 0; i < placeholders.length - 1; i += 2) {
            message = message.replace("{" + placeholders[i] + "}", placeholders[i + 1]);
        }
        return message;
    }

    /**
     * Guarda el archivo de mensajes predeterminado desde los recursos del complemento.
     *
     * @param lang El código de idioma.
     * @param messageFile La ruta al archivo de mensajes.
     */
    private void saveDefaultMessagesFromResource(String lang, Path messageFile) {
        try (InputStream resourceStream = getClass().getClassLoader().getResourceAsStream("messages_" + lang + ".yml")) {
            if (resourceStream == null) {
                return; // Fallar silenciosamente si el recurso predeterminado no existe
            }
            Files.createDirectories(messageFile.getParent());
            Files.copy(resourceStream, messageFile);
        } catch (IOException ignored) {
            // Fallar silenciosamente si no se puede guardar el recurso predeterminado
        }
    }
}
