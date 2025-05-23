package com.myapp.controller;

import Backend.Ledger;
import Backend.LedgerManager;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class NewLedgerDialogController implements Initializable {

    @FXML
    private TextField nameField;

    @FXML
    private TextArea descriptionArea;

    @FXML private ImageView image_arrow;

    @FXML
    private ComboBox<String> storageLocationComboBox;
    @FXML
    private DatePicker creationDatePicker;

    @FXML
    private ComboBox<String> categoryComboBox;
    private Stage dialogStage;
    private boolean confirmed = false;
    private File coverImage;

    @Override
    public void initialize(URL location, ResourceBundle resources) {



        if (creationDatePicker != null) {
            creationDatePicker.setValue(LocalDate.now());
        } else {
            System.out.println("creationDatePicker is null");
        }
        categoryComboBox.getItems().addAll("生活", "工作", "旅行", "学习", "其他");
        categoryComboBox.setValue("其他");


        // 初始化存储位置下拉框
        storageLocationComboBox.getItems().addAll(
                "Local Storage",
                "Cloud Storage",
                "External Drive"
        );
        storageLocationComboBox.setValue("Local Storage");

        Image image_Back = new Image(getClass().getResource("/images/arrow.png").toExternalForm());
        image_arrow.setImage(image_Back);
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
        dialogStage.setResizable(false);
    }

    @FXML
    private void handleChooseImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("选择账本封面");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
        );
        coverImage = fileChooser.showOpenDialog(dialogStage);

        if (coverImage != null) {
            // 这里可以更新UI显示选中的图片名称
        }
    }



    @FXML
    private void handleCancel() {
        dialogStage.close();
    }

    public boolean isConfirmed() {
        return confirmed;
    }




    private boolean validateInput() {
        String name = nameField.getText().trim();
        if (name.isEmpty()) {
            showAlert("请输入账本名称");
            return false;
        }
        return true;
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("警告");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private static final String THIRD_DIR = "src/main/resources/thirdlevel_json/";

    @FXML
    private void handleConfirm() {
        try {
            if (validateEnhancedInput()) {
                // 生成唯一ID
                String ledgerId = UUID.randomUUID().toString();

                // 创建默认JSON结构
                JSONObject defaultJson = createDefaultJson(ledgerId);

                // 三级存储：保存初始化JSON文件
                saveDefaultJson(ledgerId, defaultJson);

                // 创建账本对象
                Ledger newLedger = createLedger(ledgerId);

                // 二级存储：保存账本详细信息
                newLedger.save();

                // 一级存储：更新账本列表
                updatePrimaryStorage(ledgerId);

                confirmed = true;
                dialogStage.close();
            }
        } catch (IOException e) {
            showErrorAlert("存储失败", "数据保存失败: " + e.getMessage());
        } catch (Exception e) {
            showErrorAlert("系统错误", "发生意外错误: " + e.getMessage());
        }
    }

    public JSONObject createDefaultJson(String ledgerId) {
        JSONObject json = new JSONObject();

        // 基础信息
        LocalDate now = LocalDate.now();
        json.put("账本ID", ledgerId);
        json.put("创建时间", now.format(DateTimeFormatter.ISO_DATE));

        // 初始化每日数据模板（全零值）
        YearMonth currentMonth = YearMonth.from(now);
        Map<LocalDate, JSONObject> dailyData = generateDailyTemplate(currentMonth);
        dailyData.forEach((date, data) ->
                json.put(date.format(DateTimeFormatter.ISO_DATE), data)
        );

        // 初始化月度聚合数据（全零值）
        JSONObject monthlyData = createMonthlyTemplate(currentMonth);
        json.put(currentMonth.format(DateTimeFormatter.ofPattern("yyyy-MM")), monthlyData);

        // 分类数据（全部设为"无"）
        initClassificationPlaceholders(json);

        // 系统元数据
        json.put("_metadata", createMetadata(now));

        // 纪念日（保留预设）
        json.put("所有的纪念日", new JSONArray()
                .put("01-01")
                .put("10-01")
        );

        // 初始化其他零值字段
        initZeroValueFields(json);

        return json;
    }

    private Map<LocalDate, JSONObject> generateDailyTemplate(YearMonth month) {
        Map<LocalDate, JSONObject> dailyMap = new LinkedHashMap<>();

        IntStream.rangeClosed(1, month.lengthOfMonth())
                .mapToObj(month::atDay)
                .forEach(date -> {
                    JSONObject daily = new JSONObject();
                    // 全部金额字段设为0
                    daily.put("日预算", 0.0);
                    daily.put("日已花预算", 0.0);
                    daily.put("日剩余预算", 0.0);
                    daily.put("日支出", 0.0);
                    daily.put("日收入", 0.0);
                    dailyMap.put(date, daily);
                });

        return dailyMap;
    }

    private JSONObject createMonthlyTemplate(YearMonth month) {
        JSONObject monthly = new JSONObject();
        // 全部金额字段设为0
        monthly.put("月预算", 0.0);
        monthly.put("月已花预算", 0.0);
        monthly.put("月剩余预算", 0.0);
        monthly.put("月收入", 0.0);
        monthly.put("月支出", 0.0);
        return monthly;
    }

    private void initClassificationPlaceholders(JSONObject json) {
        // 当天分类（4个"无"）
        JSONArray dailyCategories = new JSONArray();
        IntStream.range(0, 4).forEach(i ->
                dailyCategories.put(new JSONArray().put("无"))
        );
        json.put("当天前四个支出最多的种类", dailyCategories);

        // 当月分类（4个"无"）
        JSONArray monthlyCategories = new JSONArray()
                .put(new JSONArray()
                        .put("无")
                        .put("无")
                        .put("无")
                        .put("无"));
        json.put("当月前四个支出最多的种类", monthlyCategories);
    }

    private JSONObject createMetadata(LocalDate date) {
        JSONObject meta = new JSONObject();
        meta.put("生成时间", date.format(DateTimeFormatter.ISO_DATE));
        meta.put("交易总数", 0);
        return meta;
    }

    private void initZeroValueFields(JSONObject json) {
        // 其他所有金额字段设为0
        Arrays.asList("日总资产", "日剩余资产", "日总负债",
                        "月总资产", "月剩余资产", "月总负债",
                        "日报销", "日已报销", "日未报销",
                        "月报销", "月已报销", "月未报销")
                .forEach(field -> json.put(field, 0.0));
    }

    private double formatDouble(double value) {
        return Math.round(value * 100.0) / 100.0;
    }


    private void saveDefaultJson(String ledgerId, JSONObject json) throws IOException {
        Path dirPath = Paths.get(THIRD_DIR);
        Path filePath = dirPath.resolve(ledgerId + ".json");

        // 确保目录存在
        if (!Files.exists(dirPath)) {
            Files.createDirectories(dirPath);
        }

        // 格式化输出
        String prettyJson = json.toString(2)
                .replace(",\"", ",\n  ")
                .replace(":{", ": {\n  ");

        // 写入文件
        Files.writeString(filePath, prettyJson, StandardCharsets.UTF_8);
    }


    private Ledger createLedger(String ledgerId) throws IOException {
        Ledger ledger = new Ledger();
        ledger.setId(ledgerId);
        ledger.setName(nameField.getText().trim());
        ledger.setDescription(descriptionArea.getText().trim());
        ledger.setCoverImage(processCoverImage(ledgerId));
        ledger.setCreationTime(LocalDateTime.now());
        return ledger;
    }

    private File processCoverImage(String ledgerId) throws IOException {
        if (coverImage != null && coverImage.exists()) {
            Path imageDir = Path.of("resources/secondlevel_json/images");
            Files.createDirectories(imageDir);

            String fileExtension = getFileExtension(coverImage.getName());
            Path target = imageDir.resolve(ledgerId + fileExtension);
            Files.copy(coverImage.toPath(), target, StandardCopyOption.REPLACE_EXISTING);
            return target.toFile();
        }
        return null;
    }

    private void updatePrimaryStorage(String ledgerId) throws IOException {
        LedgerManager manager = new LedgerManager();
        manager.loadLedgerIdsFromFile();
        manager.addLedger(ledgerId);
    }

    private String getFileExtension(String fileName) {
        int dotIndex = fileName.lastIndexOf('.');
        return (dotIndex == -1) ? "" : fileName.substring(dotIndex);
    }

    private boolean validateEnhancedInput() {
        StringBuilder errors = new StringBuilder();

        // 名称验证（1-50字符）
        String name = nameField.getText().trim();
        if (name.isEmpty()) {
            errors.append("• 账本名称不能为空\n");
        } else if (name.length() > 50) {
            errors.append("• 名称长度不能超过50个字符\n");
        }

        // 描述验证（最多200字符）
        String description = descriptionArea.getText().trim();
        if (description.length() > 200) {
            errors.append("• 描述不能超过200个字符\n");
        }

        if (errors.length() > 0) {
            showErrorAlert("输入验证失败", errors.toString());
            return false;
        }
        return true;
    }

    private void showErrorAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}