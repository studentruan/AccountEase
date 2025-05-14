package BasicInformationStorageandRetrieval;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class dataLoader {

    public static void main(String[] args) {
        // 假设账本ID为 "ledger123"
        String ledgerId = "ledger123";
        String directoryPath = "./"; // 当前目录，可根据需要修改路径

        // 调用方法检测或创建文件
        try {
            checkAndCreateJsonFile(directoryPath, ledgerId);
        } catch (IOException e) {
            System.err.println("操作失败: " + e.getMessage());
        }
    }

    /**
     * 检测是否存在名为账本ID的 JSON 文件，如果不存在则创建文件并写入初始内容
     *
     * @param directoryPath 文件所在目录路径
     * @param ledgerId      账本ID（文件名）
     * @throws IOException 如果文件操作失败
     */
    public static void checkAndCreateJsonFile(String directoryPath, String ledgerId) throws IOException {
        // 构造文件路径
        String fileName = ledgerId + ".json";
        File jsonFile = new File(directoryPath, fileName);

        // 检查文件是否存在
        if (jsonFile.exists()) {
            System.out.println("文件已存在，无需操作: " + jsonFile.getAbsolutePath());
        } else {
            // 如果文件不存在，创建文件并写入初始内容
            if (jsonFile.createNewFile()) {
                try (FileWriter writer = new FileWriter(jsonFile)) {
                    writer.write("{\n");
                    writer.write("  \"name\": \"" + ledgerId + "\"\n");
                    writer.write("}\n");
                    System.out.println("文件创建成功: " + jsonFile.getAbsolutePath());
                }
            } else {
                throw new IOException("无法创建文件");
            }
        }
    }
}