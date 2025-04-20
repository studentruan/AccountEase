package com.myapp.model;

import java.io.File;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class Ledger {
    private String name;
    private String description;
    private File coverImage;
    private LocalDate creationDate;
    private String category;

    // 每一笔交易数据用 id 作为 key，值是 Transaction 对象
    private Map<String, Transaction> transactionData = new HashMap<>();

    public Ledger(String name, String description, File coverImage,
                  LocalDate creationDate, String category) {
        this.name = name;
        this.description = description;
        this.coverImage = coverImage;
        this.creationDate = creationDate;
        this.category = category;
    }

    // Getter methods
    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public File getCoverImage() {
        return coverImage;
    }

    public LocalDate getCreationDate() {
        return creationDate;
    }

    public String getCategory() {
        return category;
    }

    public Map<String, Transaction> getTransactionData() {
        return transactionData;
    }

    // Setter methods
    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setCoverImage(File coverImage) {
        this.coverImage = coverImage;
    }

    public void setCreationDate(LocalDate creationDate) {
        this.creationDate = creationDate;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setTransactionData(Map<String, Transaction> transactionData) {
        this.transactionData = transactionData;
    }

    // 添加一笔交易
    public void addTransaction(Transaction transaction) {
        transactionData.put(transaction.getId(), transaction);
    }

    // 根据 ID 获取交易
    public Transaction getTransactionById(String id) {
        return transactionData.get(id);
    }

    // 可选：toString 方法用于调试
    @Override
    public String toString() {
        return "Ledger{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", coverImage='" + coverImage + '\'' +
                ", creationDate=" + creationDate +
                ", category='" + category + '\'' +
                ", transactionData=" + transactionData +
                '}';
    }
}
