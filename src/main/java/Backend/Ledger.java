package Backend;

import org.json.JSONObject;
import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Ledger {
    private static final String SECONDARY_DIR = "src/main/resources/secondlevel_json/";
    private String id;
    private String name;
    private File coverImage;
    private String description;
    private LocalDateTime creationTime;
    private LocalDateTime lastModifiedTime;
    private List<String> modificationHistory = new ArrayList<>();

    static {
        try {
            Files.createDirectories(Paths.get(SECONDARY_DIR));
        } catch (IOException e) {
            System.err.println("创建二级存储目录失败: " + e.getMessage());
        }
    }

    // 保存到二级存储
    public void save() throws IOException {
        JSONObject json = new JSONObject();
        json.put("账本ID", this.id);
        json.put("账本名称", this.name);
        json.put("账本封面路径", saveCoverImage());
        json.put("账本描述", this.description);
        json.put("创建时间", LocalDateTime.now().toString());
        json.put("最后修改时间", LocalDateTime.now().toString());
        json.put("版本跟踪信息", this.modificationHistory);

        Path path = Paths.get(SECONDARY_DIR, this.id + ".json");
        Files.write(path, json.toString(4).getBytes());
    }

    // 加载账本
    public void load(String id) throws IOException {
        Path path = Paths.get(SECONDARY_DIR, id + ".json");
        String content = new String(Files.readAllBytes(path));
        JSONObject json = new JSONObject(content);

        this.id = json.getString("账本ID");
        this.name = json.getString("账本名称");
        this.coverImage = new File(json.getString("账本封面路径"));
        this.description = json.getString("账本描述");
        this.creationTime = LocalDateTime.parse(json.getString("创建时间"));
        this.lastModifiedTime = LocalDateTime.parse(json.getString("最后修改时间"));
        this.modificationHistory = json.getJSONArray("版本跟踪信息").toList().stream()
                .map(Object::toString)
                .toList();
    }

    // 处理封面图片存储
    private String saveCoverImage() throws IOException {
        if (this.coverImage == null) return "";

        Path imageDir = Paths.get(SECONDARY_DIR, "images");
        Files.createDirectories(imageDir);

        String fileName = UUID.randomUUID() + getFileExtension(coverImage.getName());
        Path target = imageDir.resolve(fileName);
        Files.copy(coverImage.toPath(), target, StandardCopyOption.REPLACE_EXISTING);
        return target.toString();
    }

    private String getFileExtension(String fileName) {
        int dotIndex = fileName.lastIndexOf('.');
        return (dotIndex == -1) ? "" : fileName.substring(dotIndex);
    }

    // Getter/Setter
    public void setId(String id) { this.id = id; }
    public String getId() { return id; }
    public void setName(String name) { this.name = name; }
    public String getName() { return name; }
    public void setCoverImage(File image) { this.coverImage = image; }
    public File getCoverImage() { return coverImage; }
    public void setDescription(String desc) { this.description = desc; }
    public String getDescription() { return description; }
    public LocalDateTime getCreationTime() { return creationTime; }
    public void setCreationTime(LocalDateTime creationTime) {
        this.creationTime = creationTime;
    }
}