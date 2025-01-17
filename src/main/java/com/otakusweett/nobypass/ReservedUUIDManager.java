package com.otakusweeett.nobypass;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ReservedUUIDManager {

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
        if (reservedUUIDsFromConfig != null) {
            reservedUUIDsFromConfig.forEach((username, details) -> {
                String uuid = details.get("uuid");
                if (uuid != null && !uuid.isEmpty()) {
                    reservedUUIDs.put(username.toLowerCase(), uuid);
                }
            });
        }
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
            return false;
        }
        String reservedUUID = reservedUUIDs.get(username.toLowerCase());
        return uuid.equalsIgnoreCase(reservedUUID);
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
