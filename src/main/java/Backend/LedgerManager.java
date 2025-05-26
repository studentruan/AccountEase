package Backend;

import org.json.JSONObject;
import org.json.JSONException;
import java.io.*;
import java.nio.file.*;
import java.util.*;

/**
        * Manages ledger IDs and their persistence in the system.
 * <p>
 * This class handles the creation, storage, and retrieval of ledger identifiers
 * in a JSON file structure. It maintains a list of all ledger IDs and provides
 * methods to add, remove, and query them.
 * </p>
 *
 * @author Shang Shi
 * @version 2.0
 * @since March 27, 2023
        */
public class LedgerManager {
    /** Directory path for storing primary ledger information */
    private static final String PRIMARY_DIR = "src/main/resources/firstlevel_json/";

    /** File path for the ledger IDs JSON file */
    private static final String PRIMARY_FILE = PRIMARY_DIR + "ledger_ids.json";

    /** List to maintain all ledger IDs in memory */
    private List<String> ledgerList = new ArrayList<>();

    // Static initializer to ensure directory exists
    static {
        try {
            Files.createDirectories(Paths.get(PRIMARY_DIR));
        } catch (IOException e) {
            System.err.println("Failed to create primary storage directory: " + e.getMessage());
        }
    }

    /**
            * Loads all ledger IDs from the persistent storage file.
     * <p>
     * Reads the JSON file containing ledger IDs and populates the internal list.
            * If the file doesn't exist or is corrupted, initializes an empty list.
            * </p>
            */
    public void loadLedgerIdsFromFile() {
        try {
            JSONObject json = readPrimaryFile();
            for (String key : json.keySet()) {
                if (!key.equals("name")) {
                    String id = json.getString(key);
                    if (!ledgerList.contains(id)) {
                        ledgerList.add(id);
                    }
                }
            }
        } catch (IOException | JSONException e) {
            System.err.println("Failed to read ledger file: " + e.getMessage());
        }
    }

    /**
            * Adds a new ledger ID to the system.
     *
             * @param id The ledger ID to add
     * @throws IOException if there's an error writing to the storage file
            */
    public void addLedger(String id) throws IOException {
        JSONObject json = readPrimaryFile();
        int index = countLedgerKeys(json);
        String key = "Ledger" + (index + 1);
        json.put(key, id);
        ledgerList.add(id);
        writePrimaryFile(json);
    }

    /**
            * Removes a ledger ID from the system.
     *
             * @param id The ledger ID to remove
     * @throws IOException if there's an error writing to the storage file
            */
    public void removeLedger(String id) throws IOException {
        JSONObject json = readPrimaryFile();
        String targetKey = null;
        for (String key : json.keySet()) {
            if (json.getString(key).equals(id)) {
                targetKey = key;
                break;
            }
        }
        if (targetKey != null) {
            json.remove(targetKey);
            ledgerList.remove(id);
            rebuildLedgerKeys(json);
        }
    }

    /**
            * Rebuilds the ledger key sequence after modifications.
     *
             * @param json The JSON object containing ledger IDs
     * @throws IOException if there's an error writing to the storage file
            */
    private void rebuildLedgerKeys(JSONObject json) throws IOException {
        JSONObject newJson = new JSONObject().put("name", "ledger_id");
        int count = 1;
        for (String id : ledgerList) {
            newJson.put("Ledger" + count, id);
            count++;
        }
        writePrimaryFile(newJson);
    }

    /**
            * Reads the primary ledger IDs file.
            *
            * @return JSONObject containing all ledger IDs
     * @throws IOException if there's an error reading the file
            */
    private JSONObject readPrimaryFile() throws IOException {
        if (!Files.exists(Paths.get(PRIMARY_FILE))) {
            return new JSONObject().put("name", "ledger_id");
        }
        return new JSONObject(new String(Files.readAllBytes(Paths.get(PRIMARY_FILE))));
    }

    /**
            * Writes to the primary ledger IDs file.
     *
             * @param json The JSON object to write
     * @throws IOException if there's an error writing the file
            */
    private void writePrimaryFile(JSONObject json) throws IOException {
        Files.write(Paths.get(PRIMARY_FILE), json.toString(4).getBytes());
    }

    /**
            * Counts the number of existing ledger entries.
     *
             * @param json The JSON object to analyze
     * @return The count of existing ledger entries
     */
    private int countLedgerKeys(JSONObject json) {
        return (int) json.keySet().stream()
                .filter(key -> key.startsWith("Ledger"))
                .count();
    }

    /**
            * Retrieves all ledger IDs currently managed by the system.
     *
             * @return List of all ledger IDs
     */
    public List<String> getAllLedgerIds() {
        return new ArrayList<>(ledgerList);
    }
}