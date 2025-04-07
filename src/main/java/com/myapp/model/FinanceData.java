package com.myapp.model;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class FinanceData {
    private final SimpleDoubleProperty expense;
    private final SimpleDoubleProperty income;
    private final SimpleDoubleProperty balance;
    private final SimpleIntegerProperty budget;
    private final SimpleIntegerProperty pending;

    public FinanceData() {
        this.expense = new SimpleDoubleProperty(540);
        this.income = new SimpleDoubleProperty(640);
        this.balance = new SimpleDoubleProperty(100);
        this.budget = new SimpleIntegerProperty(1000);
        this.pending = new SimpleIntegerProperty(1900);
    }

    public double getExpense() {
        return expense.get();
    }

    public double getIncome() {
        return expense.get();
    }

    public double getBalance() {
        return expense.get();
    }
    public double getBudget() {
        return expense.get();
    }
    public double getPending() {
        return expense.get();
    }
    public double getClaimed() {
        return expense.get();
    }
    public double getReimbursement() {
        return expense.get();
    }
    public double getnNetAssets() {
        return expense.get();
    }
    public double getTotals() {
        return expense.get();
    }
    public double getDebts() {
        return expense.get();
    }


    public SimpleDoubleProperty expenseProperty() {
        return expense;
    }

    public void setExpense(double expense) {
        this.expense.set(expense);
    }

    // 其他getter和setter...
}