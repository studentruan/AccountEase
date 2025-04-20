package com.myapp.controller;

import com.myapp.model.TransactionLoader;
import com.myapp.model.Transactions;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;

import java.io.*;
import java.util.Map;
import java.util.prefs.Preferences;

public class InputSettingsController {

    @FXML
    private TextField filePathField;

    // 文件选择逻辑
    @FXML
    private void handleBrowseFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("选择账单文件");

        // 设置文件过滤器
        FileChooser.ExtensionFilter excelFilter = new FileChooser.ExtensionFilter(
                "电子表格文件 (*.xlsx, *.csv)", "*.xlsx", "*.csv", "*.xml");
        fileChooser.getExtensionFilters().add(excelFilter);

        File selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {
            filePathField.setText(selectedFile.getAbsolutePath());
        }
    }

    // 保存路径逻辑
    @FXML
    private void saveFilePath() {
        String filePath = filePathField.getText();
        if (filePath != null && !filePath.isEmpty()) {
            try {
                // 获取用户选择的文件
                File selectedFile = new File(filePath);

                // 检查文件是否存在
                if (selectedFile.exists() && selectedFile.isFile()) {
                    // 指定保存文件的路径
                    File saveFile = new File("src/main/resources/xml/test.xml");

                    // 如果目标文件不存在，则创建它
                    if (!saveFile.exists()) {
                        saveFile.createNewFile();
                    }

                    // 使用 FileInputStream 读取原文件内容
                    FileInputStream inputStream = new FileInputStream(selectedFile);

                    // 使用 FileOutputStream 写入目标文件
                    FileOutputStream outputStream = new FileOutputStream(saveFile);

                    // 创建缓冲区读取和写入文件
                    byte[] buffer = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, bytesRead);
                    }

                    // 关闭流
                    inputStream.close();
                    outputStream.close();

                    TransactionLoader loade = new TransactionLoader();
                    loade.loadTransactionsFromXml("src/main/output/classified_transactions1.xml");
                    Map<String, Transactions> data = loade.getTransactionData();
                    // 提示用户保存成功
                    System.out.println("文件已成功保存到: " + saveFile.getAbsolutePath());
                } else {
                    System.err.println("选择的文件无效或不存在");
                }
            } catch (IOException e) {
                e.printStackTrace();
                System.err.println("保存文件失败");
            }
        } else {
            System.err.println("请选择一个有效的文件路径");
        }


    }

}