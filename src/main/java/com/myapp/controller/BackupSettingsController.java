package com.myapp.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import java.io.*;
import java.nio.file.Files;
import java.util.prefs.Preferences;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class BackupSettingsController {

    @FXML private TextField backupPathField;
    @FXML private TextField restorePathField;

    private Preferences prefs = Preferences.userNodeForPackage(this.getClass());

    @FXML
    public void initialize() {
        // 加载上次使用的路径
        backupPathField.setText(prefs.get("LAST_BACKUP_PATH", ""));
        restorePathField.setText(prefs.get("LAST_RESTORE_PATH", ""));
    }

    @FXML
    private void handleChooseBackupPath() {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("选择备份保存位置");

        // 设置初始目录
        String lastPath = backupPathField.getText();
        if (!lastPath.isEmpty()) {
            chooser.setInitialDirectory(new File(lastPath));
        }

        File selectedDir = chooser.showDialog(null);
        if (selectedDir != null) {
            backupPathField.setText(selectedDir.getAbsolutePath());
            prefs.put("LAST_BACKUP_PATH", selectedDir.getAbsolutePath());
        }
    }

    @FXML
    private void handleBackup() {
        String backupDir = backupPathField.getText();
        if (backupDir.isEmpty()) {
            showAlert("错误", "请先选择备份保存位置");
            return;
        }

        try {
            // 创建备份文件
            String timestamp = java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            File backupFile = new File(backupDir, "settings_backup_" + timestamp + ".zip");

            // 实际备份逻辑（示例：备份配置文件）
            try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(backupFile))) {
                // 添加配置文件到压缩包
                File configFile = new File("config.properties");
                if (configFile.exists()) {
                    zos.putNextEntry(new ZipEntry(configFile.getName()));
                    Files.copy(configFile.toPath(), zos);
                    zos.closeEntry();
                }

                // 可以添加更多需要备份的文件...
            }

            showAlert("成功", "备份已保存到:\n" + backupFile.getAbsolutePath());
        } catch (Exception e) {
            showAlert("备份失败", "错误: " + e.getMessage());
        }
    }

    @FXML
    private void handleChooseRestoreFile() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("选择备份文件");
        chooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("ZIP备份文件", "*.zip"));

        // 设置初始目录
        String lastPath = restorePathField.getText();
        if (!lastPath.isEmpty()) {
            chooser.setInitialDirectory(new File(lastPath).getParentFile());
        }

        File selectedFile = chooser.showOpenDialog(null);
        if (selectedFile != null) {
            restorePathField.setText(selectedFile.getAbsolutePath());
            prefs.put("LAST_RESTORE_PATH", selectedFile.getAbsolutePath());
        }
    }

    @FXML
    private void handleRestore() {
        String backupFile = restorePathField.getText();
        if (backupFile.isEmpty()) {
            showAlert("错误", "请先选择备份文件");
            return;
        }

        try {
            // 实际恢复逻辑（示例）
            File file = new File(backupFile);
            if (!file.exists()) {
                showAlert("错误", "备份文件不存在");
                return;
            }

            // 这里应该添加实际的解压和恢复逻辑
            // 例如：ZipFile zip = new ZipFile(file);
            // 解压文件到配置目录...

            showAlert("成功", "设置已从备份恢复");
        } catch (Exception e) {
            showAlert("恢复失败", "错误: " + e.getMessage());
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}