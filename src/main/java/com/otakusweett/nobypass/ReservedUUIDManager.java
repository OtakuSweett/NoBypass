package com.otakusweeett.nobypass;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ReservedUUIDManager {

    private static final Logger LOGGER = Logger.getLogger(ReservedUUIDManager.class.getName());

    private final MessageManager messageManager;
    private final Map<String, String> reservedUUIDs = new HashMap<>();

    public ReservedUUIDManager(MessageManager messageManager) {
        this.messageManager = messageManager;
    }

    /**
     * Loads the reserved UUIDs from a configuration map.
     *
     * @param reservedUUIDsFromConfig The map of reserved UUIDs from the configuration.
     */
    public void loadReservedUUIDs(Map<String, Map<String, String>> reservedUUIDsFromConfig) {
        reservedUUIDs.clear();
        if (reservedUUIDsFromConfig == null || reservedUUIDsFromConfig.isEmpty()) {
            LOGGER.warning("No reserved UUIDs found in configuration.");
            return;
        }

        Map<String, String> seenUUIDs = new HashMap<>();
        reservedUUIDsFromConfig.forEach((username, details) -> {
            if (username == null || username.isEmpty()) {
                LOGGER.warning("Encountered an entry with an empty or null username. Skipping.");
                return;
            }

            if (details == null) {
                LOGGER.warning("No details provided for username: " + username + ". Skipping.");
                return;
            }

            String uuid = details.get("uuid");
            if (uuid == null || uuid.isEmpty()) {
                LOGGER.warning("No UUID found for username: " + username + ". Skipping.");
                return;
            }

            if (seenUUIDs.containsKey(uuid.toLowerCase())) {
                LOGGER.warning("Duplicate UUID detected for username: " + username + ". Ignoring duplicate entry.");
                return;
            }

            reservedUUIDs.put(username.toLowerCase(), uuid);
            seenUUIDs.put(uuid.toLowerCase(), username);
        });

        LOGGER.info("Successfully loaded " + reservedUUIDs.size() + " reserved UUIDs.");
    }

    /**
     * Checks if a UUID is reserved for a specific username.
     *
     * @param username The username to check.
     * @param uuid     The UUID to validate.
     * @return True if the UUID matches the reserved UUID for the username, false otherwise.
     */
    public boolean isUUIDReserved(String username, String uuid) {
        if (username == null || uuid == null) {
            LOGGER.warning("Attempted to check a null username or UUID.");
            return false;
        }
        String reservedUUID = reservedUUIDs.get(username.toLowerCase());
        boolean result = uuid.equalsIgnoreCase(reservedUUID);
        if (!result) {
            LOGGER.fine("UUID mismatch for username: " + username + ". Expected: " + reservedUUID + ", Got: " + uuid);
        }
        return result;
    }

    /**
     * Retrieves an unmodifiable view of the reserved UUIDs.
     *
     * @return An unmodifiable map of reserved UUIDs.
     */
    public Map<String, String> getReservedUUIDs() {
        return Collections.unmodifiableMap(reservedUUIDs);
    }
}
