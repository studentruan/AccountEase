package com.myapp.model;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import java.time.LocalDate;
import java.util.*;

public class FinanceData {
    private final SimpleDoubleProperty expense;
    private final SimpleDoubleProperty income;
    private final SimpleDoubleProperty balance;
    private final SimpleIntegerProperty budget;
    private final SimpleIntegerProperty pending;
    private Ledger ledger; // Ledger 对象



    public FinanceData(Ledger ledger) {
        this.expense = new SimpleDoubleProperty(1900);
        this.income = new SimpleDoubleProperty(2311);
        this.balance = new SimpleDoubleProperty(210);
        this.budget = new SimpleIntegerProperty(132);
        this.pending = new SimpleIntegerProperty(2331);
        this.ledger = ledger;
    }

    public double getExpense() {
        return expense.get();
    }

    public double getIncome() {
        return income.get();
    }

    public double getBalance() {
        return balance.get();
    }

    public int getBudget() {
        return budget.get();
    }

    public int getPending() {
        return pending.get();
    }

    public double getClaimed() {
        // This seems to be a placeholder for the claimed amount, you may want to add a specific property for it.
        return 0;
    }

    public double getReimbursement() {
        // This seems to be a placeholder for reimbursement, you may want to add a specific property for it.
        return 0;
    }

    public double getnNetAssets() {
        // This seems to be a placeholder for net assets, you may want to add a specific property for it.
        return 0;
    }

    public double getTotals() {
        // This seems to be a placeholder for total, you may want to add a specific property for it.
        return 0;
    }

    public double getDebts() {
        // This seems to be a placeholder for debts, you may want to add a specific property for it.
        return 0;
    }

    public SimpleDoubleProperty expenseProperty() {
        return expense;
    }

    public void setExpense(double expense) {
        this.expense.set(expense);
    }

    public SimpleDoubleProperty incomeProperty() {
        return income;
    }

    public void setIncome(double income) {
        this.income.set(income);
    }

    public SimpleDoubleProperty balanceProperty() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance.set(balance);
    }

    public SimpleIntegerProperty budgetProperty() {
        return budget;
    }

    public void setBudget(int budget) {
        this.budget.set(budget);
    }

    public SimpleIntegerProperty pendingProperty() {
        return pending;
    }

    public void setPending(int pending) {
        this.pending.set(pending);
    }

    // 获取每个月的支出数据
    public Map<String, Double> getMonthlyExpenses() {
        Map<String, Double> monthlyExpenses = new HashMap<>();
        Map<String, Transactions> transactions = ledger.getTransactionData(); // 获取交易数据

        // 打印交易数据的大小和内容
        System.out.println("Total transactions: " + transactions.size());
        for (Map.Entry<String, Transactions> entry : transactions.entrySet()) {
            String transactionId = entry.getKey();
            Transactions transaction = entry.getValue();
            System.out.println("Transaction ID: " + transactionId + ", Type: " + transaction.getType() +
                    ", Date: " + transaction.getDate() + ", Amount: " + transaction.getAmount());
        }

        for (Transactions transaction : transactions.values()) {
            if ("Expense".equals(transaction.getType())) {
                // 获取交易的月份和年份
                String monthYear = transaction.getDate().getMonthValue() + "-" + transaction.getDate().getYear();

                // 打印每次处理的月份和支出金额
                System.out.println("Processing expense - Month/Year: " + monthYear +
                        ", Current Amount: " + transaction.getAmount() +
                        ", Total for Month: " +
                        (monthlyExpenses.getOrDefault(monthYear, 0.0) + transaction.getAmount()));

                monthlyExpenses.put(monthYear, monthlyExpenses.getOrDefault(monthYear, 0.0) + transaction.getAmount());
            }
        }

        // 打印最终的每月支出数据
        System.out.println("Monthly Expenses:");
        for (Map.Entry<String, Double> entry : monthlyExpenses.entrySet()) {
            System.out.println("Month/Year: " + entry.getKey() + ", Total Amount: " + entry.getValue());
        }

        return monthlyExpenses;
    }


    // 获取每个支出类别的金额数据
    public Map<String, Double> getExpenseCategories() {
        Map<String, Double> expenseCategories = new HashMap<>();
        Map<String, Transactions> transactions = ledger.getTransactionData(); // 获取交易数据

        // 打印交易数据的大小和内容
        System.out.println("Total transactions: " + transactions.size());
        for (Map.Entry<String, Transactions> entry : transactions.entrySet()) {
            String transactionId = entry.getKey();
            Transactions transaction = entry.getValue();
            System.out.println("Transaction ID: " + transactionId + ", Type: " + transaction.getType() +
                    ", Category: " + transaction.getClassification() + ", Amount: " + transaction.getAmount());
        }

        for (Transactions transaction : transactions.values()) {
            if ("Expense".equals(transaction.getType())) {
                String category = transaction.getClassification();
                // 打印每次添加/更新的类别和金额
                System.out.println("Processing expense - Category: " + category +
                        ", Current Amount: " + transaction.getAmount() +
                        ", Total for Category: " +
                        (expenseCategories.getOrDefault(category, 0.0) + transaction.getAmount()));

                expenseCategories.put(category, expenseCategories.getOrDefault(category, 0.0) + transaction.getAmount());
            }
        }

        // 打印最终的支出类别数据
        System.out.println("Expense Categories:");
        for (Map.Entry<String, Double> entry : expenseCategories.entrySet()) {
            System.out.println("Category: " + entry.getKey() + ", Total Amount: " + entry.getValue());
        }

        return expenseCategories;
    }


}
