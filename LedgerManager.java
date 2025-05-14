package BasicInformationStorageandRetrieval;

import org.json.JSONObject;
import org.json.JSONException;
import java.io.*;
import java.nio.file.*;
import java.util.*;

public class LedgerManager {

    private static final String FILE_NAME = "ledger_id.json";
    private List<String> ledgerList = new ArrayList<>();

    // 方法一：读取全部ID
    public void loadLedgerIdsFromFile() {
        try {
            JSONObject json = readJsonFromFile();
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

    // 方法二：添加一个或多个ID
    public void addLedgerIds(String... ids) {
        try {
            JSONObject json = readJsonFromFile();
            int index = countLedgerKeys(json);

            for (String id : ids) {
                if (!ledgerList.contains(id)) {
                    String key = "账本" + (index + 1);
                    json.put(key, id);
                    ledgerList.add(id);
                    index++;
                }
            }

            writeJsonToFile(json);
        } catch (IOException | JSONException e) {
            System.err.println("添加ID失败: " + e.getMessage());
        }
    }

    // 方法三：删除键或值
    public void removeLedgerEntry(String input) {
        try {
            JSONObject json = readJsonFromFile();
            String targetKey = null;

            for (String key : json.keySet()) {
                if (key.equals("name")) continue;
                if (key.equals(input) || json.getString(key).equals(input)) {
                    targetKey = key;
                    break;
                }
            }

            if (targetKey != null) {
                String removedId = json.remove(targetKey).toString();
                ledgerList.remove(removedId);

                // 重新命名键
                JSONObject newJson = new JSONObject();
                newJson.put("name", "ledger_id");

                int count = 1;
                for (String key : json.keySet()) {
                    if (!key.equals("name")) {
                        newJson.put("账本" + count, json.getString(key));
                        count++;
                    }
                }

                writeJsonToFile(newJson);
            }
        } catch (IOException | JSONException e) {
            System.err.println("删除失败: " + e.getMessage());
        }
    }

    // 方法四：通过键查ID
    public String getIdByKey(String key) {
        try {
            JSONObject json = readJsonFromFile();
            return json.optString(key, null);
        } catch (IOException e) {
            System.err.println("读取ID失败: " + e.getMessage());
            return null;
        }
    }

    // 方法五：通过ID查键
    public String getKeyById(String id) {
        try {
            JSONObject json = readJsonFromFile();
            for (String key : json.keySet()) {
                if (!key.equals("name") && id.equals(json.getString(key))) {
                    return key;
                }
            }
        } catch (IOException | JSONException e) {
            System.err.println("查找失败: " + e.getMessage());
        }
        return null;
    }

    // 方法六：替换ID
    public void replaceLedgerId(String oldId, String newId) {
        try {
            JSONObject json = readJsonFromFile();
            String key = getKeyById(oldId);

            if (key != null) {
                if (!ledgerList.contains(newId) && !json.toString().contains(newId)) {
                    json.put(key, newId);
                    ledgerList.remove(oldId);
                    ledgerList.add(newId);
                } else {
                    json.remove(key);
                    ledgerList.remove(oldId);
                }
                writeJsonToFile(json);
            }
        } catch (IOException | JSONException e) {
            System.err.println("替换ID失败: " + e.getMessage());
        }
    }



    // 工具方法：读取文件
    private JSONObject readJsonFromFile() throws IOException {
        File file = new File(FILE_NAME);
        if (!file.exists()) {
            JSONObject newJson = new JSONObject();
            newJson.put("name", "ledger_id");
            writeJsonToFile(newJson);
            return newJson;
        }

        String content = new String(Files.readAllBytes(file.toPath()));
        return new JSONObject(content);
    }

    // 工具方法：写入文件
    private void writeJsonToFile(JSONObject json) throws IOException {
        Files.write(Paths.get(FILE_NAME), json.toString(4).getBytes());
    }

    // 工具方法：统计账本数量
    private int countLedgerKeys(JSONObject json) {
        int count = 0;
        for (String key : json.keySet()) {
            if (key.startsWith("账本")) {
                count++;
            }
        }
        return count;
    }

    // 工具方法：打印所有ID
    public void printLedgerList() {
        System.out.println("当前ID列表: " + ledgerList);
    }
}
