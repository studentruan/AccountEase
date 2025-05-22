package com.myapp.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class LedgerManager {

    private List<Ledger> ledgerList;

    public LedgerManager() {
        this.ledgerList = new ArrayList<>();
        loadAllLedgers(); // 加载已有账本
    }

    public List<Ledger> getAllLedgers() {
        return ledgerList;
    }

    public void addLedger(Ledger ledger) {
        ledgerList.add(ledger);
        // 可在此处添加保存逻辑
    }

    public void loadAllLedgers() {
        // TODO: 实现从文件或数据库加载所有账本
        // 示例：ledgerList.add(....);
    }

    public void sortByCreationDateDescending() {
        ledgerList.sort(Comparator.comparing(Ledger::getCreationDate).reversed());
    }

    public void sortByCategory() {
        ledgerList.sort(Comparator.comparing(Ledger::getCategory));
    }
}
