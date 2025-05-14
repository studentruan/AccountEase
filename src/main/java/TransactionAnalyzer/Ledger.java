package BasicInformationStorageandRetrieval;

import org.json.JSONObject;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Ledger {

    // 类属性
    private String id;
    private String name;
    private String coverImagePath;
    private String description;
    private String secondaryStoragePath;
    private LocalDateTime creationTime;
    private LocalDateTime lastModifiedTime;
    private List<String> modificationHistory = new ArrayList<>();

    // 目录路径（假定为 "a"）
    private static final String DIRECTORY_PATH = "./a";

    /**
     * 整体的 getter 方法，读取 JSON 文件并将属性录入到类中
     *
     * @param id 目标 JSON 文件的账本 ID
     * @throws IOException 文件读取异常
     */
    public void loadLedger(String id) throws IOException {
        File file = new File(DIRECTORY_PATH, id + ".json");
        if (!file.exists()) {
            throw new FileNotFoundException("JSON 文件不存在: " + file.getAbsolutePath());
        }

        // 读取 JSON 文件内容
        String content = new String(Files.readAllBytes(Paths.get(file.getAbsolutePath())));
        JSONObject jsonObject = new JSONObject(content);

        // 将 JSON 内容录入到类属性中
        this.id = jsonObject.getString("账本ID");
        this.name = jsonObject.getString("账本名称");
        this.coverImagePath = jsonObject.getString("账本封面路径");
        this.description = jsonObject.getString("账本描述");
        this.secondaryStoragePath = jsonObject.getString("二级存储路径");
        this.creationTime = LocalDateTime.parse(jsonObject.getString("创建时间"));
        this.lastModifiedTime = LocalDateTime.parse(jsonObject.getString("最后修改时间"));

        // 读取版本跟踪信息
        this.modificationHistory = new ArrayList<>();
        for (Object entry : jsonObject.getJSONArray("版本跟踪信息")) {
            modificationHistory.add(entry.toString());
        }
    }

    /**
     * Getter 方法：获取 id
     *
     * @return 账本 ID
     */
    public String getId() {
        return id;
    }

    /**
     * Getter 方法：获取 name
     *
     * @return 账本名称
     */
    public String getName() {
        return name;
    }

    /**
     * Setter 方法：设置 name，并同步修改到 JSON 文件中
     *
     * @param id   目标账本 ID
     * @param name 新的账本名称
     * @throws IOException 文件写入异常
     */
    public void setName(String id, String name) throws IOException {
        this.name = name;
        updateJsonFile(id, "账本名称", name);
    }

    /**
     * Getter 方法：获取封面路径
     *
     * @return 账本封面路径
     */
    public String getCoverImagePath() {
        return coverImagePath;
    }

    /**
     * Setter 方法：设置封面路径，并同步修改到 JSON 文件中
     *
     * @param id            目标账本 ID
     * @param coverImagePath 新的封面路径
     * @throws IOException 文件写入异常
     */
    public void setCoverImagePath(String id, String coverImagePath) throws IOException {
        this.coverImagePath = coverImagePath;
        updateJsonFile(id, "账本封面路径", coverImagePath);
    }

    /**
     * Getter 方法：获取描述
     *
     * @return 账本描述
     */
    public String getDescription() {
        return description;
    }

    /**
     * Setter 方法：设置描述，并同步修改到 JSON 文件中
     *
     * @param id          目标账本 ID
     * @param description 新的描述
     * @throws IOException 文件写入异常
     */
    public void setDescription(String id, String description) throws IOException {
        this.description = description;
        updateJsonFile(id, "账本描述", description);
    }

    /**
     * Getter 方法：获取二级存储路径
     *
     * @return 二级存储路径
     */
    public String getSecondaryStoragePath() {
        return secondaryStoragePath;
    }

    /**
     * Setter 方法：设置二级存储路径，并同步修改到 JSON 文件中
     *
     * @param id                    目标账本 ID
     * @param secondaryStoragePath 新的二级存储路径
     * @throws IOException 文件写入异常
     */
    public void setSecondaryStoragePath(String id, String secondaryStoragePath) throws IOException {
        this.secondaryStoragePath = secondaryStoragePath;
        updateJsonFile(id, "二级存储路径", secondaryStoragePath);
    }

    /**
     * Getter 方法：获取创建时间
     *
     * @return 创建时间
     */
    public LocalDateTime getCreationTime() {
        return creationTime;
    }

    /**
     * Getter 方法：获取最后修改时间
     *
     * @return 最后修改时间
     */
    public LocalDateTime getLastModifiedTime() {
        return lastModifiedTime;
    }

    /**
     * Setter 方法：设置最后修改时间，并同步修改到 JSON 文件中
     *
     * @param id              目标账本 ID
     * @param lastModifiedTime 新的最后修改时间
     * @throws IOException 文件写入异常
     */
    public void setLastModifiedTime(String id, LocalDateTime lastModifiedTime) throws IOException {
        this.lastModifiedTime = lastModifiedTime;
        updateJsonFile(id, "最后修改时间", lastModifiedTime.toString());
    }

    /**
     * Getter 方法：获取版本跟踪信息
     *
     * @return 版本跟踪信息
     */
    public List<String> getModificationHistory() {
        return modificationHistory;
    }

    /**
     * Setter 方法：设置版本跟踪信息，并同步修改到 JSON 文件中
     *
     * @param id                 目标账本 ID
     * @param modificationHistory 新的版本跟踪信息
     * @throws IOException 文件写入异常
     */
    public void setModificationHistory(String id, List<String> modificationHistory) throws IOException {
        this.modificationHistory = modificationHistory;
        updateJsonFile(id, "版本跟踪信息", modificationHistory);
    }

    /**
     * 辅助方法：更新 JSON 文件中的指定字段
     *
     * @param id    目标账本 ID
     * @param key   要更新的键
     * @param value 新的值
     * @throws IOException 文件写入异常
     */
    private void updateJsonFile(String id, String key, Object value) throws IOException {
        File file = new File(DIRECTORY_PATH, id + ".json");
        if (!file.exists()) {
            throw new FileNotFoundException("JSON 文件不存在: " + file.getAbsolutePath());
        }

        // 读取现有 JSON 文件内容
        String content = new String(Files.readAllBytes(Paths.get(file.getAbsolutePath())));
        JSONObject jsonObject = new JSONObject(content);

        // 更新字段
        jsonObject.put(key, value);

        // 更新最后修改时间
        this.lastModifiedTime = LocalDateTime.now();
        jsonObject.put("最后修改时间", this.lastModifiedTime.toString());

        // 写回文件
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(jsonObject.toString(4)); // 格式化输出
        }
    }
}