package com.myapp.model;

import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Ledger {
    private String name;
    private String description;
    private String coverImage;
    private LocalDate creationDate;
    private String category;
    private List<FinanceData> financeDataList;

    public Ledger(String name, String description, File coverImage) {
        this.name = name;
        this.description = description;
        this.coverImage = coverImage != null ? coverImage.getAbsolutePath() : null;
        this.creationDate = LocalDate.now();
        this.financeDataList = new ArrayList<>();
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCoverImage() {
        return coverImage;
    }

    public void setCoverImage(String coverImage) {
        this.coverImage = coverImage;
    }

    public LocalDate getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDate creationDate) {
        this.creationDate = creationDate;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public List<FinanceData> getFinanceDataList() {
        return financeDataList;
    }

    public void addFinanceData(FinanceData data) {
        financeDataList.add(data);
    }

    // 静态方法加载所有账本
    public static List<Ledger> loadAllLedgers() {
        // 实现从文件或数据库加载账本的逻辑
        return new ArrayList<>();
    }

    // 保存账本
    public void save() {
        // 实现保存账本的逻辑
    }
}