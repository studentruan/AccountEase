package Backend;

import org.json.JSONObject;
import org.json.JSONException;
import java.io.*;
import java.nio.file.*;
import java.util.*;

public class LedgerManager {
    private static final String PRIMARY_DIR = "src/main/resources/firstlevel_json/";
    private static final String PRIMARY_FILE = PRIMARY_DIR + "ledger_ids.json";
    private List<String> ledgerList = new ArrayList<>();

    static {
        try {
            Files.createDirectories(Paths.get(PRIMARY_DIR));
        } catch (IOException e) {
            System.err.println("创建一级存储目录失败: " + e.getMessage());
        }
    }

    // 加载所有ID
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
            System.err.println("读取文件失败: " + e.getMessage());
        }
    }

    // 添加新账本ID
    public void addLedger(String id) throws IOException {
        JSONObject json = readPrimaryFile();
        int index = countLedgerKeys(json);
        String key = "账本" + (index + 1);
        json.put(key, id);
        ledgerList.add(id);
        writePrimaryFile(json);
    }

    // 删除账本
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

    // 重建键名序列
    private void rebuildLedgerKeys(JSONObject json) throws IOException {
        JSONObject newJson = new JSONObject().put("name", "ledger_id");
        int count = 1;
        for (String id : ledgerList) {
            newJson.put("账本" + count, id);
            count++;
        }
        writePrimaryFile(newJson);
    }

    // 读取一级文件
    private JSONObject readPrimaryFile() throws IOException {
        if (!Files.exists(Paths.get(PRIMARY_FILE))) {
            return new JSONObject().put("name", "ledger_id");
        }
        return new JSONObject(new String(Files.readAllBytes(Paths.get(PRIMARY_FILE))));
    }

    // 写入一级文件
    private void writePrimaryFile(JSONObject json) throws IOException {
        Files.write(Paths.get(PRIMARY_FILE), json.toString(4).getBytes());
    }

    // 统计已有账本数
    private int countLedgerKeys(JSONObject json) {
        return (int) json.keySet().stream()
                .filter(key -> key.startsWith("账本"))
                .count();
    }

    public List<String> getAllLedgerIds() {
        return new ArrayList<>(ledgerList);
    }


}